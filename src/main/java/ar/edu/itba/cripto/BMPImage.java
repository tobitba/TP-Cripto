package ar.edu.itba.cripto;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

public class BMPImage {

    private static final int BMP_HEADER_LENGTH = 54;
    private byte[] header;
    private byte[] body;
    private int width;
    private int height;
    private int bitsPerPixel;
    private int compression;

    public BMPImage(String path) throws IOException {

        try (RandomAccessFile file = new RandomAccessFile(path, "r")) {

            header = new byte[BMP_HEADER_LENGTH];
            file.readFully(header);

            int dataOffset = readIntLE(header, 10);

            width = readIntLE(header, 18);
            height = readIntLE(header, 22);
            bitsPerPixel = readShortLE(header, 28);
            compression = readIntLE(header, 30);

            if (compression != 0)
                throw new IllegalArgumentException("Solo se admiten BMP sin compresión (compression=0).");

            if (bitsPerPixel != 24)
                throw new IllegalArgumentException("Solo se admiten BMP de 24 bits por píxel.");

            long pixelLen = file.length() - dataOffset;
            body = new byte[(int) pixelLen];
            file.seek(dataOffset);
            file.readFully(body);
        }
    }

    private int readIntLE(byte[] buffer, int offset) {
        return (buffer[offset] & 0xFF) |
                ((buffer[offset + 1] & 0xFF) << 8) |
                ((buffer[offset + 2] & 0xFF) << 16) |
                ((buffer[offset + 3] & 0xFF) << 24);
    }

    private int readShortLE(byte[] buffer, int offset) {
        return (buffer[offset] & 0xFF) |
                ((buffer[offset + 1] & 0xFF) << 8);
    }

    public byte[] getHeader() { return header; }
    public byte[] getBody() { return body; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int getBitsPerPixel() { return bitsPerPixel; }
    public int getCompression() { return compression; }

    public void setBody(byte[] data) {

        if (data.length != body.length)
            throw new IllegalArgumentException("El nuevo cuerpo debe tener el mismo tamaño que el original.");

        this.body = data;
    }

    public void save(String path) throws IOException {

        try (FileOutputStream output = new FileOutputStream(path)) {
            output.write(header);
            output.write(body);
        }
    }

    @Override
    public String toString() {

        return String.format("BMPImage [width=%d, height=%d, bpp=%d, compression=%d, bodySize=%d]",
                width, height, bitsPerPixel, compression, body.length);
    }
}
