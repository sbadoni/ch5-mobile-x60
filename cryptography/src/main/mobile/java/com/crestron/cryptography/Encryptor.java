package com.crestron.cryptography;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 * Obfuscates strings to be stored in the Android Keystore by aliases (our control sys ids).
 */
public class Encryptor {

    /**
     * encrypts a string using Base64
     *
     * @param byteToEncrypt the string that is wanted to encrypt
     * @return the encrypted string
     */
    private static String base64Encode(final byte[] byteToEncrypt) {

        /* Obfuscate the str with Base64 */
        return Base64.encodeToString(byteToEncrypt, Base64.NO_WRAP);
    }

    /**
     * encrypts wanted text
     *
     * @param securityKey   the byte array of the wanted key (Usually obtained using {@link String#getBytes(Charset)} and using UTF-8 for the charset
     * @param textToEncrypt the text to encrypt
     * @return the encrypted text
     */
    public static String encryptText(final byte[] securityKey, final String textToEncrypt) {
        try {
            final Cipher cipher = Cipher.getInstance(SecurityMethods.TRANSFORMATION);

            byte[] key = securityKey;
            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16); // use only first 128 bit

            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");

            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);

            byte[] iv = cipher.getIV();
            byte[] encryption = cipher.doFinal(textToEncrypt.getBytes(SecurityMethods.UTF));

            return base64Encode(encryption) + "!" + base64Encode(iv);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

}
