import os
import time
import subprocess


class LocalExec(object):

    def __init__(self, err_mute=False):
        self.__err_mute = err_mute

    def __call__(self, cmd_tokens, cmd_timeout, ign_rcs):
        """Execute a command on local machine."""

        def time_gap(delta):
            t = time.time()
            return t, t + delta

        def monitor(p, tbegin, tend):
            """Loop until process returns or timeout expires"""
            rc = None
            output = ''
            while time.time() < tend and rc is None:
                rc = p.poll()
                if rc is None:
                    try:
                        outs, errs = p.communicate(timeout=1)
                        output += outs
                    except subprocess.TimeoutExpired:
                        pass
            return output, rc

        if self.__err_mute:
            out_err = subprocess.DEVNULL
        else:
            out_err = subprocess.STDOUT

        output, rc = monitor(
            subprocess.Popen(
                cmd_tokens,
                universal_newlines=True,
                stdout=subprocess.PIPE,
                stderr= out_err
            ),
            *time_gap(cmd_timeout)
        )

        if rc is None:
            raise subprocess.TimeoutExpired(
                cmd=cmd_tokens,
                output=output,
                timeout=cmd_timeout
            )

        if ign_rcs is None:
            ign_rcs = []

        if rc in ign_rcs or rc == 0:
            return output

        raise subprocess.CalledProcessError(
            returncode=rc,
            cmd=cmd_tokens,
            output=output
        )
