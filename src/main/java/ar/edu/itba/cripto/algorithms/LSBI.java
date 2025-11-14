package ar.edu.itba.cripto.algorithms;

public class LSBI implements IStegoAlgorithm {

    private static final int GROUPS = 4;

    @Override
    public byte[] encode(byte[] imagePixels, byte[] dataToHide) {

        int capacity = imagePixels.length / 8;
        byte[] encoded = imagePixels.clone();

        if (dataToHide.length + 1 > capacity)
            throw new IllegalArgumentException("No hay suficiente capacidad en la imagen para LSBI.");

        int[] pos = new int[GROUPS];    // Clasificación y detección de inversión por grupo
        int[] neg = new int[GROUPS];
        int imgIndex = 4;               // Primeros 4 bytes para los flags (uno por grupo)

        // Calcular pos/neg para cada grupo
        for (byte dataByte : dataToHide) {
            for (int bit = 7; bit >= 0; bit--) {

                int messageBit = (dataByte >> bit) & 1;
                int group = encoded[imgIndex] & 0b11;
                int currentLSB = encoded[imgIndex] & 1;

                if (messageBit == 1 && currentLSB == 0)
                    pos[group]++;
                else if (messageBit == 0 && currentLSB == 1)
                    neg[group]++;

                imgIndex++;
            }
        }

        byte[] invertGroup = new byte[GROUPS];

        for (int g = 0; g < GROUPS; g++) {   // Determinar inversiones
            invertGroup[g] = (byte) ((neg[g] > pos[g]) ? 1 : 0);
        }

        imgIndex = 4;
        byte[] realEncoded = imagePixels.clone();

        for (byte dataByte : dataToHide) {
            for (int bit = 7; bit >= 0; bit--) {

                int b = (dataByte >> bit) & 1;
                int group = realEncoded[imgIndex] & 0b11;

                if (invertGroup[group] == 1)
                { b = 1 - b; }              // invertir bit

                realEncoded[imgIndex] = (byte) ((realEncoded[imgIndex] & 0xFE) | b);
                imgIndex++;
            }
        }

        System.arraycopy(invertGroup, 0, realEncoded, 0, GROUPS);   // Guardar flags

        return realEncoded;
    }

    @Override
    public byte[] decode(byte[] imagePixels) {

        byte[] invertGroup = new byte[GROUPS];

        System.arraycopy(imagePixels, 0, invertGroup, 0, GROUPS);

        int imgIndex = 4;
        int extractedByteCount = (imagePixels.length - 4) / 8;
        byte[] extracted = new byte[extractedByteCount];
        int byteIndex = 0;
        int bitCount = 0;

        while (imgIndex < imagePixels.length) {

            int group = imagePixels[imgIndex] & 0b11;
            int bit = imagePixels[imgIndex] & 1;

            if (invertGroup[group] == 1)
            { bit = 1 - bit; }

            extracted[byteIndex] = (byte) ((extracted[byteIndex] << 1) | bit);
            bitCount++;

            if (bitCount == 8) {
                bitCount = 0;
                byteIndex++;

                if (byteIndex >= extracted.length)
                { break; }
            }

            imgIndex++;
        }

        return extracted;
    }
}
