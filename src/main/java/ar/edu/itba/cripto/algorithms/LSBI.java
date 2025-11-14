package ar.edu.itba.cripto.algorithms;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class LSBI implements IStegoAlgorithm {

    private static final int PATTERN_BIT_MASK = 0b00000110;
    private static final int PATTERNS = 4;

    @Override
    public byte[] encode(byte[] imagePixels, byte[] dataToHide) {

        int capacityBytes = ((imagePixels.length - PATTERNS ) * 2) / 3 / 8; // Pixeles rojos no codifican data

        if (dataToHide.length > capacityBytes)
            throw new IllegalArgumentException("El archivo es demasiado grande para ocultarse en esta imagen.\nCapacidad máxima: " + capacityBytes + " bytes, Tamaño requerido: " + dataToHide.length + " bytes.");

        SortedMap<Integer, Integer> patternChangeCount = new TreeMap<>();
        Map<Integer, Boolean> patternInvertCondition = new HashMap<>();
        byte[] encoded = new byte[imagePixels.length];
        int imagePixelsIndex = PATTERNS;

        for (byte dataByte : dataToHide) {                  // Sacar métricas para cada patrón de bits
            for (int bit = 7; bit >= 0; bit--) {

                if (imagePixelsIndex % 3 == 2)
                { imagePixelsIndex++; }                     // Salteamos los pixeles ROJOS

                int dataBit = (dataByte >> bit) & 1;
                int pattern = (imagePixels[imagePixelsIndex] & PATTERN_BIT_MASK) >> 1;

                if (dataBit != (imagePixels[imagePixelsIndex] & 1))
                { patternChangeCount.put(pattern, patternChangeCount.getOrDefault(pattern, 0) + 1); }

                imagePixelsIndex++;
            }
        }

        for (Map.Entry<Integer, Integer> entry : patternChangeCount.entrySet()) {
            Boolean patternInvertConditionValue = entry.getValue() > capacityBytes / 2 / PATTERNS;
            patternInvertCondition.put(entry.getKey(), patternInvertConditionValue);
        }

        imagePixelsIndex = PATTERNS;

        for (byte dataByte : dataToHide) {
            for (int bit = 7; bit >= 0; bit--) {

                if (imagePixelsIndex % 3 == 2)
                { imagePixelsIndex++; }                     // Salteamos los pixeles ROJOS

                int dataBit = (dataByte >> bit) & 1;
                int pattern = (encoded[imagePixelsIndex] & PATTERN_BIT_MASK) >> 1;

                if (patternInvertCondition.getOrDefault(pattern, false))
                { dataBit = 1 - dataBit; }

                encoded[imagePixelsIndex] = (byte) ((imagePixels[imagePixelsIndex] & 0xFE) | dataBit);
                imagePixelsIndex++;
            }
        }

        imagePixelsIndex = 0;

        for (Map.Entry<Integer, Integer> entry : patternChangeCount.entrySet()) {
            int invertedDataBit = patternInvertCondition.getOrDefault(entry.getKey(), false) ? 1 : 0;
            encoded[imagePixelsIndex] = (byte) ((encoded[imagePixelsIndex] & 0xFE) | invertedDataBit);
            imagePixelsIndex++;
        }

        return encoded;
    }

    @Override
    public byte[] decode(byte[] imagePixels) {

        Map<Integer, Boolean> patternInvertCondition = new HashMap<>();
        int imagePixelsIndex = 0;

        for (; imagePixelsIndex < PATTERNS; imagePixelsIndex++) {
            patternInvertCondition.put(imagePixelsIndex, (imagePixels[imagePixelsIndex] & 1) == 1);
        }

        int capacityBytes = ((imagePixels.length - PATTERNS ) * 2) / 3 / 8; // Pixeles rojos no codifican data
        byte[] extracted = new byte[capacityBytes];
        int dataPos = 0;
        int bitsBuffer = 0;
        int bitCount = 0;

        while (dataPos < capacityBytes) {

            if (imagePixelsIndex % 3 == 2)
            { imagePixelsIndex++; }

            int pixel = imagePixels[imagePixelsIndex];
            int pattern = (pixel & PATTERN_BIT_MASK) >> 1;
            int dataBit = pixel & 1;

            if (patternInvertCondition.getOrDefault(pattern, false))
            { dataBit ^= 1; }

            bitsBuffer = (bitsBuffer << 1) | dataBit;
            bitCount++;

            if (bitCount == 8) {
                extracted[dataPos++] = (byte) bitsBuffer;
                bitsBuffer = 0;
                bitCount = 0;
            }

            imagePixelsIndex++;
        }

        return extracted;
    }
}
