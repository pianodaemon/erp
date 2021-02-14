package com.agnux.kemikal.controllers;


public class Helper {
    
    public static String getGrpcConnString(String modulo) {

        String grpcHost = System.getenv(modulo + "_GRPC_HOST"),
               grpcPort = System.getenv(modulo + "_GRPC_PORT");

         if (grpcHost == null || grpcPort == null) {
             grpcHost = "127.0.0.1";
             grpcPort = "10090";
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
}
