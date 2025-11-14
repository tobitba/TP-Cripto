package ar.edu.itba.cripto.algorithms;

public class LSBI implements IStegoAlgorithm {

    @Override
    public byte[] encode(byte[] imagePixels, byte[] dataToHide) {
        return new byte[0];
    }

    @Override
    public byte[] decode(byte[] imagePixels) {
        return new byte[0];
    }
}
