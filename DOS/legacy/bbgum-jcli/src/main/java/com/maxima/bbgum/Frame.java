package com.maxima.bbgum;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

public final class Frame {

    private byte[] header;
    private byte[] body;
    int actionLength;

    public static final int FRAME_HEADER_LENGTH = 4;
    public static final int FRAME_BODY_MAX_LENGTH = 512;
    public static final int ACTION_FLOW_INFO_SEGMENT_LENGTH = 2;
    public static final int ACTION_ACK_DATA_SIZE = 2;
    public static final int FRAME_FULL_MAX_LENGTH = FRAME_HEADER_LENGTH + FRAME_BODY_MAX_LENGTH;
    public static final int ACTION_DATA_SEGMENT_MAX_LENGTH = FRAME_BODY_MAX_LENGTH - ACTION_FLOW_INFO_SEGMENT_LENGTH;
    public static final int C_NULL_CHARACTER = 0;

    public static final byte DAT_ACK = (byte) 0x06;
    public static final byte DAT_NAK = (byte) 0x15;

    public Frame(Action action) throws FrameError {
        this();
        this.actionLength = Frame.ACTION_FLOW_INFO_SEGMENT_LENGTH
            + action.getBuffer().length;
        this.header = Frame.encodeHeader(actionLength);
        this.body[0] = action.getArchetype();
        this.body[1] = action.getTransNum();
        System.arraycopy(action.getBuffer(), 0, this.body,
            Frame.ACTION_FLOW_INFO_SEGMENT_LENGTH,
            action.getBuffer().length);
    }

    private Frame() {
        this.header = new byte[Frame.FRAME_HEADER_LENGTH];
        this.body = new byte[Frame.FRAME_BODY_MAX_LENGTH];
        this.actionLength = 0;
    }

    public byte[] getFrame() {
        byte[] data = new byte[Frame.FRAME_FULL_MAX_LENGTH];
        System.arraycopy(this.header, 0, data, 0, this.header.length);
        System.arraycopy(this.body, 0, data, this.header.length, this.body.length);
        return data;
    }

    public final Action getAction() {
        Action action = new Action();
        int dataBufferSize = this.actionLength
            - Frame.ACTION_FLOW_INFO_SEGMENT_LENGTH;
        byte[] data = new byte[dataBufferSize];

        System.arraycopy(this.body,
            Frame.ACTION_FLOW_INFO_SEGMENT_LENGTH, data, 0,
            data.length);

        action.setArchetype(this.body[0]);
        action.setTransNum(this.body[1]);
        action.setBuffer(data);

        return action;
    }

    public static byte calcIdForACKorNAK(final byte id) {
        return (byte)(id + 1);
    }

    public static byte[] encodeHeader(final int actionLength) throws FrameError {
        byte[] header = new byte[Frame.FRAME_HEADER_LENGTH];
        byte[] ascii;

        try {
            String result = String.format("%4d", actionLength);
            ascii = result.getBytes("US-ASCII");

            for (int index = 0; index < ascii.length; ++index) header[index] = ascii[index];

        } catch (UnsupportedEncodingException e) {
            throw new FrameError(e.getMessage());
        }

        return header;
    }

    public static int decodeHeader(final byte header[]) throws FrameError {
        int rc = 0;

        if (header.length == Frame.FRAME_HEADER_LENGTH) {
            Charset charset = Charset.forName("US-ASCII");
            String strHeader = new String(header, 0, header.length, charset);
            rc = Integer.parseInt(strHeader.trim());

            if (rc > Frame.FRAME_BODY_MAX_LENGTH) {
                String msg = "Detected a frame body's length violiation!!";
                throw new FrameError(msg);
            }
        } else {
            String msg = "Detected a frame header's length violiation!!";
            throw new FrameError(msg);
        }

        return rc;
    }
}
