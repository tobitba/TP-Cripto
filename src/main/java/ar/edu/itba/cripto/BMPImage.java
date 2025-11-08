package ar.edu.itba.cripto;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

public class BMPImage {
    private final int BMP_HEADER_LENGHT = 54;
    private byte[] header;
    private byte[] body;
    private int width;
    private int height;
    private int bitsPerPixel;
    private int compression;

    public BMPImage(String path) throws IOException {
        try (RandomAccessFile file = new RandomAccessFile(path, "r")) {
            header = new byte[BMP_HEADER_LENGHT];
            file.readFully(header);

            int dataOffset = (header[13] << 24) | (header[12] << 16) | (header[11] << 8) | header[10];
            long pixelLen = file.length() - dataOffset;

            body = new byte[(int) pixelLen];
            file.seek(dataOffset);
            file.readFully(body);

            width = (header[21] << 24) | (header[20] << 16) | (header[19] << 8) | header[18];
            height = (header[25] << 24) | (header[24] << 16) | (header[23] << 8) | header[22] ;
            bitsPerPixel = (header[29] << 8) | header[28];
            compression = (header[33] << 24) | (header[32] << 16) | (header[31] << 8) | header[30];
        }
    }

    public byte[] getHeader() { return header; }
    public byte[] getBody() { return body; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int getBitsPerPixel() { return bitsPerPixel; }
    public int getCompression() { return compression; }
    public int getCapacityBytesForLSB1() { return (body.length) / 8; }
    public int getCapacityBytesForLSB4() { return (body.length * 4) / 8; }

    public void setBody(byte[] data) { this.body = data; }

    public void save(String path) throws IOException {
        try (FileOutputStream output = new FileOutputStream(path)) {
            output.write(header);
            output.write(body);
        }
    }
}