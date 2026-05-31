package TomasCepukas_PI24SN_IS_5Uzduotis.model;

import javax.crypto.SecretKey;

public class AuthenticatedUser {

    private final String username;
    private final SecretKey vaultKey;

    public AuthenticatedUser(String username, SecretKey vaultKey) {
        this.username = username;
        this.vaultKey = vaultKey;
    }

    public String getUsername() {
        return username;
    }

    public SecretKey getVaultKey() {
        return vaultKey;
    }
}