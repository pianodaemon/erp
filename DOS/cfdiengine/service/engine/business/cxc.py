import tempfile
import os
from engine.error import ErrorCode
from misc.helperpg import HelperPg
from engine.common import fetch_rdirs
from pac.connector import setup_pac
from misc.tricks import dump_exception
from custom.profile import ProfileReader
from sat.reader import SaxReader
from docmaker.pipeline import DocPipeLine
from misc.helperstr import HelperStr


def __get_emisor_rfc(logger, usr_id):

    q = """select upper(EMP.rfc) as rfc
        FROM gral_suc AS SUC
        LEFT JOIN gral_usr_suc AS USR_SUC ON USR_SUC.gral_suc_id = SUC.id
        LEFT JOIN gral_emp AS EMP ON EMP.id = SUC.empresa_id
        LEFT JOIN cfdi_regimenes AS REG ON REG.numero_control = EMP.regimen_fiscal
        WHERE USR_SUC.gral_usr_id = {}""".format(usr_id)

    logger.debug("Performing query: {}".format(q))
    for row in HelperPg.onfly_query(q, True):
        # Just taking first row of query result
        return row['rfc']


def __run_builder(logger, pt, f_outdoc, resdir, dm_builder, **kwargs):
    try:
        dpl = DocPipeLine(logger, resdir, rdirs_conf=pt.res.dirs)
        dpl.run(dm_builder, f_outdoc, **kwargs)
        return ErrorCode.SUCCESS
    except:
        logger.error(dump_exception())
        return ErrorCode.DOCMAKER_ERROR


def __run_sp_ra(logger, q, pgsql_conf, tmode = True):
    """Runs a store procedure with rich answer"""

    def run_store():
        logger.debug("Performing query: {}".format(q))
        r = HelperPg.onfly_query(pgsql_conf, q, True)

        # For this case we are just expecting one row
        if len(r) != 1:
            raise Exception('unexpected result regarding execution of store')
        return r

    def check_result(r):
        rcode, rmsg = r.pop()
        if rcode != 0:
            raise Exception(rmsg)

    _res = None

    try:
        _res = run_store()
    except:
        logger.error(dump_exception())
        return ErrorCode.DBMS_SQL_ISSUES

    try:
        check_result(_res)
    except:
        logger.error(dump_exception())
        if tmode:
            return ErrorCode.DBMS_TRANS_ERROR
        else:
            return ErrorCode.REQUEST_INVALID

    return ErrorCode.SUCCESS


def __pac_sign(logger, f_xmlin, xid, out_dir, pac_conf):
    """
    Signs xml with pac connector mechanism
    """
    try:
        logger.debug('Getting a pac connector as per config profile')
        pac, err = setup_pac(logger, pac_conf)
        if pac is None:
            raise Exception(err)

        logger.debug('File to sign {}'.format(f_xmlin))

        s_signed = None
        with open(f_xmlin) as f:
            s_signed = pac.stamp(f.read(), xid)
            logger.debug(s_signed)

        f_xmlout = os.path.join(out_dir, xid)
        logger.debug('saving pac xml signed upon {}'.format(f_xmlout))
        with open(f_xmlout, "w") as f:
            f.write(s_signed)

        return ErrorCode.SUCCESS, f_xmlout
    except:
        logger.error(dump_exception())
        return ErrorCode.THIRD_PARTY_ISSUES, None


def __pac_cancel(logger, t, rfc, pac_conf):
    try:
        logger.debug('Getting a pac connector as per config profile')
        pac, err = setup_pac(logger, pac_conf)
        if pac is None:
            raise Exception(err)

        s_cancel = pac.cancel(t, rfc)
        logger.debug(s_cancel)

        return ErrorCode.SUCCESS
    except:
        logger.error(dump_exception())
        return ErrorCode.THIRD_PARTY_ISSUES


