package ar.edu.itba.cripto.algorithms;

import ar.edu.itba.cripto.BMPImage;
import ar.edu.itba.cripto.StegoUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class LSB4 implements IStegoAlgorithm {

    @Override
    public BMPImage encode(BMPImage image, byte[] dataToHide) throws IOException {
        int capacity = image.getCapacityBytesForLSB4();

        if (dataToHide.length > capacity) {
            throw new IllegalArgumentException("El archivo es demasiado grande para ocultarse en esta imagen.\nCapacidad máxima: " + capacity + " bytes, Tamaño requerido: " + dataToHide.length + " bytes.");
        }
        byte[] body = image.getBody().clone();
        int byteIndex = 0;

        for (byte dataByte : dataToHide) {
            int highNibble = (dataByte >> 4) & 0x0F;
            int lowNibble = dataByte & 0x0F;

            body[byteIndex] = (byte) ((body[byteIndex] & 0xF0) | highNibble);
            body[byteIndex + 1] = (byte) ((body[byteIndex + 1] & 0xF0) | lowNibble);
            byteIndex += 2;
        }

        image.setBody(body);

        return image;
    }

    @Override
    public void decode(BMPImage image, String outputPath) throws IOException {
        byte[] body = image.getBody();
        int dataLength = body.length / 2;
        byte[] extracted = new byte[dataLength];
        int byteIndex = 0;

        for (int i = 0; i < body.length; i += 2) {
            int highNibble = body[i] & 0x0F;
            int lowNibble = body[i + 1] & 0x0F;

            extracted[byteIndex++] = (byte) ((highNibble << 4) | lowNibble);
        }

        StegoUtils.ExtractedData data = StegoUtils.extractDataBlock(extracted);
        String finalPath = outputPath;

        if (data.extension() != null && !data.extension().isEmpty()) {
            finalPath += data.extension();
        }

        Files.write(Path.of(finalPath), data.fileData());
    }
}