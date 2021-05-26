package com.agnux.kemikal.reportes;

public class Helper {

    public static String addSeparadorMiles(String input, String sep) {
        String output = "";
        String[] arr = input.split("\\.");
        String s = arr[0];
        int cantGrupos = s.length() / 3;
        int cantCifras = s.length() % 3;
        int pos = 0;

        if (cantCifras != 0) {
            output += s.substring(0, cantCifras);
            if (cantGrupos > 0) {
                output += sep;
            }
            pos = cantCifras;
        }
        for (int i = 1; i <= cantGrupos; i++) {
            output += s.substring(pos, pos + 3);
            if (i < cantGrupos) {
                output += sep;
            }
            pos += 3;
        }
        if (arr.length > 1) {
            output += "." + arr[1];
        }
        return output;
    }
}
