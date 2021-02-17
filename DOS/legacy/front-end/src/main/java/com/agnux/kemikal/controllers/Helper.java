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
             grpcPort = "10090";
             log.log(Level.SEVERE, "{0}: gRPC connection params not found. Defaults to 127.0.0.1:10090", modulo);
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
}
