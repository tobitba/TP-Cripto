package ar.edu.itba.cripto.algorithms;

public class LSB4 implements IStegoAlgorithm {

    @Override
    public byte[] encode(byte[] imagePixels, byte[] dataToHide) {

        int capacityBytes = imagePixels.length / 2;

        if (dataToHide.length > capacityBytes)
            throw new IllegalArgumentException("El archivo es demasiado grande para ocultarse en esta imagen.\nCapacidad máxima: " + capacityBytes + " bytes, Tamaño requerido: " + dataToHide.length + " bytes.");

        byte[] encoded = imagePixels.clone();
        int byteIndex = 0;

        for (byte dataByte : dataToHide) {

            int highNibble = (dataByte >> 4) & 0x0F;
            int lowNibble  = dataByte & 0x0F;

            encoded[byteIndex]     = (byte) ((encoded[byteIndex]     & 0xF0) | highNibble);
            encoded[byteIndex + 1] = (byte) ((encoded[byteIndex + 1] & 0xF0) | lowNibble);
            byteIndex += 2;
        }

        return encoded;
    }

    @Override
    public byte[] decode(byte[] imagePixels) {

        int dataLength = imagePixels.length / 2;
        byte[] extracted = new byte[dataLength];
        int decoIndex = 0;

        for (int encoIndex = 0; encoIndex < imagePixels.length; encoIndex += 2) {

            int highNibble = imagePixels[encoIndex]     & 0x0F;
            int lowNibble  = imagePixels[encoIndex + 1] & 0x0F;

            extracted[decoIndex++] = (byte) (((highNibble << 4) & 0xF0) | lowNibble);
        }

        return extracted;
    }
}
