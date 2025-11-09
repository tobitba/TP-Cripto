package ar.edu.itba.cripto;

import java.io.File;
import java.util.Map;

public class Analyzer {

    public void analyze(Map<String, String> params) {
        String carrierFile = params.get("-p");

        System.out.println("=== ANALYZE MODE ===");
        System.out.println("Archivo portador: " + carrierFile);

        if (!new File(carrierFile).exists())
            throw new IllegalArgumentException("El archivo portador no existe: " + carrierFile);

        // TODO!
    }
}
