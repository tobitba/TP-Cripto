package ar.edu.itba.cripto.actions;

import ar.edu.itba.cripto.utils.BMPImage;
import ar.edu.itba.cripto.utils.StegoUtils;
import ar.edu.itba.cripto.algorithms.*;

import java.io.File;
import java.util.Map;
import java.nio.file.Files;
import java.nio.file.Path;

public class Extractor {

    public void extract(Map<String, String> params) throws Exception {

        String carrierFile = params.get("-p");
        String outputPath = params.get("-out");
        String stegMethod = params.get("-steg");
        String password = params.get("-pass");
        String encAlgorithm = params.get("-a");
        String mode = params.get("-m");

        validateInputs(carrierFile, stegMethod, password, encAlgorithm, mode);

        if (!new File(carrierFile).exists())
            throw new IllegalArgumentException("El archivo portador no existe: " + carrierFile);

        if (stegMethod == null)
            throw new IllegalArgumentException("Debe especificar el método esteganográfico (-steg LSB1 | LSB4 | LSBI)");

        BMPImage image = new BMPImage(carrierFile);
        IStegoAlgorithm algorithm = StegoUtils.selectAlgorithm(stegMethod);

        byte[] rawExtracted = algorithm.decode(image.getBody());
        byte[] dataBlock;

        if (password != null) {

            if (encAlgorithm == null || mode == null)
                throw new IllegalArgumentException("Debe indicar -a y -m si utiliza -pass");

            if (rawExtracted.length < 4)
                throw new IllegalArgumentException("La imagen no contiene datos cifrados válidos.");

            int cipherLength = StegoUtils.getSizeInBigEndian() ? StegoUtils.bytesToIntBE(rawExtracted) : StegoUtils.bytesToIntLE(rawExtracted);
            byte[] cipherText = new byte[cipherLength];

            if (cipherLength <= 0 || cipherLength > rawExtracted.length - 4)
                throw new IllegalArgumentException("Tamaño de ciphertext inválido.");

            System.arraycopy(rawExtracted, 4, cipherText, 0, cipherLength);
            dataBlock = Encryption.decrypt(cipherText, encAlgorithm, mode, password);

        } else {
            dataBlock = rawExtracted;
        }

        StegoUtils.ExtractedData data = StegoUtils.extractDataBlock(dataBlock);
        String finalOut = outputPath;

        if (data.extension() != null && !data.extension().isEmpty())
            finalOut += data.extension();

        Files.write(Path.of(finalOut), data.fileData());
    }

    private void validateInputs(String carrierFile, String stegMethod,
                                String password, String encAlgorithm, String mode) {

        if (carrierFile == null || !new File(carrierFile).exists())
            throw new IllegalArgumentException("El archivo portador no existe: " + carrierFile);

        if (stegMethod == null)
            throw new IllegalArgumentException("Debe especificar el método esteganográfico (-steg LSB1 | LSB4 | LSBI)");

        if (password != null && (encAlgorithm == null || mode == null))
            throw new IllegalArgumentException("Debe especificar -a y -m si usa '-pass'");
    }
}

