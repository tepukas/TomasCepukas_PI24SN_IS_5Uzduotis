package TomasCepukas_PI24SN_IS_5Uzduotis.model;

public class PasswordEntry {

    private String title;
    private String encryptedPassword;
    private String url;
    private String notes;

    public PasswordEntry(String title, String encryptedPassword, String url, String notes) {
        this.title = title;
        this.encryptedPassword = encryptedPassword;
        this.url = url;
        this.notes = notes;
    }

    public String getTitle() {
        return title;
    }

    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    public String getUrl() {
        return url;
    }

    public String getNotes() {
        return notes;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setEncryptedPassword(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}