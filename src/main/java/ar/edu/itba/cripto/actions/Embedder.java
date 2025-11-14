package ar.edu.itba.cripto.actions;

import ar.edu.itba.cripto.BMPImage;
import ar.edu.itba.cripto.StegoUtils;
import ar.edu.itba.cripto.algorithms.*;

import java.io.File;
import java.util.Map;

public class Embedder {

    public void embed(Map<String, String> params) throws Exception {

        String inputFile = params.get("-in");
        String carrierFile = params.get("-p");
        String outputFile = params.get("-out");
        String stegMethod = params.get("-steg");
        String password = params.get("-pass");
        String encAlgorithm = params.get("-a");
        String mode = params.get("-m");

        validateInputs(inputFile, carrierFile, stegMethod, password, encAlgorithm, mode);

        System.out.println("Archivo a ocultar: " + inputFile);
        System.out.println("Imagen portadora: " + carrierFile);
        System.out.println("Archivo de salida: " + outputFile);
        System.out.println("Método esteganográfico: " + stegMethod);

        BMPImage image = new BMPImage(carrierFile);
        byte[] pixels = image.getBody();
        byte[] dataBlock = StegoUtils.buildDataBlock(inputFile);

        if (password != null) {
            dataBlock = Encryption.encrypt(dataBlock, encAlgorithm, mode, password);
            System.out.println("Data block cifrado (" + dataBlock.length + " bytes)");
        }

        IStegoAlgorithm stego = selectAlgorithm(stegMethod);
        byte[] encodedPixels = stego.encode(pixels, dataBlock);

        image.setBody(encodedPixels);
        image.save(outputFile);
    }

    private void validateInputs(String inputFile, String carrierFile, String stegMethod,
                                String password, String encAlgorithm, String mode) {

        if (inputFile == null || !new File(inputFile).exists())
            throw new IllegalArgumentException("El archivo a ocultar no existe: " + inputFile);

        if (carrierFile == null || !new File(carrierFile).exists())
            throw new IllegalArgumentException("El archivo portador no existe: " + carrierFile);

        if (stegMethod == null)
            throw new IllegalArgumentException("Debe especificar el método esteganográfico (-steg LSB1 | LSB4 | LSBI)");

        if (password != null && (encAlgorithm == null || mode == null))
            throw new IllegalArgumentException("Debe especificar -a y -m si usa '-pass'");
    }

    private IStegoAlgorithm selectAlgorithm(String method) {
        return switch (method.toUpperCase()) {
            case "LSB1" -> new LSB1();
            case "LSB4" -> new LSB4();
            case "LSBI" -> new LSBI();
            default -> throw new IllegalArgumentException("Método no soportado: " + method);
        };
    }
}
