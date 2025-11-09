package ar.edu.itba.cripto.algorithms;

import ar.edu.itba.cripto.BMPImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class LSB1 implements IStegoAlgorithm {

    public BMPImage encode(BMPImage image, String fileToHide) throws IOException {
        byte[] data = Files.readAllBytes(Path.of(fileToHide));
        int capacity = image.getCapacityBytesForLSB1();

        if (data.length + 4 > capacity)
            throw new IllegalArgumentException("El archivo es demasiado grande para ocultarse en esta imagen.\n Capacidad máxima: " + capacity + " bytes, Tamaño del archivo: " + data.length + " bytes.");

        byte[] body = image.getBody().clone();
        byte[] lengthBytes = StegoUtils.intToBytes(data.length);
        byte[] dataToHide = new byte[lengthBytes.length + data.length];

        System.arraycopy(lengthBytes, 0, dataToHide, 0, lengthBytes.length);
        System.arraycopy(data, 0, dataToHide, lengthBytes.length, data.length);

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

    public void decode(BMPImage image, String outputPath) throws IOException {
        // TODO!
    }
}