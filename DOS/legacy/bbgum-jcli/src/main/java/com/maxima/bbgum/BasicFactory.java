package com.maxima.bbgum;

import java.util.HashMap;
import java.util.Map;

public class BasicFactory<I,T> {
    Map<I, Class<? extends T>> s;
    public BasicFactory(){
        this.s = new HashMap<I, Class<? extends T>>();
    }

    public boolean isSupported(final I id) {
        return (this.s.get(id) != null)? true : false;
    }

    public T incept(I id) throws InstantiationException, IllegalAccessException {
        return s.get(id).newInstance();
    }
    
    public void subscribe(I id, Class<? extends T> c) {
        this.s.put(id, c);
    }
}