def dopago(logger, pt, req):

    logger.info("stepping in dopago handler within {}".format(__name__))

    filename = req.get('filename', None)
    usr_id = req.get('usr_id', None)
    pag_id = req.get('pag_id', None)

    if (pag_id is None) or (usr_id is None) or (filename is None):
        return ErrorCode.REQUEST_INCOMPLETE.value


    source = ProfileReader.get_content(pt.source, ProfileReader.PNODE_UNIQUE)
    resdir = os.path.abspath(os.path.join(os.path.dirname(source), os.pardir))
    rdirs = fetch_rdirs(resdir, pt.res.dirs)

    tmp_dir = tempfile.gettempdir()
    tmp_file = os.path.join(tmp_dir, HelperStr.random_str())

    def update_filename():
        q = """UPDATE erp_pagos set aux_no_fac = '{}'
            WHERE erp_pagos.numero_transaccion = {}""".format(filename.replace('.xml', ''), pag_id)
        try:
            HelperPg.onfly_update(pt.dbms.pgsql_conn, q)
        except:
            logger.error(dump_exception())
            return ErrorCode.DBMS_SQL_ISSUES
        return ErrorCode.SUCCESS

    def update_consecutive_alpha(f_xmlin):
        parser = SaxReader()
        xml_dat, _ = parser(f_xmlin)

        q = """update fac_cfds_conf_folios  set folio_actual = (folio_actual + 1)
            FROM gral_suc AS SUC
            LEFT JOIN fac_cfds_conf ON fac_cfds_conf.gral_suc_id = SUC.id
            LEFT JOIN gral_usr_suc AS USR_SUC ON USR_SUC.gral_suc_id = SUC.id
            WHERE fac_cfds_conf_folios.proposito = 'PAG'
            AND fac_cfds_conf_folios.fac_cfds_conf_id=fac_cfds_conf.id
            AND USR_SUC.gral_usr_id = {}""".format(usr_id)
        try:
            HelperPg.onfly_update(pt.dbms.pgsql_conn, q)
        except:
            logger.error(dump_exception())
            return ErrorCode.DBMS_SQL_ISSUES
        return ErrorCode.SUCCESS

    rc = __run_builder(logger, pt, tmp_file, resdir,
            'pagxml', usr_id = usr_id, pag_id = pag_id)

    if rc != ErrorCode.SUCCESS:
        pass
    else:
        _rfc = None

        try:
            _rfc = __get_emisor_rfc(logger, req.get('usr_id', None),
                    pt.dbms.pgsql_conn)
        except:
            rc = ErrorCode.DBMS_SQL_ISSUES

        if rc == ErrorCode.SUCCESS:
            out_dir = os.path.join(rdirs['cfdi_output'], _rfc)
            rc, signed_file = __pac_sign(logger, tmp_file, filename,
                                         out_dir, pt.tparty.pac)
        if rc == ErrorCode.SUCCESS:
            rc = update_filename()

        if rc == ErrorCode.SUCCESS:
            rc = update_consecutive_alpha(signed_file)
            if rc == ErrorCode.SUCCESS:
                rc = __run_builder(logger, pt,
                    signed_file.replace('.xml', '.pdf'),
                    resdir, 'pagpdf', xml = signed_file, rfc = _rfc)

    if os.path.isfile(tmp_file):
        os.remove(tmp_file)

    return rc.value


