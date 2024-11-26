package application.Controllers;
import application.*;

import application.DB.MySQL;
import application.Services.PersistenceHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import modelClasses.Budgett;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class BudgetController {

    // Persistence handler (assumes implementation elsewhere)
    private final PersistenceHandler persistenceHandler;

    // FXML injected components
    @FXML
    private TextField budgetCategory;

    @FXML
    private TextField budgetAmount;

    @FXML
    private DatePicker budgetDate;

    @FXML
    private TableView<Budgett> budgetTable;

    @FXML
    private TableColumn<Budgett, String> colCategory;

    @FXML
    private TableColumn<Budgett, Double> colAmount;

    @FXML
    private TableColumn<Budgett, LocalDate> colDate;

    @FXML
    private TableColumn<Budgett, String> colStatus;

    @FXML
    private TableColumn<Budgett, String> colActions;

    @FXML
    private ComboBox<String> sortByCategory;

    @FXML
    private Button saveBudgetBtn;

    @FXML
    private Button lockBudgetBtn;

    @FXML
    private Button sortBudgetBtn;

    @FXML
    private Button shareBudgetBtn;

    @FXML
    private Button deleteBudgetBtn;


    // Observable list to store budget entries
    private final ObservableList<Budgett> budgetList = FXCollections.observableArrayList();

    // Constructor
    public BudgetController() {
        this.persistenceHandler = new MySQL();
    }

    @FXML
    public void initialize() {
        // Initialize ComboBox with categories
        sortByCategory.setItems(FXCollections.observableArrayList("Food", "Utilities", "Transport", "Savings", "Other"));

        // Bind table columns to Budgett properties
        colCategory.setCellValueFactory(data -> data.getValue().categoryProperty());
        colAmount.setCellValueFactory(data -> data.getValue().amountProperty().asObject());
        colDate.setCellValueFactory(data -> data.getValue().dateProperty());
        colStatus.setCellValueFactory(data -> data.getValue().statusProperty());
        colActions.setCellValueFactory(data -> data.getValue().actionsProperty());

        // Set table data
        budgetTable.setItems(budgetList);

        // Load existing budgets from database
        loadBudgetsFromDatabase();
    }

    // Load existing budgets from the database
    private void loadBudgetsFromDatabase() {
        try {
            // Fetch the budget entries from the database
            ResultSet resultSet = persistenceHandler.fetchAllBudgets();

            // Clear the existing list and add the fetched budgets
            budgetList.clear();

            while (resultSet.next()) {
                String category = resultSet.getString("category");
                double amount = resultSet.getDouble("amount");
                java.sql.Date date = resultSet.getDate("date");
                String status = resultSet.getString("status");
                String actions = resultSet.getString("actions");

                // Create a new Budgett object and add it to the list
                Budgett budget = new Budgett(category, amount, date.toLocalDate(), status, actions);
                budgetList.add(budget);
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Failed to load budgets from the database.");
        }
    }

    // Method to sort budgets by selected category
    @FXML
    void sortBudget(ActionEvent event) {
        String selectedCategory = sortByCategory.getValue();

        if (selectedCategory != null) {
            // Filter the list by selected category (without affecting other entries)
            ObservableList<Budgett> sortedList = FXCollections.observableArrayList(
                    budgetList.stream()
                            .filter(budget -> budget.getCategory().equals(selectedCategory))
                            .toList()
            );
            budgetTable.setItems(sortedList);
            showAlert(Alert.AlertType.INFORMATION, "Budgets sorted by category: " + selectedCategory);
        } else {
            showAlert(Alert.AlertType.WARNING, "Please select a category to sort.");
        }
    }


    // Method to navigate back to the dashboard
    @FXML
    void goBackToDashboard(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/application/FXML/Dashboard.fxml"));
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Failed to load the dashboard.");
        }
    }

    // Method to save a budget entry
    @FXML
    void saveBudget(ActionEvent event) {
        try {
            String category = budgetCategory.getText();
            String amountText = budgetAmount.getText();
            LocalDate date = budgetDate.getValue();

            // Validate input fields
            if (category.isEmpty() || amountText.isEmpty() || date == null) {
                showAlert(Alert.AlertType.WARNING, "Please fill all the fields.");
                return;
            }

            double amount = Double.parseDouble(amountText);

            // Convert LocalDate to java.sql.Date
            java.sql.Date sqlDate = java.sql.Date.valueOf(date);

            // Create new Budgett object
            Budgett budget = new Budgett(category, amount, date, "Unlocked", "Actions Placeholder");

            // Save budget to database
            boolean isSaved = persistenceHandler.saveBudget(category, amount, sqlDate);  // Pass sqlDate instead of LocalDate
            if (isSaved) {
                // Add to the observable list to update the UI
                budgetList.add(budget);

                // Show success message and clear inputs
                showAlert(Alert.AlertType.INFORMATION, "Budget saved successfully.");
                clearInputs();
            } else {
                showAlert(Alert.AlertType.ERROR, "Failed to save the budget to the database.");
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid amount. Please enter a valid number.");
        }
    }

    @FXML
    void deleteBudget(ActionEvent event) {
        Budgett selectedBudget = budgetTable.getSelectionModel().getSelectedItem();

        if (selectedBudget != null) {
            // Confirm deletion with the user
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Delete Confirmation");
            confirmAlert.setHeaderText(null);
            confirmAlert.setContentText("Are you sure you want to delete the selected budget?");
            confirmAlert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    // Remove from database
                    boolean isDeleted = persistenceHandler.deleteBudget(selectedBudget.getCategory(), selectedBudget.getDate());

                    if (isDeleted) {
                        // Remove from the observable list to update the table
                        budgetList.remove(selectedBudget);
                        showAlert(Alert.AlertType.INFORMATION, "Budget deleted successfully.");
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Failed to delete the budget from the database.");
                    }
                }
            });
        } else {
            showAlert(Alert.AlertType.WARNING, "Please select a budget to delete.");
        }
    }


    // Method to lock a budget entry
    @FXML
    void lockBudget(ActionEvent event) {
        Budgett selectedBudget = budgetTable.getSelectionModel().getSelectedItem();

        if (selectedBudget != null) {
            selectedBudget.setStatus("Locked");
            budgetTable.refresh(); // Refresh table to show the updated status
            showAlert(Alert.AlertType.INFORMATION, "Budget locked successfully.");
        } else {
            showAlert(Alert.AlertType.WARNING, "Please select a budget to lock.");
        }
    }


    // Method to share budget information (placeholder implementation)
    @FXML
    void shareBudget(ActionEvent event) {
        showAlert(Alert.AlertType.INFORMATION, "Share functionality not implemented yet.");
    }

    // Helper method to clear input fields
    private void clearInputs() {
        budgetCategory.clear();
        budgetAmount.clear();
        budgetDate.setValue(null);
    }

    // Helper method to display alerts
    private void showAlert(Alert.AlertType alertType, String message) {
        Alert alert = new Alert(alertType);
        alert.setContentText(message);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}