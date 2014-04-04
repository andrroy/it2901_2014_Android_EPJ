package org.royrvik.capgeminiemr.utils;

import java.util.Arrays;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.BadPaddingException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.IllegalBlockSizeException;

/**
 * Created by Joakim.
 */
public class Encryption {

    private final static String ALGORITHM = "AES";
    private final static String SALT = "2490KAVN3Q4RKVAE";
    private final static String HEX = "0123456789ABCDEF";

    /**
     * Encrypts the data string, using the salt string and a static SALT to generate the secret key.
     * @param saltString A {@linkplain java.lang.String} with SALT to create a {@linkplain javax.crypto.SecretKey}.
     * @param data The {@linkplain String} to encrypt.
     * @return A {@linkplain java.lang.String} with the encrypted data.
     */
    public static String encrypt(String saltString, String data) {
        Cipher cipher;
        SecretKey key = generateSecretKey(saltString);
        try {
            cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return toHex(cipher.doFinal(data.getBytes()));
        } catch ( BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Decrypts the data string, using the salt string and a static SALT to generate the secret key.
     * @param saltString A {@linkplain java.lang.String} with SALT to create a {@linkplain javax.crypto.SecretKey}.
     * @param data The {@linkplain String} to decrypt.
     * @return A {@linkplain java.lang.String} with the decrypted data.
     */
    public static String decrypt(String saltString, String data) {
        Cipher cipher;
        SecretKey key = generateSecretKey(saltString);
        try {
            cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);
            return new String(cipher.doFinal(toByte(data)));
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * A helper function for the decrypt method.
     * @param hexString The {@linkplain java.lang.String} to convert.
     * @return The generated byte array.
     */
    private static  byte[] toByte(String hexString) {
        int len = hexString.length()/2;

        byte[] result = new byte[len];

        for (int i = 0; i < len; i++)
            result[i] = Integer.valueOf(hexString.substring(2*i, 2*i+2), 16).byteValue();
        return result;
    }

    /**
     * A helper function for the encrypt method.
     * @param stringBytes The byte array to convert.
     * @return The generated {@linkplain String}.
     */
    private static String toHex(byte[] stringBytes) {
        StringBuilder result = new StringBuilder(2*stringBytes.length);

        for (byte stringByte : stringBytes) {
            result.append(HEX.charAt((stringByte >> 4) & 0x0f)).append(HEX.charAt(stringByte & 0x0f));
        }

        return result.toString();
    }

    /**
     * Generates a new {@link SecretKey} using the saltString, static salt and the chosen algorithm.
     * @param saltString - a String to digest and use as salt
     */
    private static SecretKey generateSecretKey(String saltString) {
        byte[] bytes;
        try {
            bytes = (SALT + saltString).getBytes("UTF-8");
            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            bytes = sha.digest(bytes);
            bytes = Arrays.copyOf(bytes, 16); // use only first 128 bit
            return new SecretKeySpec(bytes, ALGORITHM);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}