def undofacturar(logger, pt, req):

    fact_id = req.get('fact_id', None)
    usr_id = req.get('usr_id', None)
    reason = req.get('reason', None)
    mode = req.get('mode', None)

    if reason is None:
        reason = ''

    if (fact_id is None) or (usr_id is None) or (mode is None):
        return ErrorCode.REQUEST_INCOMPLETE.value

    q_val = """select * from fac_val_cancel( {}::integer )
        AS result( rc integer, msg text )
        """.format(   # Store procedure parameters
            fact_id   #  _fac_id
    )

    q_do = """select * from fac_exec_cancel(
        {}::integer,
        {}::integer,
        '{}'::text,
        {}::integer
        ) AS result( rc integer, msg text )
        """.format(   # Store procedure parameters
        usr_id,       #  _usr_id
        fact_id,      #  _fac_id
        reason,       #  _reason
        mode          #  _mode
    )

    def get_xml_name():
        q = """select ref_id as filename
            FROM fac_docs
            WHERE fac_docs.id="""

        for row in HelperPg.onfly_query(pt.dbms.pgsql_conn, "{0}{1}".format(q, fact_id), True):
            # Just taking first row of query result
            return row['filename'] + '.xml'

    source = ProfileReader.get_content(pt.source, ProfileReader.PNODE_UNIQUE)
    resdir = os.path.abspath(os.path.join(os.path.dirname(source), os.pardir))
    rdirs = fetch_rdirs(resdir, pt.res.dirs)

    _uuid = None
    _res = None
    _rfc = None

    try:
        _rfc = __get_emisor_rfc(logger, usr_id, pt.dbms.pgsql_conn)
    except:
        return ErrorCode.DBMS_SQL_ISSUES.value

    try:
        cfdi_dir = os.path.join(rdirs['cfdi_output'], _rfc)
        f_xml = os.path.join(cfdi_dir, get_xml_name())
        logger.debug('File to cancel {}'.format(f_xml))
        parser = SaxReader()
        xml_dat, _ = parser(f_xml)
        _uuid = xml_dat['UUID']
    except:
        return ErrorCode.RESOURCE_NOT_FOUND.value

    rc = __run_sp_ra(logger, q_val, pt.dbms.pgsql_conn, tmode = False)
    if rc != ErrorCode.SUCCESS:
        return rc.value

    rc = __pac_cancel(logger, _uuid, _rfc, pt.tparty.pac)
    if rc != ErrorCode.SUCCESS:
        return rc.value

    rc = __run_sp_ra(logger, q_do, pt.dbms.pgsql_conn)
    return rc.value


