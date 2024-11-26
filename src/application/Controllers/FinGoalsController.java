package application.Controllers;
import application.*;

import application.DB.MySQL;
import application.Services.PersistenceHandler;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import modelClasses.Goal;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import application.Services.ReminderScheduler;

public class FinGoalsController {

    private Stage stage;
    private Scene scene;
    private Alert alert;
    private PersistenceHandler persistenceHandler;

    @FXML
    private CheckBox gdaily;
    @FXML
    private Button delgg;
    @FXML
    private TextField gid;
    @FXML
    private DatePicker gdeadline;
    @FXML
    private CheckBox gmonth;
    @FXML
    private TextField gname;
    @FXML
    private Button goback;
    @FXML
    private TextField gtarget;
    @FXML
    private ComboBox<String> gtype;
    @FXML
    private CheckBox gweek;
    @FXML
    private Button savegg;

    @FXML
    private TableView<Goal> goalTable;
    @FXML
    private TableColumn<Goal, String> goalNameColumn;
    @FXML
    private TableColumn<Goal, String> goalTypeColumn;
    @FXML
    private TableColumn<Goal, Double> targetAmountColumn;
    @FXML
    private TableColumn<Goal, String> deadlineColumn;
    @FXML
    private TableColumn<Goal, String> remindersColumn;

    public FinGoalsController() {
        this.persistenceHandler = this.persistenceHandler = new MySQL();
    }

    @FXML
    void initialize() {
        // Initialize ComboBox with goal types
        gtype.setItems(FXCollections.observableArrayList("Savings", "Investment", "Debt Payment", "Emergency Fund", "Travel", "Life"));

        // Set up TableView columns
        goalNameColumn.setCellValueFactory(cellData -> cellData.getValue().goalNameProperty());
        goalTypeColumn.setCellValueFactory(cellData -> cellData.getValue().goalTypeProperty());
        targetAmountColumn.setCellValueFactory(cellData -> cellData.getValue().targetAmountProperty().asObject());
        deadlineColumn.setCellValueFactory(cellData -> cellData.getValue().deadlineProperty());
        remindersColumn.setCellValueFactory(cellData -> cellData.getValue().remindersProperty());

        // Restrict DatePicker to disallow past dates
        gdeadline.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                // Disable dates before today
                if (date.isBefore(LocalDate.now())) {
                    setDisable(true);
                    setStyle("-fx-background-color: #d3d3d3;"); // Optional: Style disabled dates
                }
            }
        });

        loadGoalData();

        // Start the reminder scheduler
        ReminderScheduler scheduler = new ReminderScheduler();
        scheduler.startReminderTask();
    }


    @FXML
    void goBackToDashboard(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/application/FXML/Dashboard.fxml"));
            stage = (Stage) goback.getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void saveGoal(ActionEvent event) {
        System.out.println("Save button clicked");

        String goalName = gname.getText();
        String goalType = gtype.getValue();
        String targetAmountStr = gtarget.getText();
        String deadline = gdeadline.getValue() != null ? gdeadline.getValue().toString() : "";

        StringBuilder reminders = new StringBuilder();
        if (gdaily.isSelected()) reminders.append("daily,");
        if (gweek.isSelected()) reminders.append("weekly,");
        if (gmonth.isSelected()) reminders.append("monthly,");

        if (reminders.length() > 0) {
            reminders.setLength(reminders.length() - 1);  // Remove trailing comma
        }

        // Input validation
        if (goalName.isEmpty() || goalType == null || targetAmountStr.isEmpty() || deadline.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Please fill out all fields.");
            return;
        }

        // Check if at least one reminder is selected
        if (reminders.length() == 0) {
            showAlert(Alert.AlertType.ERROR, "Please select at least one reminder option.");
            return;
        }

        double targetAmount;
        try {
            targetAmount = Double.parseDouble(targetAmountStr);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Please enter a valid target amount.");
            return;
        }

        boolean isSaved = persistenceHandler.saveGoal(goalName, goalType, targetAmount, deadline, reminders.toString());

        if (isSaved) {
            showAlert(Alert.AlertType.INFORMATION, "Goal saved successfully.");
            clearFields();
            loadGoalData();
        } else {
            showAlert(Alert.AlertType.ERROR, "Failed to save the goal.");
        }
    }

    
    @FXML
    void deleteGoal(ActionEvent event) {
        System.out.println("Delete button clicked");
        // Get the goal name from the user
        String goalNameToDelete = gid.getText().trim();  // Corrected: use 'gid' instead of 'gname'

        if (goalNameToDelete.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Please enter the goal name to delete.");
            return;
        }

        // Show confirmation dialog
        Alert confirmDelete = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDelete.setTitle("Confirm Deletion");
        confirmDelete.setHeaderText("Are you sure you want to delete the goal: " + goalNameToDelete + "?");
        confirmDelete.setContentText("This action cannot be undone.");
        confirmDelete.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Perform the deletion if confirmed
                boolean isDeleted = persistenceHandler.deleteGoal(goalNameToDelete);
                if (isDeleted) {
                    showAlert(Alert.AlertType.INFORMATION, "Goal deleted successfully.");
                    loadGoalData();
                    gid.clear(); // Clear the name field after successful deletion
                } else {
                    showAlert(Alert.AlertType.ERROR, "Failed to delete the goal.");
                }
            }
        });
    }




    private void showAlert(Alert.AlertType alertType, String message) {
        alert = new Alert(alertType);
        alert.setTitle(alertType == Alert.AlertType.ERROR ? "Error" : "Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearFields() {
        gname.clear();
        gtype.setValue(null);
        gtarget.clear();
        gdeadline.setValue(null);
        gdaily.setSelected(false);
        gweek.setSelected(false);
        gmonth.setSelected(false);
    }

    @FXML
    private void loadGoalData() {
        String query = "SELECT goal_name, goal_type, target_amount, deadline, reminders FROM Goals";
        try {
            ResultSet resultSet = persistenceHandler.executeQuery(query);

            goalTable.getItems().clear();  // Clear existing data in the table
            while (resultSet.next()) {
                String goalName = resultSet.getString("goal_name");
                String goalType = resultSet.getString("goal_type");
                double targetAmount = resultSet.getDouble("target_amount");
                String deadline = resultSet.getString("deadline");
                String reminders = resultSet.getString("reminders");

                Goal goal = new Goal(goalName, goalType, targetAmount, deadline, reminders);
                goalTable.getItems().add(goal);  // Add goal to TableView
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error loading goal data.");
        }
    }


}
