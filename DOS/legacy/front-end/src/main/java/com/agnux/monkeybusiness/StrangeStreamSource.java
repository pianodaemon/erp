package com.agnux.monkeybusiness;

import java.io.InputStream;


public interface StrangeStreamSource {

    public InputStream getInputStream();

    public int calcSize();

    public String getMimeType();
}
