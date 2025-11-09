package ar.edu.itba.cripto.algorithms;

import ar.edu.itba.cripto.BMPImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class LSB4 implements IStegoAlgorithm {

    public BMPImage encode(BMPImage image, String fileToHide) throws IOException {
        byte[] data = Files.readAllBytes(Path.of(fileToHide));
        int capacity = image.getCapacityBytesForLSB4();

        if (data.length + 4 > capacity)
            throw new IllegalArgumentException("El archivo es demasiado grande para ocultarse en esta imagen.\nCapacidad máxima: " + capacity + " bytes, Tamaño del archivo: " + data.length + " bytes.");

        byte[] body = image.getBody().clone();
        byte[] lengthBytes = StegoUtils.intToBytes(data.length);
        byte[] dataToHide = new byte[lengthBytes.length + data.length];

        System.arraycopy(lengthBytes, 0, dataToHide, 0, lengthBytes.length);
        System.arraycopy(data, 0, dataToHide, lengthBytes.length, data.length);

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

    public void decode(BMPImage image, String outputPath) throws IOException {
        // TODO!
    }
}