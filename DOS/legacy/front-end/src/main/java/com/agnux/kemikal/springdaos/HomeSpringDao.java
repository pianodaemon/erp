/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agnux.kemikal.springdaos;

import com.agnux.kemikal.interfacedaos.HomeInterfaceDao;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

/**
 *
 * @author agnux
 */
public class HomeSpringDao  implements HomeInterfaceDao  {
    
    private static final Logger log  = Logger.getLogger(HomeSpringDao.class.getName());
    private JdbcTemplate jdbcTemplate;
    
    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    
    @Override
    public HashMap<String, String> getUserByName(String name) {
        
        String sql_to_query = "SELECT gral_usr.id, "
                +"gral_usr.username, "
                +"gral_emp.id AS empresa_id, "
                +"gral_emp.titulo AS empresa, "
                +"gral_emp.incluye_produccion, "
                +"gral_emp.incluye_contabilidad, "
                +"gral_emp.control_exis_pres,"
                +"gral_emp.incluye_crm, "
                +"gral_emp.nivel_cta, "
                +"gral_emp.transportista, "
                +"gral_emp.nomina,"
                + "gral_emp.incluye_log,"
                +"gral_usr_suc.gral_suc_id AS sucursal_id, "
                +"gral_suc.titulo AS sucursal,"
                +"gral_usr.gral_empleados_id "
        +"FROM gral_usr  "
        +"JOIN gral_usr_suc ON gral_usr_suc.gral_usr_id=gral_usr.id "
        +"JOIN gral_suc ON gral_suc.id=gral_usr_suc.gral_suc_id "
        +"JOIN gral_emp ON gral_emp.id=gral_suc.empresa_id "
        +"WHERE gral_usr.username=? and gral_usr.enabled=?;";
        
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        
        HashMap<String, String> hm = (HashMap<String, String>) this.jdbcTemplate.queryForObject(
            sql_to_query, 
            new Object[]{name, true}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("username",rs.getString("username"));
                    row.put("empresa_id",String.valueOf(rs.getInt("empresa_id")));
                    row.put("empresa",String.valueOf(rs.getString("empresa")));
                    row.put("incluye_produccion",String.valueOf(rs.getBoolean("incluye_produccion")));
                    row.put("incluye_contab",String.valueOf(rs.getBoolean("incluye_contabilidad")));
                    row.put("control_exi_pres",String.valueOf(rs.getBoolean("control_exis_pres")));
                    row.put("incluye_crm",String.valueOf(rs.getBoolean("incluye_crm")));
                    row.put("nivel_cta",String.valueOf(rs.getInt("nivel_cta")));
                    row.put("transportista",String.valueOf(rs.getBoolean("transportista")));
                    row.put("sucursal_id",String.valueOf(rs.getInt("sucursal_id")));
                    row.put("sucursal",String.valueOf(rs.getString("sucursal")));
                    row.put("empleado_id",String.valueOf(rs.getInt("gral_empleados_id")));
                    row.put("incluye_nomina",String.valueOf(rs.getBoolean("nomina")));
                    row.put("incluye_log",String.valueOf(rs.getBoolean("incluye_log")));
                    return row;
                }
            }
        );
        
        //actualiza ultimo acceso
        this.getJdbcTemplate().execute("UPDATE gral_usr SET ultimo_acceso=now() WHERE username='"+name+"' AND enabled=true;");
        
        return hm; 
    }
    
    
    
    
    @Override
    public HashMap<String, String> getUserById(Integer id_user) {
        
        String sql_to_query = ""
                + "SELECT gral_usr.id, "
                    + "gral_usr.username, "
                    + "gral_suc.empresa_id, "
                    + "gral_emp.titulo AS empresa, "
                    + "gral_emp.incluye_produccion, "
                    + "gral_emp.incluye_contabilidad, "
                    + "gral_emp.control_exis_pres,"
                    + "gral_emp.incluye_crm, "
                    + "gral_emp.nivel_cta, "
                    + "gral_emp.transportista, "
                    + "gral_emp.nomina,"
                    + "gral_emp.incluye_log,"
                    + "gral_usr_suc.gral_suc_id AS sucursal_id, "
                    + "gral_suc.titulo AS sucursal, "
                    +"gral_usr.gral_empleados_id "
                + "FROM gral_usr "
                + "JOIN gral_usr_suc ON gral_usr_suc.gral_usr_id = gral_usr.id "
                + "JOIN gral_suc ON gral_suc.id = gral_usr_suc.gral_suc_id "
                + "JOIN gral_emp ON gral_emp.id=gral_suc.empresa_id "
                + "WHERE gral_usr.id=? and gral_usr.enabled=?";
        
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        
        HashMap<String, String> hm = (HashMap<String, String>) this.jdbcTemplate.queryForObject(
            sql_to_query, 
            new Object[]{new Integer(id_user), true}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("username",rs.getString("username"));
                    row.put("empresa_id",String.valueOf(rs.getInt("empresa_id")));
                    row.put("empresa",String.valueOf(rs.getString("empresa")));
                    row.put("incluye_produccion",String.valueOf(rs.getBoolean("incluye_produccion")));
                    row.put("incluye_contab",String.valueOf(rs.getBoolean("incluye_contabilidad")));
                    row.put("control_exi_pres",String.valueOf(rs.getBoolean("control_exis_pres")));
                    row.put("incluye_crm",String.valueOf(rs.getBoolean("incluye_crm")));
                    row.put("nivel_cta",String.valueOf(rs.getInt("nivel_cta")));
                    row.put("transportista",String.valueOf(rs.getBoolean("transportista")));
                    row.put("sucursal_id",String.valueOf(rs.getInt("sucursal_id")));
                    row.put("sucursal",String.valueOf(rs.getString("sucursal")));
                    row.put("empleado_id",String.valueOf(rs.getInt("gral_empleados_id")));
                    row.put("incluye_nomina",String.valueOf(rs.getBoolean("nomina")));
                    row.put("incluye_log",String.valueOf(rs.getBoolean("incluye_log")));
                    return row;
                }
            }
        );
        //actualiza ultimo acceso
        this.getJdbcTemplate().execute("UPDATE gral_usr SET ultimo_acceso=now() WHERE id="+id_user +" AND enabled=true;");
        return hm; 
    }
    
    
    
    
    
    
    
}
