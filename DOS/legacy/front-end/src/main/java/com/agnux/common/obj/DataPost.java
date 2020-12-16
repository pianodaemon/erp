/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agnux.common.obj;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author fmora
 */
public class DataPost {
    private String orderby;
    private String desc;
    private int items_por_pag;
    private int pag_start;
    private String display_pag;
    private String input_json;
    private String cadena_busqueda;
    int total_items;
    int total_pag;
    private String id_user_cod;


    
    public DataPost(String orderby,String desc,int items_por_pag,int pag_start,String display_pag,String input_json,String cadena_busqueda,int total_items,int total_pag, String id_user_cod){
        setCadena_busqueda(cadena_busqueda);
        setDesc(desc);
        setDisplay_pag(display_pag);
        setInput_json(input_json);
        setItems_por_pag(items_por_pag);
        setOrderby(orderby);
        setPag_start(pag_start);
        setTotal_items(total_items);
        setTotal_pag(total_pag);
        setId_user_cod(id_user_cod);
        
    }

    public String getId_user_cod() {
        return this.id_user_cod;
    }

    public void setId_user_cod(String id_user_cod) {
        this.id_user_cod = id_user_cod;
    }
    
    
    public void setTotal_items(int total_items) {
        this.total_items = total_items;
    }

    public void setTotal_pag(int total_pag) {
        this.total_pag = total_pag;
    }

    public int getTotal_items() {
        return total_items;
    }

    public int getTotal_pag() {
        return total_pag;
    }
    
    public void setCadena_busqueda(String cadena_busqueda) {
        this.cadena_busqueda = cadena_busqueda;
    }
    
    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setDisplay_pag(String display_pag) {
        this.display_pag = display_pag;
    }

    public void setInput_json(String input_json) {
        this.input_json = input_json;
    }

    public void setItems_por_pag(int items_por_pag) {
        this.items_por_pag = items_por_pag;
    }

    public void setOrderby(String orderby) {
        this.orderby = orderby;
    }

    public void setPag_start(int pag_start) {
        this.pag_start = pag_start;
    }

    public String getCadena_busqueda() {
        return cadena_busqueda;
    }

    public String getDesc() {
        return desc;
    }

    public String getDisplay_pag() {
        return display_pag;
    }

    public String getInput_json() {
        return input_json;
    }

    public int getItems_por_pag() {
        return items_por_pag;
    }

    public String getOrderby() {
        return orderby;
    }
    
    public int getPag_start() {
        return pag_start;
    }
    
    public ArrayList<HashMap<String, Object>> formaHashForPos(DataPost obj){
        ArrayList<HashMap<String, Object>> retorno = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> row = new HashMap<String, Object>();
        
        row.put("orderby", obj.getOrderby());
        row.put("desc", obj.getDesc());
        row.put("items_por_pag", obj.getItems_por_pag());
        row.put("pag_start", obj.getPag_start());
        row.put("display_pag", obj.getDisplay_pag());
        row.put("input_json", obj.getInput_json());
        row.put("cadena_busqueda", obj.getCadena_busqueda());
        
        row.put("total_items", obj.getTotal_items());
        row.put("total_paginas", obj.getTotal_pag());
        row.put("iu", obj.getId_user_cod());
        retorno.add(row);
        
        return retorno;
    }
}
