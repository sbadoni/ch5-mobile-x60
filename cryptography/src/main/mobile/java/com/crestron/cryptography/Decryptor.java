package com.crestron.cryptography;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * De-obfuscates strings that are stored in the Android Keystore by aliases (our control sys ids).
 */
public class Decryptor {

    /**
     * decrypts a base64 string
     *
     * @param encryptedStr the encrypted string
     * @return the decoded string
     * @throws UnsupportedEncodingException if there is an encoding issue
     */
    private static byte[] base64Decode(final String encryptedStr) throws UnsupportedEncodingException {
        return Base64.decode(encryptedStr.getBytes(SecurityMethods.UTF), Base64.DEFAULT);
    }

    /**
     * decrypts a string based off of the securityKey
     *
     * @param securityKey   the byte array of the wanted key (Usually obtained using {@link String#getBytes(Charset)} and using UTF-8 for the charset
     * @param encryptedData the encrypted data
     * @return the decrypted string
     */
    public static String decryptData(final byte[] securityKey, final String encryptedData) {

        String[] dat = encryptedData.split("!");

        byte[] ivBytes = null;
        byte[] dataBytes = null;

        try {
            dataBytes = base64Decode(dat[0]);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            ivBytes = base64Decode(dat[1]);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            final Cipher cipher = Cipher.getInstance(SecurityMethods.TRANSFORMATION);
            IvParameterSpec ips = new IvParameterSpec(ivBytes);

            byte[] key = securityKey;
            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16); // use only first 128 bit

            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");

            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ips);

            return new String(cipher.doFinal(dataBytes));
        } catch (BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException | InvalidKeyException | NoSuchAlgorithmException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        return null;

    }

}