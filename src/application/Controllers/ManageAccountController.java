package application.Controllers;
import application.*;

import application.DB.MySQL;
import application.Services.PersistenceHandler;
import application.Services.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ManageAccountController {

    @FXML
    private Label userText;

    @FXML
    private TextField crrPass;  // Current password field

    @FXML
    private Button goback;

    @FXML
    private TextField newPass;  // New password field

    @FXML
    private TextField newUsr;  // New username field

    @FXML
    private TextField oldUsr;  // Old username field

    @FXML
    private Button savePass;  // Save password button

    @FXML
    private Button saveUsr;  // Save username button
    
    @FXML
    private TextField oldUsr1;

    private PersistenceHandler persistenceHandler; // Declare the PersistenceHandler object

    public ManageAccountController() {
        // Initialize the PersistenceHandler
        this.persistenceHandler = new MySQL();
    }

    @FXML
    private void initialize() {
        String username = UserSession.getInstance().getLoggedInUsername();
        
        if (userText != null) {
            userText.setText("Welcome, " + (username != null ? username : "Guest"));
        }
    }

    @FXML
    void goBackToDashboard(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/application/FXML/Dashboard.fxml"));
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void savePassword(ActionEvent event) {
        String oldUsername = oldUsr1.getText();
        String currentPassword = crrPass.getText();
        String newPassword = newPass.getText();

        // Check if any field is empty
        if (oldUsername.isEmpty() || currentPassword.isEmpty() || newPassword.isEmpty()) {
            showAlert(AlertType.WARNING, "All fields are required.");
            return;
        }

        // Validate the old password
        if (persistenceHandler.validateOldPassword(oldUsername, currentPassword)) {
            // Update password if validation is successful
            boolean updateSuccess = persistenceHandler.updatePassword(oldUsername, newPassword);
            if (updateSuccess) {
                showAlert(AlertType.INFORMATION, "Password updated successfully.");
            } else {
                showAlert(AlertType.ERROR, "Failed to update password. Please try again.");
            }
        } else {
            showAlert(AlertType.ERROR, "Incorrect old password.");
        }
    }



    @FXML
    void saveUsername(ActionEvent event) {
        String oldUsername = oldUsr.getText();
        String newUsername = newUsr.getText();

        // Check if fields are empty
        if (oldUsername.isEmpty() || newUsername.isEmpty()) {
            showAlert(AlertType.WARNING, "Both old and new usernames are required.");
            return;
        }

        // Update the username using the PersistenceHandler
        boolean updateSuccess = persistenceHandler.updateUsername(oldUsername, newUsername);
        if (updateSuccess) {
            // Show success alert
            showAlert(AlertType.INFORMATION, "Username updated successfully.");
        } else {
            // Show failure alert
            showAlert(AlertType.ERROR, "Failed to update username. Please check if the old username is correct.");
        }
    }
    
    private void showAlert(Alert.AlertType alertType, String message) {
        Alert alert = new Alert(alertType); // Corrected: Instantiate the Alert
        alert.setTitle(alertType == Alert.AlertType.ERROR ? "Error" : "Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
