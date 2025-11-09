package ar.edu.itba.cripto;

import java.io.File;
import java.util.Map;

public class Embedder {

    public void embed(Map<String, String> params) {
        String inputFile = params.get("-in");
        String carrierFile = params.get("-p");
        String outputFile = params.get("-out");
        String algorithm = params.get("-steg");

        System.out.println("=== EMBED MODE ===");
        System.out.println("Archivo a ocultar: " + inputFile);
        System.out.println("Imagen portadora: " + carrierFile);
        System.out.println("Archivo de salida: " + outputFile);
        System.out.println("Algoritmo: " + algorithm);

        if (!new File(inputFile).exists())
            throw new IllegalArgumentException("El archivo a ocultar no existe: " + inputFile);
        if (!new File(carrierFile).exists())
            throw new IllegalArgumentException("El archivo portador no existe: " + carrierFile);
        if (algorithm == null)
            throw new IllegalArgumentException("Debe especificar el método estenografico (-steg LSB1 | LSB4 | LSBI)");

        // TODO!
    }
}
