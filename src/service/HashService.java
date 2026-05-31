package TomasCepukas_PI24SN_IS_5Uzduotis.service;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

public class HashService {

    private static final int SALT_LENGTH = 16;
    private static final int ITERATIONS = 120000;
    private static final int KEY_LENGTH = 256;

    public byte[] generateSalt() {
        byte[] salt = new byte[SALT_LENGTH];

        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(salt);

        return salt;
    }

    public byte[] hashPassword(String password, byte[] salt) throws Exception {
        PBEKeySpec spec = new PBEKeySpec(
                password.toCharArray(),
                salt,
                ITERATIONS,
                KEY_LENGTH
        );

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

        return factory.generateSecret(spec).getEncoded();
    }

    public boolean verifyPassword(String password, byte[] salt, byte[] expectedHash) throws Exception {
        byte[] actualHash = hashPassword(password, salt);

        return Arrays.equals(actualHash, expectedHash);
    }

    public SecretKey deriveAesKey(String password, byte[] salt) throws Exception {
        byte[] keyBytes = hashPassword(password, salt);

        return new SecretKeySpec(keyBytes, "AES");
    }

    public String toBase64(byte[] value) {
        return Base64.getEncoder().encodeToString(value);
    }

    public byte[] fromBase64(String value) {
        return Base64.getDecoder().decode(value);
    }
}