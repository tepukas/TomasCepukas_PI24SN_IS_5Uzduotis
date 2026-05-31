package TomasCepukas_PI24SN_IS_5Uzduotis.model;

public class User {

    private final String username;
    private final String saltBase64;
    private final String passwordHashBase64;

    public User(String username, String saltBase64, String passwordHashBase64) {
        this.username = username;
        this.saltBase64 = saltBase64;
        this.passwordHashBase64 = passwordHashBase64;
    }

    public String getUsername() {
        return username;
    }

    public String getSaltBase64() {
        return saltBase64;
    }

    public String getPasswordHashBase64() {
        return passwordHashBase64;
    }

    public String toFileLine() {
        return username + ";" + saltBase64 + ";" + passwordHashBase64;
    }

    public static User fromFileLine(String line) {
        String[] parts = line.split(";", -1);

        if (parts.length != 3) {
            throw new IllegalArgumentException("Neteisingas vaertotojo irasas faile");
        }

        return new User(parts[0], parts[1], parts[2]);
    }
}