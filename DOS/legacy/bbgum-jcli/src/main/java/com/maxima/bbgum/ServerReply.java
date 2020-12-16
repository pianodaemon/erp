package com.maxima.bbgum;

public class ServerReply {

    private int result;
    private byte[] data;

    public ServerReply() {
        this.result = 0;
    }
    public int getReplyCode() {
        return result;
    }

    public void setReplyCode(int result) {
        this.result = result;
    }

    public byte[] getReplyBuffer() {
        return data;
    }

    public void setReplyBuffer(byte[] data) {
        this.data = data;
    }   
}
