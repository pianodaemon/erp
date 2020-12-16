package com.maxima.bbgum;

public class Action {

    private byte id;
    private byte transnum;
    private byte[] data;

    public Action() {

    }

    public Action(final byte[] data) throws FrameError {

        if (data.length > Frame.FRAME_BODY_MAX_LENGTH) {
            String msg = "Action can not be bigger than " + Frame.FRAME_BODY_MAX_LENGTH + " " + "bytes";
            throw new FrameError(msg);
        }

        this.setArchetype(data[0]);
        this.setTransNum(data[1]);

        byte dataForAction[] = new byte[data.length - Frame.ACTION_FLOW_INFO_SEGMENT_LENGTH];
        System.arraycopy(data,
            Frame.ACTION_FLOW_INFO_SEGMENT_LENGTH,
            dataForAction, 0, dataForAction.length);
        this.setBuffer(dataForAction);
    }

    public byte getArchetype() {
        return id;
    }

    public void setArchetype(byte id) {
        this.id = id;
    }

    public byte getTransNum() {
        return transnum;
    }

    public void setTransNum(byte transaction) {
        this.transnum = transaction;
    }

    public byte[] getBuffer() {
        return data;
    }

    public void setBuffer(byte[] data) {
        this.data = data;
    }
}
