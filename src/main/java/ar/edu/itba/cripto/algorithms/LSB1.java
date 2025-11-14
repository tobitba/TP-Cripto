package ar.edu.itba.cripto.algorithms;

import ar.edu.itba.cripto.BMPImage;
import ar.edu.itba.cripto.StegoUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class LSB1 implements IStegoAlgorithm {

    @Override
    public BMPImage encode(BMPImage image, byte[] dataToHide) throws IOException {

        int capacity = image.getCapacityBytesForLSB1();

        if (dataToHide.length > capacity)
            throw new IllegalArgumentException("El archivo es demasiado grande para ocultarse en esta imagen.\nCapacidad máxima: " + capacity + " bytes, Tamaño requerido: " + dataToHide.length + " bytes.");

        byte[] body = image.getBody().clone();
        int byteIndex = 0;

        for (byte dataByte : dataToHide) {
            for (int bit = 7; bit >= 0; bit--) {
                int messageBit = (dataByte >> bit) & 1;
                body[byteIndex] = (byte) ((body[byteIndex] & 0xFE) | messageBit);
                byteIndex++;
            }
        }

        image.setBody(body);

        return image;
    }

    @Override
    public void decode(BMPImage image, String outputPath) throws IOException {

        byte[] body = image.getBody();
        byte[] extracted = new byte[body.length / 8];
        int byteIndex = 0;
        int bitCount = 0;

        for (byte _byte_ : body) {
            int bit = _byte_ & 1;
            extracted[byteIndex] = (byte) ((extracted[byteIndex] << 1) | bit);
            bitCount++;

            if (bitCount == 8) {
                bitCount = 0;
                byteIndex++;

                if (byteIndex >= extracted.length)
                    break;
            }
        }
        StegoUtils.ExtractedData data = StegoUtils.extractDataBlock(extracted);
        String finalPath = outputPath;

        if (data.extension() != null && !data.extension().isEmpty())
            finalPath += data.extension();

        Files.write(Path.of(finalPath), data.fileData());
    }
}