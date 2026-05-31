package TomasCepukas_PI24SN_IS_5Uzduotis;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import TomasCepukas_PI24SN_IS_5Uzduotis.model.AuthenticatedUser;
import TomasCepukas_PI24SN_IS_5Uzduotis.service.UserService;
import TomasCepukas_PI24SN_IS_5Uzduotis.util.AlertUtil;

public class Main extends Application {

    private final UserService userService = new UserService();

    private Stage stage;
    private AuthenticatedUser currentUser;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        this.stage.setTitle("Slaptazodziu tvarkykle");
        showLoginScene();
    }

    private void showLoginScene() {
        Label title = new Label("Registracija ir prisijungimas");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Vartotojo vardas");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Slaptazodis");

        Button registerButton = new Button("Registruotis");
        Button loginButton = new Button("Prisijungti");

        registerButton.setMaxWidth(Double.MAX_VALUE);
        loginButton.setMaxWidth(Double.MAX_VALUE);

        registerButton.setOnAction(event -> {
            try {
                String username = usernameField.getText().trim();
                String password = passwordField.getText();

                userService.register(username, password);

                AlertUtil.showInfo("Sekminga", "Vartotojas uzregistruotas");
            } catch (Exception e) {
                AlertUtil.showError("Registracija nepavyko", e.getMessage());
            }
        });

        loginButton.setOnAction(event -> {
            try {
                String username = usernameField.getText().trim();
                String password = passwordField.getText();

                currentUser = userService.login(username, password);

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
                passwordField,
                registerButton,
                loginButton
        );

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    private void showMainScene() {
        Label title = new Label("Sekmingai prisijungta");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        Button logoutButton = new Button("Atsijungti");

        logoutButton.setOnAction(event -> {
            currentUser = null;
            showLoginScene();
        });

        VBox root = new VBox(12);
        root.setPadding(new Insets(25));
        root.setPrefWidth(500);

        root.getChildren().addAll(title, logoutButton);

        Scene scene = new Scene(root);
        stage.setScene(scene);
    }

    public static void main(String[] args) {
        launch(args);
    }
}