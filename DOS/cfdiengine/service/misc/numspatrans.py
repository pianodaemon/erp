def __tp_mode(n, prefix, llimit, rlimit, func_trans, flag, mode):
    if (n >= llimit) and (n <= rlimit):
        t = prefix + ' '
        if n > llimit:
            try:
                return {
                    1: lambda: (True, "{0}{1}".format(t, func_trans(n - llimit , flag))),
                    2: lambda: (True, "{0}y {1}".format(t, func_trans(n - llimit , flag)))
                }[mode]()
            except KeyError:
                raise Exception("translation mode invalid")
        else:
            return (True, t)
    else:
        return (False, None)


def __tp_phrase(n, prefix, llimit, rlimit, func_trans, flag):
    return __tp_mode(n, prefix, llimit, rlimit, func_trans, flag, 1)


def __tp_phrase_y(n, prefix, llimit, rlimit, func_trans, flag):
    return __tp_mode(n, prefix, llimit, rlimit, func_trans, flag, 2)


def __transdec0(n, flag = False):
    """The most fundamental unit translation"""
    try:
        return {
            9: lambda: "nueve",
            8: lambda: "ocho",
            7: lambda: "siete",
            6: lambda: "seis",
            5: lambda: "cinco",
            4: lambda: "cuatro",
            3: lambda: "tres",
            2: lambda: "dos",
            1: lambda: "uno" if flag == False else "un",
            0: lambda: "cero"
        }[n]()
    except KeyError:
        raise Exception("The input is not less than a 10")


def __transdec1(n, flag = False):
    __FACTOR = 10
    fti = __transdec0

    select = {
        3: lambda: "treinta",
        4: lambda: "cuarenta",
        5: lambda: "cincuenta",
        6: lambda: "sesenta",
        7: lambda: "setenta",
        8: lambda: "ochenta",
        9: lambda: "noventa"
    }

    for i in range(3,10):
        ll = (i * __FACTOR)
        rl = (i * __FACTOR) + (__FACTOR - 1)
        (hit, phrase) = __tp_phrase_y(n, select[i](), ll, rl, fti, flag)
        if hit:
            return phrase

    if n >= (2 * __FACTOR) and n <= (2 * __FACTOR + __FACTOR - 1):
        return "veinte " if n == (2 * __FACTOR) else "veinti{0}".format(
            fti(n - (2 * __FACTOR) , flag)
        )

    switcher = {
        10: lambda: "diez ",
        11: lambda: "once ",
        12: lambda: "doce ",
        13: lambda: "trece ",
        14: lambda: "catorce ",
        15: lambda: "quince ",
        16: lambda: "dieciseis ",
        17: lambda: "diecisiete ",
        18: lambda: "dieciocho ",
        19: lambda: "diecinueve "
    }

    return switcher.get(n, lambda: fti(n, flag))()


def __transdec2(n, flag = False):
    __FACTOR = 100
    fti = __transdec1

    switcher = {
        2: lambda: "doscientos",
        3: lambda: "trescientos",
        4: lambda: "cuatrocientos",
        5: lambda: "quinientos",
        6: lambda: "seiscientos",
        7: lambda: "setecientos",
        8: lambda: "ochocientos",
        9: lambda: "novecientos"
    }

    for i in range(2,10):
        ll = (i * __FACTOR)
        rl = (i * __FACTOR) + (__FACTOR - 1)
        (hit, phrase) = __tp_phrase(n, switcher[i](), ll, rl, fti, flag)
        if hit:
            return phrase

    if n >= __FACTOR and n <= (__FACTOR + __FACTOR - 1):
        return "cien " if n == __FACTOR else "ciento {0}".format(
            fti(n - __FACTOR , flag)
        )

    return fti(n, flag)


def __transdec3(n, flag = False):
    __FACTOR = 1000
    fti = __transdec2

    (hit, phrase) = __tp_phrase(
        n, "mil", __FACTOR, (__FACTOR + __FACTOR - 1), fti, flag
    )
    if hit:
        return phrase

    for i in range(2,10):
        ll = (i * __FACTOR)
        rl = (i * __FACTOR) + (__FACTOR - 1)
        (hit, phrase) = __tp_phrase(
            n, "%s %s" % (__transdec0(i) , "mil"), ll, rl, fti, True
        )
        if hit:
            return phrase

    return fti(n, flag)


def __transdec4(n, flag = False):
    __STEP = 1000

    for i in range(10,100):
        ll = (i * __STEP)
        rl = (i * __STEP) + (__STEP - 1)
        (hit, phrase) = __tp_phrase(
            n, "%s %s" % (__transdec1(i, True) , "mil"), ll, rl, __transdec2, True
        )
        if hit:
            return phrase

    return __transdec3(n, flag)


def __transdec5(n, flag = False):
    __STEP = 1000

    for i in range(100,1000):
        ll = (i * __STEP)
        rl = (i * __STEP) + (__STEP - 1)
        (hit, phrase) = __tp_phrase(
            n, "%s %s" % (__transdec2(i, True) , "mil"), ll, rl, __transdec2, True
        )
        if hit:
            return phrase

    return __transdec4(n, flag)


def __transdec6(n, flag = False):
    __FACTOR = 1000000
    fti = __transdec5
    (hit, phrase) = __tp_phrase(
        n, "un millon", __FACTOR, (__FACTOR + __FACTOR - 1), fti, True
    )
    if hit:
        return phrase

    for i in range(2,10):
        ll = (i * __FACTOR)
        rl = (i * __FACTOR) + (__FACTOR - 1)
        (hit, phrase) = __tp_phrase(
            n, "%s %s" % (__transdec0(i) , "millones"), ll, rl, fti, True
        )
        if hit:
            return phrase

    return __transdec5(n, flag)


def numspatrans(n):
    """
    translates int to spanish phrase
    """
    try:
       return __transdec6(int(n))
    except ValueError:
       raise
