package com.agnux.kemikal.controllers;

import java.util.logging.Logger;
import java.util.logging.Level;


public class Helper {

    private static final Logger log  = Logger.getLogger(Helper.class.getName());

    public static String getGrpcConnString(String modulo) {

        String grpcHost = System.getenv(modulo + "_GRPC_HOST"),
               grpcPort = System.getenv(modulo + "_GRPC_PORT");

         if (grpcHost == null || grpcPort == null) {

             grpcHost = "127.0.0.1";
             switch (modulo) {
                 case "SALES"   : grpcPort = "10090"; break;
                 case "COBRANZA": grpcPort = "10110"; break;
                 default        : grpcPort = "10090"; break;
             }
             String msj = String.format("gRPC connection params (%s) not found. Defaults to %s:%s", modulo, grpcHost, grpcPort);
             log.log(Level.SEVERE, msj);
         }

         return grpcHost + ":" + grpcPort;
    }
    
    public static int toInt(String str) {
        String res = str.trim();
        return res.equals("") ? 0 : Integer.parseInt(res);
    }
    
    public static double toDouble(String str) {
        String res = str.trim();
        return res.equals("") ? 0.0 : Double.parseDouble(res);
    }
    
    public static long toLong(String str) {
        String res = str.trim();
        return res.equals("") ? 0 : Long.parseLong(res);
    }
    
    public static String[] getCfdiengineConnParams() {

        String host = System.getenv("CFDIENGINE_HOST"),
               port = System.getenv("CFDIENGINE_PORT");

         if (host == null || port == null) {

             host = "localhost";
             port = "10080";
             String msj = String.format("CFDIENGINE connection params not found. Defaults to %s:%s", host, port);
             log.log(Level.SEVERE, msj);
         }

         String[] params = {host, port};
         return params;
    }
}
