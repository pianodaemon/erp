package com.agnux.cfdi.adendas;

import java.util.LinkedHashMap;


public abstract class Adenda {
    protected Adenda next;
    
    public void SetNext(Adenda next){
        this.next = next;
    }
    
    public abstract void createAdenda(Integer noAdenda, LinkedHashMap<String, Object> dataAdenda, String dirXml, String fileNameXml);
}
