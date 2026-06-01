package TomasCepukas_PI24SN_IS_5Uzduotis;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import TomasCepukas_PI24SN_IS_5Uzduotis.model.AuthenticatedUser;
import TomasCepukas_PI24SN_IS_5Uzduotis.model.PasswordEntry;
import TomasCepukas_PI24SN_IS_5Uzduotis.service.PasswordGeneratorService;
import TomasCepukas_PI24SN_IS_5Uzduotis.service.UserService;
import TomasCepukas_PI24SN_IS_5Uzduotis.service.VaultService;
import TomasCepukas_PI24SN_IS_5Uzduotis.util.AlertUtil;

import java.util.List;

public class Main extends Application {

    private final UserService userService = new UserService();
    private final PasswordGeneratorService passwordGeneratorService = new PasswordGeneratorService();

    private Stage stage;
    private AuthenticatedUser currentUser;
    private VaultService vaultService;

    private final ObservableList<PasswordEntry> entries = FXCollections.observableArrayList();
    private final ObservableList<PasswordEntry> filteredEntries = FXCollections.observableArrayList();

    private TableView<PasswordEntry> table;

    private TextField titleField;
    private PasswordField passwordField;
    private TextField urlField;
    private TextArea notesArea;
    private TextField searchField;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        this.stage.setTitle("Slaptazodziu tvarkykle");
        showLoginScene();

