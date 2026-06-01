package TomasCepukas_PI24SN_IS_5Uzduotis.service;

import java.security.SecureRandom;

public class PasswordGeneratorService {

    private static final String SYMBOLS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+[]{}";

    private final SecureRandom random = new SecureRandom();

    public String generatePassword(int length) {
        StringBuilder password = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(SYMBOLS.length());
            password.append(SYMBOLS.charAt(index));
        }

        return password.toString();
    }
}