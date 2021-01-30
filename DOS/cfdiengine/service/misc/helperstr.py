import os
import sys
import random
import string
import re
import enum


class UMT(enum.Enum):
    """
    Unix terminal format codes
    """
    NORMAL        = '\033[0m'
    BOLD          = '\033[1m'
    DIM           = '\033[2m'
    UNDERLINE     = '\033[4m'
    REVERSE       = '\033[7m'
    STRIKETHROUGH = '\033[9m'
    RED           = '\033[31m'
    YELLOW        = '\033[33m'


class HelperStr(object):

    @staticmethod
    def erase_bom(path):
        import codecs

        BUFSIZE = 4096
        chunk = None

        def takeout(l, f, c):
            i = 0
            c = c[l:]
            while c:
                f.seek(i)
                f.write(c)
                i += len(c)
                f.seek(l, os.SEEK_CUR)
                c = f.read(BUFSIZE)
            f.seek(-l, os.SEEK_CUR)
            f.truncate()

        with open(path, "r+b") as p:
            chunk = p.read(BUFSIZE)
            if chunk.startswith(codecs.BOM_UTF8):
                takeout(len(codecs.BOM_UTF8), p, chunk)
            if chunk.startswith(codecs.BOM_UTF32_BE):
                takeout(len(codecs.BOM_UTF32_BE), p, chunk)
            if chunk.startswith(codecs.BOM_UTF32_LE):
                takeout(len(codecs.BOM_UTF32_LE), p, chunk)
            if chunk.startswith(codecs.BOM_UTF16_BE):
                takeout(len(codecs.BOM_UTF16_BE), p, chunk)
            if chunk.startswith(codecs.BOM_UTF16_LE):
                takeout(len(codecs.BOM_UTF16_LE), p, chunk)

    @staticmethod
    def format_currency(amount):
        """format as currency an string amount"""

        def makeup_intseg(int_seg):
            mut = []
            for idx , c in enumerate(reversed(int_seg)):
                mut.append(c)
                if ((idx + 1) % 3) == 0:
                    mut.append(',')
            if mut[-1] == ',':
                del mut[-1]
            return ''.join(reversed(mut))

        m = re.match("^\d+(\.\d{1,2})?$", amount)
        if m:
            if amount.find(".") == -1:
                 return "{0}.{1:0<2}".format(
                     makeup_intseg(amount), "0"
                 )
            else:
                 int_seg , decimal_seg = amount.split('.', 1)
                 return "{0}.{1:0<2}".format(
                     makeup_intseg(int_seg), decimal_seg
                 )
        else:
            raise Exception("input parameter is not an amount string")

    @staticmethod
    def random_str(size=8):
        """generates random string as per size"""
        return ''.join(
            random.SystemRandom().choice(
                string.ascii_uppercase + string.digits
            ) for _ in range(size)
        )

    @staticmethod
    def edit_pattern(pattern, replace, source, dest=None):
        """Reads a source file and writes the destination file.

        In each line, replaces pattern with replace.

        Args:
            pattern (str): pattern to match (can be re.pattern)
            replace (str): replacement str
            source  (str): input filename
            dest (str):   destination filename, if not given, source will be over written.
        """

        import shutil
        from tempfile import mkstemp

        fout = None
        fin = open(source, 'r', encoding="utf-8")

        if dest is not None:
            fout = open(dest, 'w', encoding="utf-8")
        else:
            fd, name = mkstemp()
            os.close(fd)
            fout = open(name, 'w', encoding="utf-8")

        for line in fin:
            out = re.sub(pattern, replace, line)
            fout.write(out)
        try:
            fout.writelines(fin.readlines())
        except Exception as E:
            raise E

        fin.close()
        fout.close()

        if dest is None:
            shutil.move(name, source)
