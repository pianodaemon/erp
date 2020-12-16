package com.agnux.cfdi.adendas;

import java.util.LinkedHashMap;

/* We're no longer supporting addenda features */
public class AdendaCliente extends Adenda{

    @Override
    public void createAdenda(Integer noAdenda, LinkedHashMap<String, Object> dataAdenda, String dirXml, String fileNameXml) {}
}
