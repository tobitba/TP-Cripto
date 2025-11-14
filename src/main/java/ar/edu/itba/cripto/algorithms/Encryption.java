package ar.edu.itba.cripto.algorithms;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.spec.KeySpec;
import java.util.Arrays;

public class Encryption {

    private static final byte[] SALT = new byte[8];     // 0x0000000000000000
    private static final int PBKDF2_ITERATIONS = 10000;

    private static class KeyAndIV {
        byte[] key;
        byte[] iv;

        KeyAndIV(byte[] key, byte[] iv) { this.key = key; this.iv = iv; }
    }

    private static int blockSizeBits(String algorithm) {
        return switch (algorithm.toLowerCase()) {
            case "aes128", "aes192", "aes256" -> 128;
            case "3des" -> 64;
            default -> throw new IllegalArgumentException("Algoritmo no soportado: " + algorithm);
        };
    }

    private static KeyAndIV deriveKeyAndIV(String algorithm, String mode, String password) throws Exception {

        int keyBits = switch (algorithm.toLowerCase()) {
            case "aes128" -> 128;
            case "aes192" -> 192;
            case "aes256" -> 256;
            case "3des"   -> 192;
            default -> throw new IllegalArgumentException("Algoritmo no soportado: " + algorithm);
        };

        int blockSize = blockSizeBits(algorithm);

        int ivBits = switch (mode.toLowerCase()) {
            case "ecb" -> 0;
            case "cbc", "cfb", "cfb8", "ofb", "ofb8" -> blockSize;
            default -> throw new IllegalArgumentException("Modo no soportado: " + mode);
        };

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), SALT, PBKDF2_ITERATIONS, keyBits + ivBits);

        byte[] derived = factory.generateSecret(spec).getEncoded();
        byte[] key = Arrays.copyOfRange(derived, 0, keyBits / 8);
        byte[] iv  = Arrays.copyOfRange(derived, keyBits / 8, (keyBits + ivBits) / 8);

        return new KeyAndIV(key, iv);
    }

    public static byte[] encrypt(byte[] data, String algorithm, String mode, String password) throws Exception {

        KeyAndIV k = deriveKeyAndIV(algorithm, mode, password);
        Cipher cipher = Cipher.getInstance(normalize(algorithm, mode));
        SecretKeySpec keySpec = new SecretKeySpec(k.key, baseCipherName(algorithm));

        if (k.iv.length == 0)
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        else
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(k.iv));

        return cipher.doFinal(data);
    }

    public static byte[] decrypt(byte[] data, String algorithm, String mode, String password) throws Exception {

        KeyAndIV k = deriveKeyAndIV(algorithm, mode, password);
        Cipher cipher = Cipher.getInstance(normalize(algorithm, mode));
        SecretKeySpec keySpec = new SecretKeySpec(k.key, baseCipherName(algorithm));

        if (k.iv.length == 0)
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
        else
            cipher.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(k.iv));

        return cipher.doFinal(data);
    }

    private static String baseCipherName(String algorithm) {
        return algorithm.equalsIgnoreCase("3des") ? "DESede" : "AES";
    }

    private static String normalize(String algorithm, String mode) {

        String alg = baseCipherName(algorithm);
        String m = mode.toLowerCase();

        if (m.equals("cfb") || m.equals("cfb8")) {
            return alg + "/CFB8/NoPadding";
        }

        if (m.equals("ofb") || m.equals("ofb8")) {
            return alg + "/OFB/NoPadding";
        }

        if (m.equals("cbc")) {
            return alg + "/CBC/PKCS5Padding";
        }

        if (m.equals("ecb")) {
            return alg + "/ECB/PKCS5Padding";
        }

        return alg + "/" + mode.toUpperCase() + "/PKCS5Padding";
    }
}