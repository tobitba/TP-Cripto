package ar.edu.itba.cripto.actions;

import ar.edu.itba.cripto.BMPImage;
import ar.edu.itba.cripto.StegoUtils;
import ar.edu.itba.cripto.algorithms.IStegoAlgorithm;
import ar.edu.itba.cripto.algorithms.LSB1;
import ar.edu.itba.cripto.algorithms.LSB4;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class Embedder {

    public void embed(Map<String, String> params) throws IOException {
        String inputFile = params.get("-in");
        String carrierFile = params.get("-p");
        String outputFile = params.get("-out");
        String stegMethod = params.get("-steg");
        String password = params.get("-pass");
        String encAlgorithm = params.get("-a");
        String mode = params.get("-m");

        System.out.println("Archivo a ocultar: " + inputFile);
        System.out.println("Imagen portadora: " + carrierFile);
        System.out.println("Archivo de salida: " + outputFile);
        System.out.println("Método esteganográfico: " + stegMethod);

        if (password != null)
            System.out.println("Modo cifrado: " + encAlgorithm + "/" + mode);

        if (!new File(inputFile).exists())
            throw new IllegalArgumentException("El archivo a ocultar no existe: " + inputFile);
        if (!new File(carrierFile).exists())
            throw new IllegalArgumentException("El archivo portador no existe: " + carrierFile);
        if (stegMethod == null)
            throw new IllegalArgumentException("Debe especificar el método esteganográfico (-steg LSB1 | LSB4 | LSBI)");

        BMPImage image = new BMPImage(carrierFile);
        byte[] dataBlock = StegoUtils.buildDataBlock(inputFile);

        if (password != null && encAlgorithm != null && mode != null) {
            // TODO! dataBlock = StegoUtils.encryptDataBlock(dataBlock, encAlgorithm, mode, password);
        }

        IStegoAlgorithm algorithm = switch (stegMethod.toUpperCase()) {
            case "LSB1" -> new LSB1();
            case "LSB4" -> new LSB4();
            // TODO! case "LSBI" -> new LSBI();
            default -> throw new IllegalArgumentException("Método no soportado: " + stegMethod);
        };

        BMPImage encoded = algorithm.encode(image, dataBlock);
        encoded.save(outputFile);
    }
}
