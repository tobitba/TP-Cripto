package ar.edu.itba.cripto.actions;

import ar.edu.itba.cripto.utils.BMPImage;
import ar.edu.itba.cripto.utils.StegoUtils;
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

        BMPImage image = new BMPImage(carrierFile);
        byte[] pixels = image.getBody();
        byte[] dataBlock = StegoUtils.buildDataBlock(inputFile);

        if (encAlgorithm != null) {
            dataBlock = Encryption.encrypt(dataBlock, encAlgorithm, mode, password);

            byte[] length = StegoUtils.getSizeInBigEndian() ? StegoUtils.intToBytesBE(dataBlock.length) : StegoUtils.intToBytesLE(dataBlock.length);
            byte[] finalBlock = new byte[4 + dataBlock.length];

            System.arraycopy(length, 0, finalBlock, 0, 4);
            System.arraycopy(dataBlock, 0, finalBlock, 4, dataBlock.length);

            dataBlock = finalBlock;
            System.out.println("Data block cifrado (" + dataBlock.length + " bytes)");
        }

        IStegoAlgorithm stego = StegoUtils.selectAlgorithm(stegMethod);
        byte[] encodedPixels = stego.encode(pixels, dataBlock);

        image.setBody(encodedPixels);
        image.save(outputFile);

        System.out.println("\nHecho!");
        System.out.println("Data de " + dataBlock.length + " bytes codificada en imagen de " + (image.getHeader().length + encodedPixels.length) + " bytes.");
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
}
