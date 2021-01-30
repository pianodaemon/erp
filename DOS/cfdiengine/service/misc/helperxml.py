import os


class HelperXml(object):

    @staticmethod
    def run_xslt(xml_filename, xsl_filename):
        """
        An xml wrapper to xslt proc
        """
        import subprocess
        from misc.localexec import LocalExec
        from distutils.spawn import find_executable

        def seekout_xsltproc():
            XSLTPROC_BIN = 'xsltproc'
            executable = find_executable(XSLTPROC_BIN)
            if executable:
                return os.path.abspath(executable)
            raise SignerError("it has not found {} binary".format(XSLTPROC_BIN))

        le = LocalExec(err_mute=True)
        exe = seekout_xsltproc()
        exe_args = [ xsl_filename, xml_filename ]

        try:
            return le([exe] + exe_args, cmd_timeout=20, ign_rcs=None)
        except subprocess.CalledProcessError as e:
            msg = "Command raised exception\nOutput: " + str(e.output)
        raise Exception(msg)
