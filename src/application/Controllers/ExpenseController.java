package application.Controllers;
import application.*;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

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
import modelClasses.exxpense;
import javafx.scene.control.cell.PropertyValueFactory;

public class ExpenseController {

    private Stage stage;
    private Scene scene;

    @FXML
    private ComboBox<String> accountdd1;

    @FXML
    private TextField cat;

    @FXML
    private Button clearExp;

    @FXML
    private Button sortExp;

    @FXML
    private TextField ecpCat;

    @FXML
    private ComboBox<String> expAcc;

    @FXML
    private TextField expAmt;

    @FXML
    private DatePicker expDate;

    @FXML
    private TextField expDesc;

    @FXML
    private TextField expname;

    @FXML
    private Button goback;

    @FXML
    private Button saveExp;

    @FXML
    private TableView<exxpense> expTable;

    @FXML
    private TableColumn<exxpense, Integer> colExpenseId;

    @FXML
    private TableColumn<exxpense, String> colName;

    @FXML
    private TableColumn<exxpense, String> colCategory;

    @FXML
    private TableColumn<exxpense, String> colAccount;

    @FXML
    private TableColumn<exxpense, BigDecimal> colAmount;

    @FXML
    private TableColumn<exxpense, String> colDescription;

    @FXML
    private TableColumn<exxpense, Date> colDate;

    private PersistenceHandler persistenceHandler;

    private ObservableList<exxpense> expenseList = FXCollections.observableArrayList();

    public ExpenseController() {
        this.persistenceHandler = new MySQL();
    }

    @FXML
    void goBackToDashboard(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/application/FXML/Dashboard.fxml"));
            stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        ObservableList<String> sortOptions = FXCollections.observableArrayList(
            "Amount: Low to High",
            "Amount: High to Low",
            "Date: Newest",
            "Date: Oldest"
        );

        ObservableList<String> accountOptions = FXCollections.observableArrayList(
            "Current",
            "Savings",
            "Others"
        );
        accountdd1.setItems(sortOptions);
        expAcc.setItems(accountOptions);

        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colAccount.setCellValueFactory(new PropertyValueFactory<>("account"));

        loadExpenseData();
    }

    @FXML
    public void saveExpense(ActionEvent event) {
        String name = expname.getText();
        String category = ecpCat.getText();
        String expenseDate = expDate.getValue() != null ? expDate.getValue().toString() : "";
        String account = expAcc.getValue();
        String amountStr = expAmt.getText();
        String description = expDesc.getText();

        // Check for empty fields
        if (name.isEmpty() || category.isEmpty() || expenseDate.isEmpty() || account == null || amountStr.isEmpty() || description.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "All fields must be filled out.");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
            if (amount < 0) { // Validation for negative values
                showAlert(Alert.AlertType.ERROR, "Amount cannot be negative.");
                expAmt.clear();
                return;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Please enter a valid amount.");
            expAmt.clear();
            return;
        }

        // Attempt to save the expense
        boolean isSaved = persistenceHandler.saveExpense(name, category, expenseDate, account, amount, description);

        if (isSaved) {
            showAlert(Alert.AlertType.INFORMATION, "Expense saved successfully.");
            clearFields();
            loadExpenseData();
        } else {
            showAlert(Alert.AlertType.ERROR, "Failed to save the expense.");
        }
    }

    private void loadExpenseData() {
        String query = "SELECT name, category, amount, account, description, date FROM Expense";
        try {
            ResultSet resultSet = persistenceHandler.executeQuery(query);
            expenseList.clear();

            while (resultSet.next()) {
                String name = resultSet.getString("name");
                String category = resultSet.getString("category");
                BigDecimal amount = resultSet.getBigDecimal("amount");
                String account = resultSet.getString("account");
                String description = resultSet.getString("description");
                Date date = resultSet.getDate("date");

                exxpense expense = new exxpense(name, category, amount, account, description, date);
                expenseList.add(expense);
            }

            expTable.setItems(expenseList);
            expTable.refresh();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error loading expense data.");
        }
    }

    @FXML
    public void deleteExpense(ActionEvent event) {
        // Get the selected expense record from the table
        exxpense selectedExpense = expTable.getSelectionModel().getSelectedItem();

        if (selectedExpense == null) {
            // Show an error alert if no record is selected
            showAlert(Alert.AlertType.ERROR, "Please select an entry to delete.");
            return;
        }

        // Confirmation alert
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Are you sure you want to delete the selected expense record?");
        if (confirmAlert.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return; // Cancel deletion if the user does not confirm
        }

        // Call PersistenceHandler to delete the expense record using unique fields
        boolean isDeleted = persistenceHandler.deleteExpense(
                selectedExpense.getName(),
                selectedExpense.getCategory(),
                selectedExpense.getDate().toString(),  // Convert Date to String if necessary
                selectedExpense.getAccount(),
                selectedExpense.getAmount().doubleValue() // Convert BigDecimal to double if necessary
        );

        if (isDeleted) {
            // Show success alert
            showAlert(Alert.AlertType.INFORMATION, "Expense entry deleted successfully.");
            loadExpenseData(); // Reload data into the table to reflect changes
        } else {
            // Show failure alert
            showAlert(Alert.AlertType.ERROR, "Failed to delete the expense entry.");
        }
    }




    private void clearFields() {
        expname.clear();
        ecpCat.clear();
        expAmt.clear();
        expDesc.clear();
        expDate.setValue(null);
        expAcc.setValue(null);
    }

    private void showAlert(Alert.AlertType alertType, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(alertType == Alert.AlertType.ERROR ? "Error" : "Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void sortExpense(ActionEvent event) {
        String sortOption = accountdd1.getValue();
        String query = "SELECT name, category, amount, account, description, date FROM Expense";

        if (sortOption != null) {
            switch (sortOption) {
                case "Amount: Low to High":
                    query += " ORDER BY amount ASC";
                    break;
                case "Amount: High to Low":
                    query += " ORDER BY amount DESC";
                    break;
                case "Date: Newest":
                    query += " ORDER BY date DESC";
                    break;
                case "Date: Oldest":
                    query += " ORDER BY date ASC";
                    break;
            }
        }

        loadSortedExpenseData(query);
    }

    private void loadSortedExpenseData(String query) {
        try {
            ResultSet resultSet = persistenceHandler.executeQuery(query);
            expenseList.clear();

            while (resultSet.next()) {
                String name = resultSet.getString("name");
                String category = resultSet.getString("category");
                BigDecimal amount = resultSet.getBigDecimal("amount");
                String account = resultSet.getString("account");
                String description = resultSet.getString("description");
                Date date = resultSet.getDate("date");

                exxpense expense = new exxpense(name, category, amount, account, description, date);
                expenseList.add(expense);
            }

            expTable.setItems(expenseList);
            expTable.refresh();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error loading sorted expense data.");
        }
    }

    @FXML
    public void clearFilters(ActionEvent event) {
        loadExpenseData();
    }
}