def facturar(logger, pt, req):

    def fetch_empdat(usr_id):
        sql = """select upper(EMP.rfc) as rfc, EMP.no_id as no_id
            FROM gral_suc AS SUC
            LEFT JOIN gral_usr_suc AS USR_SUC ON USR_SUC.gral_suc_id = SUC.id
            LEFT JOIN gral_emp AS EMP ON EMP.id = SUC.empresa_id
            WHERE USR_SUC.gral_usr_id="""
        q = "{0}{1}".format(sql, usr_id)
        logger.debug("Performing query: {}".format(q))
        try:
            for row in HelperPg.onfly_query(pt.dbms.pgsql_conn, q):
                return ErrorCode.SUCCESS, dict(rfc=row['rfc'], no_id=row['no_id'])
        except:
            logger.error(dump_exception())
            return ErrorCode.DBMS_SQL_ISSUES, None

    def store(f_xmlin, usr_id, prefact_id, no_id):
        parser = SaxReader()
        xml_dat, _ = parser(f_xmlin)
        ref_id = '{}_{}{}'.format(no_id, xml_dat['CFDI_SERIE'], xml_dat['CFDI_FOLIO'])
        q = """select fac_save_xml from fac_save_xml(
            '{}'::character varying, {}::integer, {}::integer, '{}'::character varying,
            '{}'::character varying, '{}'::character varying, '{}'::character varying,
            '{}'::character varying, '{}'::character varying, '{}'::character varying,
            '{}'::character varying, '{}'::character varying, '{}'::character varying,
            '{}'::character varying, '{}'::character varying, '{}'::character varying,
            '{}'::character varying, '{}'::character varying, '{}'::character varying,
             {}::double precision, {}::double precision, {}::double precision, {}::boolean,
            '{}'::character varying
        )""".format(                             # Store procedure parameters
            os.path.basename(f_xmlin),           # file_xml
            prefact_id,                          # prefact_id
            usr_id,                              # usr_id
            xml_dat['CFDI_DATE'].split('T')[0],  # creation_date
            ref_id,                              # no_id_emp
            xml_dat['CFDI_SERIE'],               # serie
            xml_dat['CFDI_FOLIO'],               # folio
            'THIS FIELD IS DEPRECATED',          # items_str
            'THIS FIELD IS DEPRECATED',          # traslados_str
            'THIS FIELD IS DEPRECATED',          # retenciones_str
            xml_dat['INCEPTOR_REG'],             # reg_fiscal
            'THIS FIELD IS DEPRECATED',          # pay_method
            xml_dat['INCEPTOR_CP'],              # exp_place
            'FACTURA',                           # proposito      - It is obviously hardcoded
            'THIS FIELD IS DEPRECATED',          # no_aprob
            'THIS FIELD IS DEPRECATED',          # ano_aprob
            xml_dat['RECEPTOR_RFC'],             # rfc_custm      - RFC customer
            xml_dat['RECEPTOR_NAME'],            # rs_custm       - Razon social customer
            '0000',                              # account_number - An account fake number invented by me
            xml_dat['TAXES']['TRAS']['TOTAL'],   # total_tras
            '0',                                 # subtotal_with_desc
            xml_dat['CFDI_TOTAL'],               # total
            'false',                             # refact
            xml_dat['UUID']                      # id de documento - It came from SAT timbrado throughout PAC
        )
        logger.debug("Performing query: {}".format(q))
        try:
            s_out = None
            for row in HelperPg.onfly_query(pt.dbms.pgsql_conn, q, True):
                # Just taking first row of query result
                s_out = row['fac_save_xml']
                break

            # here we should parse s_out line
            logger.debug('store procedure fac_save_xml has returned: {}'.format(s_out))

            return ErrorCode.SUCCESS
        except:
            logger.error(dump_exception())
            return ErrorCode.ETL_ISSUES

    logger.info("stepping in factura handler within {}".format(__name__))

    filename = req.get('filename', None)

    source = ProfileReader.get_content(pt.source, ProfileReader.PNODE_UNIQUE)
    resdir = os.path.abspath(os.path.join(os.path.dirname(source), os.pardir))
    rdirs = fetch_rdirs(resdir, pt.res.dirs)

    tmp_dir = tempfile.gettempdir()
    tmp_file = os.path.join(tmp_dir, HelperStr.random_str())

    rc = __run_builder(logger, pt, tmp_file, resdir,
            'facxml',
            usr_id = req.get('usr_id', None),
            prefact_id = req.get('prefact_id', None))

    if rc == ErrorCode.SUCCESS:
        rc, inceptor_data = fetch_empdat(req.get('usr_id', None))
        if rc == ErrorCode.SUCCESS:
            out_dir = os.path.join(rdirs['cfdi_output'], inceptor_data['rfc'])
            rc, outfile = __pac_sign(logger, tmp_file, filename, out_dir, pt.tparty.pac)
            if rc == ErrorCode.SUCCESS:
                rc = store(outfile, req.get('usr_id', None),
                        req.get('prefact_id', None),
                        inceptor_data['no_id'])
            if rc == ErrorCode.SUCCESS:
                rc = __run_builder(logger, pt,
                        outfile.replace('.xml', '.pdf'),  # We replace the xml extension
                        resdir, 'facpdf', xml = outfile,
                        rfc = inceptor_data['rfc'])

    if os.path.isfile(tmp_file):
        os.remove(tmp_file)

    return rc.value


