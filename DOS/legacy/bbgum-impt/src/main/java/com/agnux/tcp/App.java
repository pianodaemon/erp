package com.agnux.tcp;

import com.maxima.bbgum.ServerReply;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class App {

    public static void main(String[] args) {
        String host = args[0];
        int port = Integer.parseInt(args[1]);
        System.out.println("=================================================");
        System.out.println("Sending the following request to " + host + ":" + port);
        String req = App.readStandartInput(System.in);
        System.out.println("=================================================");
        System.out.println(req);
        sendRequest(host, port, req.getBytes());
    }

    private static String readStandartInput(InputStream p) {
        InputStreamReader ir = new InputStreamReader(p);
        BufferedReader rf = new BufferedReader(ir);
        StringBuilder sb = new StringBuilder();
        String l;
        for (;;) {
            try {
                if ((l = rf.readLine()) == null) {
                    break;
                }
                sb.append(l).append("\n");
            } catch (IOException ex) {
                System.err.println(ex.getMessage());
                System.exit(1);
            }
        }

        String rs = sb.toString();

        if (rs.length() == 0) {
            System.err.println("Request without content");
            System.exit(1);
        }

        return App.removeLastChar(rs);
    }

    private static void sendRequest(String host, Integer port, byte[] req) {
        try {
            BbgumProxy bbgumProxy = new BbgumProxy();

            ServerReply reply = bbgumProxy.uploadBuff(host, port, req);
            String msg = "core reply code: " + reply.getReplyCode();
            if (reply.getReplyCode() == 0) {
                System.out.println(msg);
                System.exit(0);
            } else {
                System.err.println(msg);
                System.exit(1);
            }
        } catch (BbgumProxyError ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
        }
    }

    public static String removeLastChar(String s) {
        return (s == null || s.length() == 0)
                ? null
                : (s.substring(0, s.length() - 1));
    }
}
