package com.example.job.portal.Naukri4U.Utility;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Component
public class DataEncrypter {

    private static final String SECRET_KEY = "SERVICE_NAUKRI_4"; // 16 chars = AES 128
    private static final byte[] IV = "123456789012".getBytes();  // 12 bytes static IV

    private static final int TAG_SIZE = 128;

    public String encryptData(String data) throws Exception {

        if (data == null || data.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Empty or null data!");
        }

        SecretKeySpec secretKey = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec parameterSpec = new GCMParameterSpec(TAG_SIZE, IV);

        cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);

        byte[] encryptedBytes = cipher.doFinal(data.getBytes());

        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public String decryptData(String encryptedData) throws Exception {

        SecretKeySpec secretKey = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec parameterSpec = new GCMParameterSpec(TAG_SIZE, IV);

        cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);

        byte[] decodedBytes = Base64.getDecoder().decode(encryptedData);

        byte[] decryptedBytes = cipher.doFinal(decodedBytes);

        return new String(decryptedBytes);
    }

    public static void main(String[] args) throws Exception {
        DataEncrypter encrypter = new DataEncrypter();
        String originalData = "TgLOqmRhXi47YhhUIXrhP3A+C00IX5/L9AdCIuZqVEoQCBFPbFtm1UWGtw==";
        String decryptedData = encrypter.decryptData(originalData);
        System.out.println("Decrypted Data: " + decryptedData);
    }
}
