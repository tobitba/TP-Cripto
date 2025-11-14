package ar.edu.itba.cripto;

import ar.edu.itba.cripto.actions.Analyzer;
import ar.edu.itba.cripto.actions.Embedder;
import ar.edu.itba.cripto.actions.Extractor;

import java.util.HashMap;
import java.util.Map;

public class Steganography {

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            printProgramUsage();
            System.exit(1);
        }
        Map<String, String> params = parseArguments(args);

        if (params.containsKey("-embed")) {
            new Embedder().embed(params);
        } else if (params.containsKey("-extract")) {
            new Extractor().extract(params);
        } else if (params.containsKey("-analyze")) {
            new Analyzer().analyze(params);
        } else {
            System.out.println("Debe especificar una acción (-embed | -extract | -analyze)");
            printProgramUsage();
            System.exit(1);
        }
    }

    private static void printProgramUsage() {
        System.out.println("Uso del programa:");
        System.out.println("  -embed   : Ocultar un archivo dentro de un BMP");
        System.out.println("  -extract : Extraer un archivo oculto de un BMP");
        System.out.println("  -analyze : Analizar un BMP para detectar esteganografía");
        System.out.println();
    }

    private static Map<String, String> parseArguments(String[] args) {
        Map<String, String> params = new HashMap<>();
        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("-") && i + 1 < args.length && !args[i + 1].startsWith("-")) {
                params.put(args[i].toLowerCase(), args[i + 1]);
                i++;
            } else if (args[i].startsWith("-")) {
                params.put(args[i].toLowerCase(), null);
            }
        }
        return params;
    }
}
