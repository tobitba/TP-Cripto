package ar.edu.itba.cripto.utils;

import ar.edu.itba.cripto.algorithms.IStegoAlgorithm;
import ar.edu.itba.cripto.algorithms.LSB1;
import ar.edu.itba.cripto.algorithms.LSB4;
import ar.edu.itba.cripto.algorithms.LSBI;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class StegoUtils {

    private StegoUtils() {  }

    public record ExtractedData(byte[] fileData, String extension) {  }

    public static byte[] intToBytes(int value) {
        return new byte[] { (byte) (value >> 24), (byte) (value >> 16), (byte) (value >> 8), (byte) value };
    }

    public static int bytesToInt(byte[] bytes) {
        return ((bytes[0] & 0xFF) << 24) | ((bytes[1] & 0xFF) << 16) | ((bytes[2] & 0xFF) << 8) | (bytes[3] & 0xFF);
    }

    public static byte[] buildDataBlock(String filePath) throws IOException {
        String extension = getFileExtension(filePath);

        byte[] extensionBytes = (("." + extension) + "\0").getBytes(StandardCharsets.UTF_8);
        byte[] fileData = Files.readAllBytes(Path.of(filePath));
        byte[] sizeBytes = intToBytes(fileData.length);
        byte[] data = new byte[sizeBytes.length + fileData.length + extensionBytes.length];

        System.arraycopy(sizeBytes, 0, data, 0, sizeBytes.length);
        System.arraycopy(fileData, 0, data, sizeBytes.length, fileData.length);
        System.arraycopy(extensionBytes, 0, data, sizeBytes.length + fileData.length, extensionBytes.length);

        return data;
    }

    private static String getFileExtension(String filePath) {
        int dotIndex = filePath.lastIndexOf('.');

        return (dotIndex == -1 || dotIndex == filePath.length() - 1) ? "" : filePath.substring(dotIndex + 1);
    }


    public static ExtractedData extractDataBlock(byte[] hiddenData) {
        int size = bytesToInt(Arrays.copyOfRange(hiddenData, 0, 4));
        byte[] fileData = Arrays.copyOfRange(hiddenData, 4, 4 + size);
        int extensionStartPosition = 4 + size;
        int extensionEndPosition = extensionStartPosition;

        while (extensionEndPosition < hiddenData.length && hiddenData[extensionEndPosition] != 0)
        { extensionEndPosition++; }

        String extension = new String(Arrays.copyOfRange(hiddenData, extensionStartPosition, extensionEndPosition), StandardCharsets.UTF_8);

        return new ExtractedData(fileData, extension);
    }

    public static IStegoAlgorithm selectAlgorithm(String method) {
        return switch (method.toUpperCase()) {
            case "LSB1" -> new LSB1();
            case "LSB4" -> new LSB4();
            case "LSBI" -> new LSBI();
            default -> throw new IllegalArgumentException("Método no soportado: " + method);
        };
    }
}
