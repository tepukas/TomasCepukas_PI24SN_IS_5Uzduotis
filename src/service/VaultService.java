package TomasCepukas_PI24SN_IS_5Uzduotis.service;

import TomasCepukas_PI24SN_IS_5Uzduotis.model.AuthenticatedUser;
import TomasCepukas_PI24SN_IS_5Uzduotis.model.PasswordEntry;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class VaultService {

    private static final Path VAULTS_DIR = Path.of("data", "vaults");

    private final AuthenticatedUser user;
    private final CryptoService cryptoService = new CryptoService();
    private final CsvService csvService = new CsvService();

    public VaultService(AuthenticatedUser user) {
        this.user = user;
    }

    public List<PasswordEntry> loadEntries() throws Exception {
        Path vaultPath = getVaultPath();

        if (!Files.exists(vaultPath)) {
            Files.createDirectories(VAULTS_DIR);
            Files.writeString(vaultPath, csvService.toCsv(List.of()), StandardCharsets.UTF_8);
        }

        String csvText = Files.readString(vaultPath, StandardCharsets.UTF_8);

        return csvService.fromCsv(csvText);
    }

    public void saveEntries(List<PasswordEntry> entries) throws Exception {
        Files.createDirectories(VAULTS_DIR);

        String csvText = csvService.toCsv(entries);

        Files.writeString(getVaultPath(), csvText, StandardCharsets.UTF_8);
    }

    public String encryptPassword(String password) throws Exception {
        return cryptoService.encrypt(password, user.getVaultKey());
    }

    public String decryptPassword(String encryptedPassword) throws Exception {
        return cryptoService.decrypt(encryptedPassword, user.getVaultKey());
    }

    private Path getVaultPath() {
        return VAULTS_DIR.resolve(user.getUsername() + ".csv");
    }
}