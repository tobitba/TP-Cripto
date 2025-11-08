package ar.edu.itba.cripto;

import java.util.HashMap;
import java.util.Map;

public class Steganography {
    public static void main(String[] args) {
        if (args.length == 0) {
            printProgramUsage();
            System.exit(1);
        }

        Map<String, String> params = parseArguments(args);


    }

    private static void printProgramUsage() {
        System.out.println("...?");
    }

    private static Map<String, String> parseArguments(String[] args) {
        Map<String, String> params = new HashMap<>();
        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("-") && i + 1 < args.length && !args[i + 1].startsWith("-")) {
                params.put(args[i], args[i + 1].toLowerCase());
                i++;
            } else if (args[i].startsWith("-")) {
                params.put(args[i], null);
            }
        }
        return params;
    }
}