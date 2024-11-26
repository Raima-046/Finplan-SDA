package application.Controllers;
import application.*;
import application.DB.MySQL;
import application.Services.PersistenceHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import java.io.IOException;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javafx.event.ActionEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegisterController {

    private Stage stage;
    private Scene scene;
    private Alert alert;

    // Use the PersistenceHandler class
    private PersistenceHandler persistenceHandler;

    @FXML
    private TextField firstN;

    @FXML
    private TextField lastN;

    @FXML
    private TextField userr;

    @FXML
    private TextField contt;

    @FXML
    private PasswordField passs;

    @FXML
    private Label repTLabel;

    @FXML
    private ComboBox<String> repType;
    
    @FXML
    private Button registerBtn;
    
    @FXML
    private Button loginBtn;

    public RegisterController() {
        // Initialize the PersistenceHandler
        this.persistenceHandler = new MySQL();
    }
    
    @FXML
    public void regBtn() {
        System.out.println("Register button pressed!");

        // Validate if all fields are filled
        if (userr.getText().isEmpty() || firstN.getText().isEmpty() || lastN.getText().isEmpty()
                || contt.getText().isEmpty() || passs.getText().isEmpty()) {
            showAlert(AlertType.ERROR, "Please fill all the blank fields");
        } else {
            System.out.println("All fields are filled.");

            String checkUserQuery = "SELECT username FROM Users WHERE username = ?";
            String insertUserQuery = "INSERT INTO Users (firstname, lastname, username, password, contact, addeddate) VALUES (?, ?, ?, ?, ?, ?)";

            try {
                // Check if the username already exists
                ResultSet result = persistenceHandler.executeQuery(checkUserQuery, userr.getText());

                if (result.next()) {
                    showAlert(AlertType.ERROR, "Username " + userr.getText() + " is already taken.");
                } else if (passs.getText().length() < 5) {
                    showAlert(AlertType.ERROR, "Password must have more than 5 characters!");
                } else {
                    // Insert the new user into the database
                    Date dd = new Date();
                    java.sql.Date sqlDate = new java.sql.Date(dd.getTime());

                    int rowsInserted = persistenceHandler.executeUpdate(insertUserQuery, 
                        firstN.getText(), 
                        lastN.getText(), 
                        userr.getText(), 
                        passs.getText(), 
                        contt.getText(), 
                        sqlDate);

                    if (rowsInserted > 0) {
                        showAlert(AlertType.INFORMATION, "You've been successfully registered");
                    }
                }

            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(AlertType.ERROR, "An error occurred: " + e.getMessage());
            }
        }
    }

    @FXML
    void goToLogin(ActionEvent e) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/application/FXML/LoginPage.fxml"));
        stage = (Stage) ((javafx.scene.Node) e.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    void goToReports(ActionEvent e) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/application/FXML/GenerateReport.fxml"));
        stage = (Stage) ((javafx.scene.Node) e.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    // Helper method to show alerts
    private void showAlert(AlertType alertType, String message) {
        alert = new Alert(alertType);
        alert.setTitle(alertType == AlertType.ERROR ? "Error Message" : "Information Message");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
