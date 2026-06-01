package TomasCepukas_PI24SN_IS_5Uzduotis.service;

import TomasCepukas_PI24SN_IS_5Uzduotis.model.AuthenticatedUser;
import TomasCepukas_PI24SN_IS_5Uzduotis.model.User;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class UserService {

    private static final Path DATA_DIR = Path.of("data");
    private static final Path VAULTS_DIR = Path.of("data", "vaults");
    private static final Path USERS_FILE = Path.of("data", "users.txt");

    private final HashService hashService = new HashService();

    public UserService() {
        try {
            Files.createDirectories(DATA_DIR);
            Files.createDirectories(VAULTS_DIR);

            if (!Files.exists(USERS_FILE)) {
                Files.createFile(USERS_FILE);
            }
        } catch (Exception e) {
            throw new RuntimeException("Nepavyko paruosti duomenu aplanko", e);
        }
    }

    public void register(String username, String password) throws Exception {
        validateUsernameAndPassword(username, password);

        if (findUser(username) != null) {
            throw new IllegalArgumentException("Toks vartotojas jau egzistuoja");
        }

        byte[] salt = hashService.generateSalt();
        byte[] hash = hashService.hashPassword(password, salt);

        User user = new User(
                username,
                hashService.toBase64(salt),
                hashService.toBase64(hash)
        );

        Files.writeString(
                USERS_FILE,
                user.toFileLine() + System.lineSeparator(),
                StandardCharsets.UTF_8,
                java.nio.file.StandardOpenOption.APPEND
        );

        SecretKey vaultKey = hashService.deriveAesKey(password, salt);

        VaultService vaultService = new VaultService(
                new AuthenticatedUser(username, vaultKey)
        );

        vaultService.createEmptyVaultIfNotExists();
    }

    public AuthenticatedUser login(String username, String password) throws Exception {
        validateUsernameAndPassword(username, password);

        User user = findUser(username);

        if (user == null) {
            throw new IllegalArgumentException("Vartotojas nerastas");
        }

        byte[] salt = hashService.fromBase64(user.getSaltBase64());
        byte[] expectedHash = hashService.fromBase64(user.getPasswordHashBase64());

        boolean passwordCorrect = hashService.verifyPassword(password, salt, expectedHash);

        if (!passwordCorrect) {
            throw new IllegalArgumentException("Neteisingas slaptazodis");
        }

        SecretKey vaultKey = hashService.deriveAesKey(password, salt);

        return new AuthenticatedUser(username, vaultKey);
    }

    private User findUser(String username) throws Exception {
        List<String> lines = Files.readAllLines(USERS_FILE, StandardCharsets.UTF_8);

        for (String line : lines) {
            if (!line.isBlank()) {
                User user = User.fromFileLine(line);

                if (user.getUsername().equalsIgnoreCase(username)) {
                    return user;
                }
            }
        }

        return null;
    }

    private void validateUsernameAndPassword(String username, String password) {
        if (username == null || username.trim().isBlank()) {
            throw new IllegalArgumentException("Vartotojo vardas negali buti tuscias");
        }

        if (!username.matches("[a-zA-Z0-9_]+")) {
            throw new IllegalArgumentException("Vartotojo varde naudokite tik raides skaicius arba pabraukima");
        }

        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Slaptazodis turi buti bent 6 simboliu");
        }
    }
}