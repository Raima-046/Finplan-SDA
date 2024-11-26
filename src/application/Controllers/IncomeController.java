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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import modelClasses.inccome;

public class IncomeController {

    @FXML
    private TextField Incname;

    @FXML
    private ComboBox<String> incAcc;

    @FXML
    private TextField incAmt;

    @FXML
    private TextField incCat;

    @FXML
    private DatePicker incDate;

    @FXML
    private TextField incDesc;

    @FXML
    private ComboBox<String> incSort;

    @FXML
    private Button saveInc;

    @FXML
    private Button deleteInc;

    @FXML
    private Button incClear;

    @FXML
    private Button goBack;

    @FXML
    private TableView<inccome> incTable;

    @FXML
    private TableColumn<inccome, String> colName;

    @FXML
    private TableColumn<inccome, String> colCategory;

    @FXML
    private TableColumn<inccome, BigDecimal> colAmount;

    @FXML
    private TableColumn<inccome, String> colAccount;

    @FXML
    private TableColumn<inccome, String> colDescription;

    @FXML
    private TableColumn<inccome, Date> colDate;

    private final PersistenceHandler persistenceHandler;
    private ObservableList<inccome> incomeList = FXCollections.observableArrayList();

    public IncomeController() {
        this.persistenceHandler = new MySQL();
    }

    @FXML
    public void initialize() {
        ObservableList<String> sortOptions = FXCollections.observableArrayList(
            "Amount: Low to High",
            "Amount: High to Low",
            "Date: Newest",
            "Date: Oldest"
        );
        incSort.setItems(sortOptions);

        ObservableList<String> accountOptions = FXCollections.observableArrayList(
            "Current",
            "Savings",
            "Others"
        );
        incAcc.setItems(accountOptions);

        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colAccount.setCellValueFactory(new PropertyValueFactory<>("account"));

        loadIncomeData();
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
            showAlert(Alert.AlertType.ERROR, "Failed to load the dashboard.");
        }
    }

    @FXML
    public void saveIncome(ActionEvent event) {
        String name = Incname.getText();
        String category = incCat.getText();
        String account = incAcc.getValue();
        String description = incDesc.getText();
        BigDecimal amount;
        Date date;

        if (name.isEmpty() || category.isEmpty() || account == null || incAmt.getText().isEmpty() || description.isEmpty() || incDate.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "All fields must be filled out.");
            return;
        }

        try {
            amount = new BigDecimal(incAmt.getText());
            if (amount.compareTo(BigDecimal.ZERO) < 0) { 
                showAlert(Alert.AlertType.ERROR, "Amount cannot be negative.");
                incAmt.clear();
                return;
            }
            date = Date.valueOf(incDate.getValue());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid numeric value for amount.");
            incAmt.clear();
            return;
        }

        boolean isSaved = persistenceHandler.saveIncome(name, category, date.toString(), account, amount.doubleValue(), description);

        if (isSaved) {
            showAlert(Alert.AlertType.INFORMATION, "Income saved successfully.");
            clearFields();
            loadIncomeData();
        } else {
            showAlert(Alert.AlertType.ERROR, "Failed to save the income.");
        }
    }

    @FXML
    public void deleteIncome(ActionEvent event) {
        // Get the selected income record from the table
        inccome selectedIncome = incTable.getSelectionModel().getSelectedItem();

        if (selectedIncome == null) {
            // Show an error alert if no record is selected
            showAlert(Alert.AlertType.ERROR, "Please select an entry to delete.");
            return;
        }

        // Confirmation alert
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Are you sure you want to delete the selected income record?");
        if (confirmAlert.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return; // Cancel deletion if the user does not confirm
        }

        // Call PersistenceHandler to delete the income record
        boolean isDeleted = persistenceHandler.deleteIncome(
                selectedIncome.getName(),
                selectedIncome.getCategory(),
                selectedIncome.getDate().toString(),  // Convert Date to String if necessary
                selectedIncome.getAccount(),
                selectedIncome.getAmount().doubleValue() // Convert BigDecimal to double if necessary
        );

        if (isDeleted) {
            // Show success alert
            showAlert(Alert.AlertType.INFORMATION, "Income entry deleted successfully.");
            loadIncomeData(); // Reload data into the table to reflect changes
        } else {
            // Show failure alert
            showAlert(Alert.AlertType.ERROR, "Failed to delete the income entry.");
        }
    }



    private void loadIncomeData() {
        String query = "SELECT name, category, amount, account, description, date FROM Income";
        try {
            ResultSet resultSet = persistenceHandler.executeQuery(query);
            incomeList.clear();

            while (resultSet.next()) {
                String name = resultSet.getString("name");
                String category = resultSet.getString("category");
                BigDecimal amount = resultSet.getBigDecimal("amount");
                String account = resultSet.getString("account");
                String description = resultSet.getString("description");
                Date date = resultSet.getDate("date");

                inccome income = new inccome(name, category, amount, account, description, date);
                incomeList.add(income);
            }

            incTable.setItems(incomeList);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error loading income data.");
        }
    }

    @FXML
    public void sortIncome(ActionEvent event) {
        String sortOption = incSort.getValue();
        String query = "SELECT name, category, amount, account, description, date FROM Income";

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

        loadSortedIncomeData(query);
    }

    private void loadSortedIncomeData(String query) {
        try {
            ResultSet resultSet = persistenceHandler.executeQuery(query);
            incomeList.clear();

            while (resultSet.next()) {
                String name = resultSet.getString("name");
                String category = resultSet.getString("category");
                BigDecimal amount = resultSet.getBigDecimal("amount");
                String account = resultSet.getString("account");
                String description = resultSet.getString("description");
                Date date = resultSet.getDate("date");

                inccome income = new inccome(name, category, amount, account, description, date);
                incomeList.add(income);
            }

            incTable.setItems(incomeList);
            incTable.refresh();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error loading sorted income data.");
        }
    }

    @FXML
    public void clearFilters(ActionEvent event) {
        loadIncomeData();
    }

    private void showAlert(Alert.AlertType alertType, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(alertType == Alert.AlertType.ERROR ? "Error" : "Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearFields() {
        Incname.clear();
        incCat.clear();
        incAmt.clear();
        incDesc.clear();
        incDate.setValue(null);
        incAcc.setValue(null);
        incSort.setValue(null);
    }
}
