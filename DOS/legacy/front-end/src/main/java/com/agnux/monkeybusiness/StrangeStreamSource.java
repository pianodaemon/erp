package com.agnux.monkeybusiness;

import java.io.IOException;
import java.io.InputStream;

public interface StrangeStreamSource {

    public InputStream getInputStream() throws IOException;

    public int calcSize();

    public String getMimeType();
}
