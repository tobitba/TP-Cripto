package ar.edu.itba.cripto.actions;

import java.io.File;
import java.util.Map;

public class Extractor {

    public void extract(Map<String, String> params) {
        String carrierFile = params.get("-p");
        String outputFile = params.get("-out");
        String algorithm = params.get("-steg");

        System.out.println("=== EXTRACT MODE ===");
        System.out.println("Imagen portadora: " + carrierFile);
        System.out.println("Archivo de salida: " + outputFile);
        System.out.println("Algoritmo: " + algorithm);

        if (!new File(carrierFile).exists())
            throw new IllegalArgumentException("El archivo portador no existe: " + carrierFile);
        if (outputFile == null)
            throw new IllegalArgumentException("Debe especificar un archivo de salida (-out)");
        if (algorithm == null)
            throw new IllegalArgumentException("Debe especificar el método estenografico (-steg LSB1 | LSB4 | LSBI)");

        // TODO!
    }
}
