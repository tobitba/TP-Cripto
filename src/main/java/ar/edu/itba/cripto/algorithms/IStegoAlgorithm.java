package ar.edu.itba.cripto.algorithms;

import ar.edu.itba.cripto.BMPImage;
import java.io.IOException;

public interface IStegoAlgorithm {

    BMPImage encode(BMPImage image, byte[] dataToHide) throws IOException;

    void decode(BMPImage image, String outputPath) throws IOException;
}