def donota(logger, pt, req):

    def store(f_xml):
        parser = SaxReader()
        xml_dat, _ = parser(f_xml)

        q = """select * from ncr_save_xml(
            {}::integer,
            '{}'::character varying,
            '{}'::character varying,
            '{}'::character varying,
            '{}'::boolean,
            {}::integer
            ) AS result( rc integer, msg text )
            """.format(                 # Store procedure parameters
            req.get('ncr_id', None),    #  _ncr_id
            os.path.basename(f_xml),    #  _file_xml
            xml_dat['CFDI_SERIE'],      #  _serie
            xml_dat['CFDI_FOLIO'],      #  _folio
            req.get('saldado', None),   #  _saldado
            req.get('usr_id', None)     #  _usr_id
        )
        logger.debug("Performing query: {}".format(q))
        try:
            res = HelperPg.onfly_query(pt.dbms.pgsql_conn, q, True)
            if len(res) != 1:
                raise Exception('unexpected result regarding execution of store')

            rcode, rmsg = res.pop()
            if rcode == 0:
                return ErrorCode.SUCCESS

            raise Exception(rmsg)
        except:
            logger.error(dump_exception())
            return ErrorCode.DBMS_SQL_ISSUES

    logger.info("stepping in donota handler within {}".format(__name__))

    filename = req.get('filename', None)

    source = ProfileReader.get_content(pt.source, ProfileReader.PNODE_UNIQUE)
    resdir = os.path.abspath(os.path.join(os.path.dirname(source), os.pardir))
    rdirs = fetch_rdirs(resdir, pt.res.dirs)

    tmp_dir = tempfile.gettempdir()
    tmp_file = os.path.join(tmp_dir, HelperStr.random_str())

    rc = __run_builder(logger, pt, tmp_file, resdir,
            'ncrxml',
            usr_id = req.get('usr_id', None),
            nc_id = req.get('ncr_id', None))

    if rc != ErrorCode.SUCCESS:
        pass
    else:
        _rfc = None

        try:
            _rfc = __get_emisor_rfc(logger, req.get('usr_id', None),
                    pt.dbms.pgsql_conn)
        except:
            rc = ErrorCode.DBMS_SQL_ISSUES

        if rc == ErrorCode.SUCCESS:
            out_dir = os.path.join(rdirs['cfdi_output'], _rfc)
            rc, signed_file = __pac_sign(logger, tmp_file, filename,
                    out_dir, pt.tparty.pac)
            if rc == ErrorCode.SUCCESS:
                rc = store(signed_file)
            if rc == ErrorCode.SUCCESS:
                rc = __run_builder(logger, pt,
                        signed_file.replace('.xml', '.pdf'),
                        resdir, 'ncrpdf', xml = signed_file, rfc = _rfc)

    if os.path.isfile(tmp_file):
        os.remove(tmp_file)

    return rc.value


def undonota(logger, pt, req):

    ncr_id = req.get('ncr_id', None)
    usr_id = req.get('usr_id', None)
    reason = req.get('reason', None)
    mode = req.get('mode', None)

    if reason is None:
        reason = ''

    if (ncr_id is None) or (usr_id is None) or (mode is None):
        return ErrorCode.REQUEST_INCOMPLETE.value

    def get_xml_name():
        q = """select ref_id as filename
            FROM fac_nota_credito
            WHERE fac_nota_credito.id = {}""".format(ncr_id)

        for row in HelperPg.onfly_query(pt.dbms.pgsql_conn, q, True):
            # Just taking first row of query result
            return row['filename'] + '.xml'

    source = ProfileReader.get_content(pt.source, ProfileReader.PNODE_UNIQUE)
    resdir = os.path.abspath(os.path.join(os.path.dirname(source), os.pardir))
    rdirs = fetch_rdirs(resdir, pt.res.dirs)

    _uuid = None
    _rfc = None

    try:
        _rfc = __get_emisor_rfc(logger, usr_id, pt.dbms.pgsql_conn)
    except:
        return ErrorCode.DBMS_SQL_ISSUES.value

    try:
        cfdi_dir = os.path.join(rdirs['cfdi_output'], _rfc)
        f_xml = os.path.join(cfdi_dir, get_xml_name())
        logger.debug('File to cancel {}'.format(f_xml))
        parser = SaxReader()
        xml_dat, _ = parser(f_xml)
        _uuid = xml_dat['UUID']
    except:
        return ErrorCode.RESOURCE_NOT_FOUND.value

    rc = __pac_cancel(logger, _uuid, _rfc, pt.tparty.pac)
    if rc != ErrorCode.SUCCESS:
        return rc.value

    q_do = """select * from ncr_exec_cancel(
        {}::integer,
        {}::integer,
        '{}'::text,
        {}::integer
        ) AS result( rc integer, msg text )
        """.format(   # Store procedure parameters
        usr_id,       #  _usr_id
        ncr_id,       #  _ncr_id
        reason,       #  _reason
        mode          #  _mode
    )

    rc = __run_sp_ra(logger, q_do, pt.dbms.pgsql_conn)
    return rc.value
