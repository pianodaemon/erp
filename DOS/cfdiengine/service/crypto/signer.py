from misc.helperstr import HelperStr
from misc.localexec import LocalExec
from distutils.spawn import find_executable
import subprocess
import tempfile
import os


class SignerError(Exception):
    def __init__(self, message=None):
        self.message = message

    def __str__(self):
        return self.message


class Signer(object):
    """
    """
    __SSL_BIN = "openssl"
    __SIZE_RANDOM_STR = 8
    __SUPPORTED = ['sha1', 'sha256']

    SHA1, SHA256 = range(2)

    def __init__(self, cipher, pem_pubkey, pem_privkey):

        # You must first extract the public key from the certificate:
        # openssl x509 -pubkey -noout -in cert.pem > pubkey.pem
        # then use the key to verify the signature:
        # openssl dgst -verify pubkey.pem -signature sigfile datafile

        def seekout_openssl():
            executable = find_executable(self.__SSL_BIN)
            if executable:
                return os.path.abspath(executable)
            raise SignerError("it has not found {} binary".format(self.__SSL_BIN))

        self.le = LocalExec()
        self.cipher = self.__SUPPORTED[cipher]
        self.pem_pubkey = pem_pubkey
        self.pem_privkey = pem_privkey
        self.ssl_bin = seekout_openssl()

    def verify(self, signature, str2verify):
        """verifies base64 string with a public key"""

        tmp_dir = tempfile.gettempdir()
        decoded_f = '{}/{}'.format(tmp_dir, HelperStr.random_str(self.__SIZE_RANDOM_STR))
        signature_f = '{}/{}'.format(tmp_dir, HelperStr.random_str(self.__SIZE_RANDOM_STR))
        verify_f = '{}/{}'.format(tmp_dir, HelperStr.random_str(self.__SIZE_RANDOM_STR))

        self.__touch(signature_f)
        self.__touch(verify_f)

        with open(signature_f, 'a') as sf:
            sf.write(signature)

        with open(verify_f, 'a') as vf:
            vf.write(str2verify)

        # When stripping \n characters and presenting that as one
        # single line you need the -A option.
        base64_args = [
            'base64',
            '-A',
            '-d',
            '-in',
            signature_f,
            '-out',
            decoded_f
        ]

        dgst_args = [
            'dgst',
            '-{}'.format(self.cipher),
            '-verify',
            self.pem_pubkey,
            '-signature',
            decoded_f,
            verify_f
        ]

        try:
            self.le([self.ssl_bin] + base64_args, cmd_timeout=10, ign_rcs=None)
            self.le([self.ssl_bin] + dgst_args, cmd_timeout=10, ign_rcs=None)
        except subprocess.CalledProcessError as e:
            msg = "Command raised exception\nOutput: " + str(e.output)
            raise SignerError(msg)

        os.remove(decoded_f)
        os.remove(signature_f)
        os.remove(verify_f)

    def __fetch_result(self, path):
        rs = None
        statinfo = os.stat(path)
        if statinfo.st_size > 0:
            rs = ''
            with open(path, 'r') as rf:
                for line in rf:
                    rs = rs + line.replace("\n", "")
        if rs is None:
            SignerError("Unexpected ssl output!!!")
        return rs

    def __touch(self, path):
        with open(path, 'a'):
            os.utime(path, None)

    def sign(self, str2sign):
        """signs an string and returns base64 string"""
        tmp_dir = tempfile.gettempdir()
        sealbin_f = '{}/{}'.format(tmp_dir, HelperStr.random_str(self.__SIZE_RANDOM_STR))
        input_f = '{}/{}'.format(tmp_dir, HelperStr.random_str(self.__SIZE_RANDOM_STR))
        result_f = '{}/{}'.format(tmp_dir, HelperStr.random_str(self.__SIZE_RANDOM_STR))

        self.__touch(input_f)

        with open(input_f, 'r+b') as cf:
            cf.write(str2sign.encode("utf-8-sig"))

        HelperStr.erase_bom(input_f)

        dgst_args = [
            'dgst',
            '-{}'.format(self.cipher),
            '-sign',
            self.pem_privkey,
            '-out',
            sealbin_f,
            input_f
        ]

        base64_args = [
            'base64',
            '-in',
            sealbin_f,
            '-A',
            '-out',
            result_f
        ]

        try:
            self.le([self.ssl_bin] + dgst_args, cmd_timeout=10, ign_rcs=None)
            self.le([self.ssl_bin] + base64_args, cmd_timeout=10, ign_rcs=None)
        except subprocess.CalledProcessError as e:
            msg = "Command raised exception\nOutput: " + str(e.output)
            raise SignerError(msg)

        rs = self.__fetch_result(result_f)

        os.remove(sealbin_f)
        os.remove(input_f)
        os.remove(result_f)

        return rs
