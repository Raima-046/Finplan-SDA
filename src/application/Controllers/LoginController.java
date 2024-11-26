package application.Controllers;
import application.*;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import application.DB.MySQL;
import application.Services.PersistenceHandler;
import application.Services.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class LoginController {

    private Stage stage;
    private Scene scene;
    private Alert alert;

    // Use the PersistenceHandler
    private PersistenceHandler persistenceHandler;

    @FXML
    private Button loginBtn;
    
    @FXML
    private TextField pswd;

    @FXML
    private TextField userr;

    public LoginController() {
        // Initialize the PersistenceHandler
        this.persistenceHandler = new MySQL();
    }

    @FXML
    void createAcc(ActionEvent e) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/application/FXML/RegisterPage.fxml"));
        stage = (Stage) ((javafx.scene.Node) e.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void logBtn(ActionEvent event) throws IOException {
        System.out.println("Login button pressed!");

        if (userr.getText().trim().isEmpty() || pswd.getText().trim().isEmpty()) {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);
            alert.setContentText("Please fill all the blank fields.");
            alert.showAndWait();
        } else {
            String selectData = "SELECT username, budget_limit FROM users WHERE username = ? AND password = ?";

            try {
                ResultSet result = persistenceHandler.executeQuery(selectData, userr.getText().trim(), pswd.getText().trim());

                if (result.next()) {
                    // Save user details to UserSession
                    String username = result.getString("username");

                    UserSession.getInstance().setLoggedInUsername(username);

                    // Display login success alert
                    alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Information Message");
                    alert.setHeaderText(null);
                    alert.setContentText("You've been successfully logged in");
                    alert.showAndWait();

                    // Load the dashboard
                    Parent root = FXMLLoader.load(getClass().getResource("/application/FXML/Dashboard.fxml"));
                    stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
                    scene = new Scene(root);
                    stage.setScene(scene);
                    stage.show();
                } else {
                    alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Error Message");
                    alert.setHeaderText(null);
                    alert.setContentText("Incorrect username/password. Please enter a valid username/password.");
                    alert.showAndWait();
                }

            } catch (SQLException e) {
                e.printStackTrace();
                alert = new Alert(AlertType.ERROR);
                alert.setTitle("Database Error");
                alert.setHeaderText(null);
                alert.setContentText("An error occurred while accessing the database. Please try again.");
                alert.showAndWait();
            }
        }
    }
}
