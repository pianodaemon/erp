/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agnux.kemikal.controllers;

/**
 *
 * @author j2eeserver
 */
public class PotCatCusorder {

    public String cmd;
    public String usuario_id;
    public String salesman_id;
    public String customer_id;
    public String cust_df_id;
    public String warehouse_id;
    public String currency_id;
    public String sup_credays_id;
    public String forma_pago_id;
    public String met_pago_id;
    public String uso_id;
    public String pedido_id;
    public String tasaretimmex;
    public String currency_val;
    public String perc_desc;
    public String allow_desc;
    public String send_comments;
    public String flete_enable;
    public String send_route;
    public String comments;
    public String razon_desc;
    public String trans;
    public String date_lim;
    public String delivery_place;
    public String purch_order;
    public String account;
    public String no_cot;
    public String matrix;

    public String conform_cat_store() {
        return "select poc_cat_cusorder from poc_cat_cusorder("
                + "'" + cmd + "',"
                + usuario_id + ","
                + salesman_id + ","
                + customer_id + ","
                + cust_df_id + ","
                + warehouse_id + ","
                + currency_id + ","
                + sup_credays_id + ","
                + met_pago_id + ","
                + forma_pago_id + ","
                + uso_id + ","
                + pedido_id + ","
                + tasaretimmex + ","
                + currency_val + ","
                + perc_desc + ","
                + allow_desc + ","
                + send_comments + ","
                + flete_enable + ","
                + send_route + "," +
                "'" + comments + "'," +
                "'" + razon_desc + "'," +
                "'" + trans + "'," +
                "'" + date_lim + "'," +
                "'" + delivery_place + "'," +
                "'" + purch_order + "'," +
                "'" + account + "'," +
                "'" + no_cot + "'," +
                " array["+ matrix +"]);";
    }
}
