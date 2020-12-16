/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agnux.common.helpers;


import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 *
 * @author fmora
 */
public class DatagridHelper {
    private LinkedHashMap<String,String> configHeader;
    private String gridString;
    
    public void setGridString(String gridString) {
        this.gridString = gridString;
    }
    
    public String getGridString() {
        return gridString;
    }
    
    
    public void setConfigHeader(LinkedHashMap<String,String> configHeader) {
        this.configHeader = configHeader;
    }
    
    public LinkedHashMap<String,String> getConfigHeader() {
        return configHeader;
    }
    
    public void formaTabla(){
        String cuerpo_tabla = new String();
        
        cuerpo_tabla = "<table id=\"pag1\" class=\"tablesorter pagedemo _current\" >";  
        cuerpo_tabla += "<thead style=\"border-color:#303D74;\">";
        cuerpo_tabla += "<tr class=\"header backgroundheader\" height=\"30px\">";

        int ancho_sumatoria = 0;
        
        Iterator it2 = this.getConfigHeader().keySet().iterator();
        while (it2.hasNext()) {
            String key = ( String ) it2.next();
            String value = ( String ) this.getConfigHeader().get( key );
            String [] separados = null;
            separados = value.split(":");
            if ( separados.length > 0 ) {
                cuerpo_tabla += "<th width="+separados[1]+" class=\""+key+"\"><span class=\"header__\" style=\"display:none;\">"+ key+"</span>"+separados[0]+"&nbsp;&nbsp;&nbsp;&nbsp;</th>";
                ancho_sumatoria++;
            }
        }
        
        cuerpo_tabla += "</tr></thead>";
        cuerpo_tabla += "<tfoot ><tr align=\"center\">";
        cuerpo_tabla += "<th  colspan="+ancho_sumatoria+"  height=\"30px\">";
        cuerpo_tabla += "<div style=\"height:25px;\"><div id=\"gridpaginator_data\" style=\"width:100%;height:20px;margin:0 auto;text-align:center;\">"
        + "</div></div>";
        cuerpo_tabla += "</th></tr></tfoot><tbody></tbody></table>";
        
        this.setGridString(cuerpo_tabla + "\n");
        
    }
    
    
    
    
    
}
