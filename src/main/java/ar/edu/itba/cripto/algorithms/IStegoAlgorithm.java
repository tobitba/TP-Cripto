package ar.edu.itba.cripto.algorithms;

public interface IStegoAlgorithm {

    byte[] encode(byte[] imagePixels, byte[] dataToHide);

    byte[] decode(byte[] imagePixels);
}