        stage.setOnCloseRequest(event -> {
            saveVault();
            Platform.exit();
        });
    }

    private void showLoginScene() {
        Label title = new Label("Prisijungimas");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Vartotojo vardas");

        PasswordField loginPasswordField = new PasswordField();
        loginPasswordField.setPromptText("Slaptazodis");

        Button registerButton = new Button("Registruotis");
        Button loginButton = new Button("Prisijungti");

        registerButton.setMaxWidth(Double.MAX_VALUE);
        loginButton.setMaxWidth(Double.MAX_VALUE);

        registerButton.setOnAction(event -> {
            try {
                String username = usernameField.getText().trim();
                String password = loginPasswordField.getText();

                userService.register(username, password);

                AlertUtil.showInfo("Sekminga", "Vartotojas uzregistruotas");
            } catch (Exception e) {
                AlertUtil.showError("Registracija nepavyko", e.getMessage());
            }
        });

        loginButton.setOnAction(event -> {
            try {
                String username = usernameField.getText().trim();
                String password = loginPasswordField.getText();

                currentUser = userService.login(username, password);
                vaultService = new VaultService(currentUser);

                entries.setAll(vaultService.loadEntries());
                filteredEntries.setAll(entries);

                showMainScene();
            } catch (Exception e) {
                AlertUtil.showError("Prisijungimas nepavyko", e.getMessage());
            }
        });

        VBox root = new VBox(12);
        root.setPadding(new Insets(25));
        root.setPrefWidth(400);

        root.getChildren().addAll(
                title,
                usernameField,
                loginPasswordField,
                registerButton,
                loginButton
        );

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    private void showMainScene() {
        table = createTable();

        titleField = new TextField();
        titleField.setPromptText("Pavadinimas");

        passwordField = new PasswordField();
        passwordField.setPromptText("Slaptazodis");

        urlField = new TextField();
        urlField.setPromptText("URL arba programos pavadinimas");

        notesArea = new TextArea();
        notesArea.setPromptText("Pastabos");
        notesArea.setPrefRowCount(3);

        searchField = new TextField();
        searchField.setPromptText("Ieskoti pagal pavadinima");

        Button addButton = new Button("Prideti");
        Button updateButton = new Button("Atnaujinti");
        Button deleteButton = new Button("Istrinti");
        Button searchButton = new Button("Ieskoti");
        Button showAllButton = new Button("Rodyti visus");
        Button showPasswordButton = new Button("Rodyti slaptazodi");
        Button copyPasswordButton = new Button("Kopijuoti slaptazodi");
        Button generatePasswordButton = new Button("Generuoti slaptazodi");
        Button logoutButton = new Button("Atsijungti");

        addButton.setOnAction(event -> addEntry());
        updateButton.setOnAction(event -> updateEntry());
        deleteButton.setOnAction(event -> deleteEntry());
        searchButton.setOnAction(event -> searchEntries());

        showAllButton.setOnAction(event -> {
            searchField.clear();
            filteredEntries.setAll(entries);
        });

        showPasswordButton.setOnAction(event -> showSelectedPassword());
        copyPasswordButton.setOnAction(event -> copySelectedPassword());
        generatePasswordButton.setOnAction(event -> generatePassword());
        logoutButton.setOnAction(event -> logout());

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, selected) -> {
            if (selected != null) {
                titleField.setText(selected.getTitle());
                passwordField.clear();
                urlField.setText(selected.getUrl());
                notesArea.setText(selected.getNotes());
            }
        });

        HBox searchBox = new HBox(8);
        searchBox.getChildren().addAll(searchField, searchButton, showAllButton);

        HBox actionBox = new HBox(8);
        actionBox.getChildren().addAll(addButton, updateButton, deleteButton);

        HBox securityBox = new HBox(8);
        securityBox.getChildren().addAll(
                showPasswordButton,
                copyPasswordButton,
                generatePasswordButton,
                logoutButton
        );

        VBox form = new VBox(8);
        form.setPadding(new Insets(10));
        form.setPrefWidth(500);

        form.getChildren().addAll(
                new Label("Iraso duomenys"),
                titleField,
                passwordField,
                urlField,
                notesArea,
                actionBox,
                securityBox
        );

        VBox tableBox = new VBox(8);
        tableBox.setPadding(new Insets(10));
        HBox.setHgrow(tableBox, Priority.ALWAYS);

        tableBox.getChildren().addAll(
                new Label("Slaptazodziu irasai"),
                searchBox,
                table
        );

        HBox root = new HBox(10);
        root.setPadding(new Insets(10));
        root.getChildren().addAll(tableBox, form);

        Scene scene = new Scene(root, 1150, 600);
        stage.setScene(scene);
    }

    private TableView<PasswordEntry> createTable() {
        TableView<PasswordEntry> tableView = new TableView<>(filteredEntries);

        TableColumn<PasswordEntry, String> titleColumn = new TableColumn<>("Pavadinimas");
        titleColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitle()));

        TableColumn<PasswordEntry, String> passwordColumn = new TableColumn<>("Slaptazodis");
        passwordColumn.setCellValueFactory(data -> new SimpleStringProperty("********"));

        TableColumn<PasswordEntry, String> urlColumn = new TableColumn<>("URL / Programa");
        urlColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getUrl()));

        TableColumn<PasswordEntry, String> notesColumn = new TableColumn<>("Pastabos");
        notesColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNotes()));

        tableView.getColumns().add(titleColumn);
        tableView.getColumns().add(passwordColumn);
        tableView.getColumns().add(urlColumn);
        tableView.getColumns().add(notesColumn);

        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        return tableView;
    }

    private void addEntry() {
        try {
            if (titleField.getText().trim().isBlank()) {
                throw new IllegalArgumentException("Pavadinimas negali buti tuscias");
            }

            if (passwordField.getText().isBlank()) {
                throw new IllegalArgumentException("Slaptazodis negali buti tuscias");
            }

            if (findByTitle(titleField.getText().trim()) != null) {
                throw new IllegalArgumentException("Irasas tokiu pavadinimu jau egzistuoja");
            }

            String encryptedPassword = vaultService.encryptPassword(passwordField.getText());

            PasswordEntry entry = new PasswordEntry(
                    titleField.getText().trim(),
                    encryptedPassword,
                    urlField.getText().trim(),
                    notesArea.getText().trim()
            );

            entries.add(entry);
            filteredEntries.setAll(entries);

            saveVault();
            clearForm();

            AlertUtil.showInfo("Sekminga", "Irasas pridetas");
        } catch (Exception e) {
            AlertUtil.showError("Klaida", e.getMessage());
        }
    }

    private void updateEntry() {
        PasswordEntry selected = table.getSelectionModel().getSelectedItem();

        if (selected == null) {
            AlertUtil.showError("Klaida", "Pasirinkite irasa kuri norite atnaujinti");
            return;
        }

        try {
            if (titleField.getText().trim().isBlank()) {
                throw new IllegalArgumentException("Pavadinimas negali buti tuscias");
            }

            selected.setTitle(titleField.getText().trim());
            selected.setUrl(urlField.getText().trim());
            selected.setNotes(notesArea.getText().trim());

            if (!passwordField.getText().isBlank()) {
                selected.setEncryptedPassword(vaultService.encryptPassword(passwordField.getText()));
            }

            filteredEntries.setAll(entries);

            saveVault();
            clearForm();

            AlertUtil.showInfo("Sekminga", "Irasas atnaujintas");
        } catch (Exception e) {
            AlertUtil.showError("Klaida", e.getMessage());
        }
    }

    private void deleteEntry() {
        PasswordEntry selected = table.getSelectionModel().getSelectedItem();

        if (selected == null) {
            AlertUtil.showError("Klaida", "Pasirinkite irasa kuri norite istrinti");
            return;
        }

        entries.remove(selected);
        filteredEntries.setAll(entries);

        saveVault();
        clearForm();

        AlertUtil.showInfo("Sekminga", "Irasas istrintas");
    }

    private void searchEntries() {
        String searchText = searchField.getText().trim().toLowerCase();

        if (searchText.isBlank()) {
            filteredEntries.setAll(entries);
            return;
        }

        List<PasswordEntry> result = entries.stream()
                .filter(entry -> entry.getTitle().toLowerCase().contains(searchText))
                .toList();

        filteredEntries.setAll(result);
    }

    private void showSelectedPassword() {
        PasswordEntry selected = table.getSelectionModel().getSelectedItem();

        if (selected == null) {
            AlertUtil.showError("Klaida", "Pasirinkite irasa");
            return;
        }

        try {
            String password = vaultService.decryptPassword(selected.getEncryptedPassword());

            AlertUtil.showInfo("Slaptazodis", "Slaptazodis: " + password);
        } catch (Exception e) {
            AlertUtil.showError("Klaida", e.getMessage());
        }
    }

    private void copySelectedPassword() {
        PasswordEntry selected = table.getSelectionModel().getSelectedItem();

        if (selected == null) {
            AlertUtil.showError("Klaida", "Pasirinkite irasa");
            return;
        }

        try {
            String password = vaultService.decryptPassword(selected.getEncryptedPassword());

            ClipboardContent content = new ClipboardContent();
            content.putString(password);

            Clipboard clipboard = Clipboard.getSystemClipboard();
            clipboard.setContent(content);

            AlertUtil.showInfo("Nukopijuota", "Slaptazodis nukopijuotas i iskarpine");
        } catch (Exception e) {
            AlertUtil.showError("Klaida", e.getMessage());
        }
    }

    private void generatePassword() {
        String generatedPassword = passwordGeneratorService.generatePassword(16);
        passwordField.setText(generatedPassword);
    }

    private PasswordEntry findByTitle(String title) {
        return entries.stream()
                .filter(entry -> entry.getTitle().equalsIgnoreCase(title))
                .findFirst()
                .orElse(null);
    }

    private void saveVault() {
        if (vaultService != null) {
            try {
                vaultService.saveEntries(entries);
            } catch (Exception ignored) {
            }
        }
    }

    private void logout() {
        saveVault();

        currentUser = null;
        vaultService = null;

        entries.clear();
        filteredEntries.clear();

        showLoginScene();
    }

    private void clearForm() {
        titleField.clear();
        passwordField.clear();
        urlField.clear();
        notesArea.clear();

        table.getSelectionModel().clearSelection();
    }

    public static void main(String[] args) {
        launch(args);
    }
}