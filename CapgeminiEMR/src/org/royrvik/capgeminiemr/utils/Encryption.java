package org.royrvik.capgeminiemr.utils;

import android.content.Context;

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
 * Created by Joakim on 26.03.2014.
 */
public class Encryption {

    private final static String ALGORITHM = "AES";
    private final static String SALT = "2490KAVN3Q4RKVAE";
    private final static String HEX = "0123456789ABCDEF";

    /**
     * Encrypts the data string, using the username and context to generate the secret key
     * @param username
     * @param data
     * @return
     */
    public static String encrypt(String username, String data) {
        Cipher cipher;
        SecretKey key = generateSecretKey(username);
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
     * Decrypts the data string, using the username and context to generate the secret key
     * @param username
     * @param data
     * @return
     */
    public static String decrypt(String username, String data) {
        Cipher cipher;
        SecretKey key = generateSecretKey(username);
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
     *
     * @param hexString
     * @return
     */
    private static  byte[] toByte(String hexString) {
        int len = hexString.length()/2;

        byte[] result = new byte[len];

        for (int i = 0; i < len; i++)
            result[i] = Integer.valueOf(hexString.substring(2*i, 2*i+2), 16).byteValue();
        return result;
    }

    /**
     *
     * @param stringBytes
     * @return
     */
    private static String toHex(byte[] stringBytes) {
        StringBuffer result = new StringBuffer(2*stringBytes.length);

        for (int i = 0; i < stringBytes.length; i++) {
            result.append(HEX.charAt((stringBytes[i]>>4)&0x0f)).append(HEX.charAt(stringBytes[i]&0x0f));
        }

        return result.toString();
    }

    /**
     * Generates a new {@link SecretKey} using the username string and the chosen algorithm.
     * @param username - a String to digest and use as salt
     */
    private static SecretKey generateSecretKey(String username) {
        byte[] bytes;
        try {
            bytes = (SALT + username).getBytes("UTF-8");
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
