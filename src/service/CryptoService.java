package TomasCepukas_PI24SN_IS_5Uzduotis.service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

public class CryptoService {

    private static final String AES_MODE = "AES/GCM/NoPadding";
    private static final int IV_LENGTH = 12;
    private static final int TAG_LENGTH = 128;

    public String encrypt(String plainText, SecretKey key) throws Exception {
        byte[] iv = new byte[IV_LENGTH];

        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(iv);

        Cipher cipher = Cipher.getInstance(AES_MODE);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(TAG_LENGTH, iv);

        cipher.init(Cipher.ENCRYPT_MODE, key, parameterSpec);

        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

        String ivBase64 = Base64.getEncoder().encodeToString(iv);
        String encryptedBase64 = Base64.getEncoder().encodeToString(encryptedBytes);

        return ivBase64 + ":" + encryptedBase64;
    }

    public String decrypt(String encryptedText, SecretKey key) throws Exception {
        if (encryptedText == null || encryptedText.isBlank()) {
            return "";
        }

        String[] parts = encryptedText.split(":", -1);

        if (parts.length != 2) {
            throw new IllegalArgumentException("Neteisingas sifruoto teksto formatas");
        }

        byte[] iv = Base64.getDecoder().decode(parts[0]);
        byte[] encryptedBytes = Base64.getDecoder().decode(parts[1]);

        Cipher cipher = Cipher.getInstance(AES_MODE);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(TAG_LENGTH, iv);

        cipher.init(Cipher.DECRYPT_MODE, key, parameterSpec);

        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }
}