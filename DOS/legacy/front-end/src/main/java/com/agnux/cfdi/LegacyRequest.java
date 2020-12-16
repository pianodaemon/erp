/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agnux.cfdi;

import java.io.IOException;
import java.util.HashMap;
import org.codehaus.jackson.map.ObjectMapper;
/**
 *
 * @author j2eeserver
 */
public class LegacyRequest {
    public HashMap<String,Object> request = new HashMap<String,Object>();
    
    public String getJson() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        HashMap<String,Object> wrapper = new HashMap<String,Object>();
        wrapper.put("request", this.request);
        return mapper.writeValueAsString(wrapper);
    }
    
    public void args(HashMap<String,String> args) {    
        this.request.put("args", args);
    }
    
    public void sendTo(String module) {
        this.request.put("to", module);
    }
    
    public void from(String entity) {
        this.request.put("from", entity);
    }

    public void action(String action) {
        this.request.put("action", action);
    }
}