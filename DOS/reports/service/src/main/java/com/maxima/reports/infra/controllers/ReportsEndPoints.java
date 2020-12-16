package com.maxima.reports.infra.controllers;


import com.maxima.reports.dal.rdbms.SalesDAO;
import java.util.ArrayList;
import java.util.HashMap;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

@RestController
@RequestMapping("/v1")
public class ReportsEndPoints {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @GetMapping("ventas/{fecha_inicial}/{fecha_final}/{empresa_id}/netas/cliente")
    @ResponseBody
    ArrayList<HashMap<String, String>> ventasNetasCliente(@PathVariable(value = "fecha_inicial") String fecha_inicial,
                @PathVariable(value = "fecha_final") String fecha_final,
                @PathVariable(value = "empresa_id") int empresaId) {
        
        return SalesDAO.getVentasNetasxCliente(jdbcTemplate, fecha_inicial, fecha_final, empresaId);
    }
}
