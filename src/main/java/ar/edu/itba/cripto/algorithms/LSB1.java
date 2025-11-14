package ar.edu.itba.cripto.algorithms;

public class LSB1 implements IStegoAlgorithm {

    @Override
    public byte[] encode(byte[] imagePixels, byte[] dataToHide) {

        int imageDataCapacityBytes = imagePixels.length / 8;

        if (dataToHide.length > imageDataCapacityBytes)
            throw new IllegalArgumentException("El archivo es demasiado grande para ocultarse en esta imagen.\nCapacidad máxima: " + imageDataCapacityBytes + " bytes, Tamaño requerido: " + dataToHide.length + " bytes.");

        byte[] encoded = new byte[imagePixels.length];
        int byteIndex = 0;

        for (byte dataByte : dataToHide) {
            for (int bit = 7; bit >= 0; bit--) {
                int messageBit = (dataByte >> bit) & 1;
                encoded[byteIndex] = (byte) ((imagePixels[byteIndex] & 0xFE) | messageBit);
                byteIndex++;
            }
        }

        return encoded;
    }

    @Override
    public byte[] decode(byte[] imagePixels) {

        byte[] extracted = new byte[imagePixels.length / 8];
        int byteIndex = 0;
        int bitCount = 0;

        for (byte dataByte : imagePixels) {
            int bit = dataByte & 1;
            extracted[byteIndex] = (byte) ((extracted[byteIndex] << 1) | bit);
            bitCount++;

            if (bitCount == 8) {
                bitCount = 0;
                byteIndex++;

                if (byteIndex >= extracted.length)
                { break; }
            }
        }

        return extracted;
    }
}