/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agnux.common.obj;

import com.agnux.common.helpers.DatagridHelper;
import java.util.LinkedHashMap;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author fmora
 */
public class ResourceProject {
    
    private String layoutheader = "./vm/layouts/header.vm";
    private String layoutfooter = "./vm/layouts/footer.vm";
    private String layoutmenu = "./vm/layouts/menu.vm";
    
    public String getLayoutmenu() {
        return layoutmenu;
    }
    
    public String getLayoutfooter() {
        return layoutfooter;
    }

    public String getLayoutheader() {
        return layoutheader;
    }
    
    public String generaGrid(LinkedHashMap<String,String> infoConstruccionTabla){
        DatagridHelper grid = new DatagridHelper();
        grid.setConfigHeader(infoConstruccionTabla);
        grid.formaTabla();
        return grid.getGridString();
    }
    
    public int calculaTotalPag(int totalItems, int rowsperpag){
        int residuo = totalItems % rowsperpag;
        int total_pag = totalItems/rowsperpag;
        if(residuo > 0){
            total_pag++;
        }
        return total_pag;
    }

    //calcula a partir de dondes cortara el query (offset) para el paginador
    public int __get_inicio_offset(int items_por_pagina, int pag_start){
        int total = items_por_pagina * pag_start;
        total = total - items_por_pagina;
        return total;
    }
    
    
    
    public String getUrl(HttpServletRequest httpservletrequest){
        //String scheme = httpservletrequest.getScheme();
        //String serverName = httpservletrequest.getServerName();
        //int serverPort = httpservletrequest.getServerPort();
        String contextPath = httpservletrequest.getContextPath();
        //return scheme+"://"+serverName+":"+serverPort+contextPath;
        
        return contextPath;
    }
    
    

    
}