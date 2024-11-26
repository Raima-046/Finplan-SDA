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
import modelClasses.Transactionns;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class TransactionController {
    
    private Stage stage;
    private Scene scene;
    private Alert alert;
    
    @FXML
    private Button goback;

    @FXML
    private ComboBox<String> accountdd;

    @FXML
    private TextField amt;

    @FXML
    private TextField cat;

    @FXML
    private Button clear;

    @FXML
    private DatePicker date;

    @FXML
    private RadioButton exprb;

    @FXML
    private TextField famax;

    @FXML
    private TextField famin;

    @FXML
    private TextField fcat;

    @FXML
    private DatePicker fedate;

    @FXML
    private Button filter;

    @FXML
    private DatePicker fsdate;

    @FXML
    private RadioButton incrb;

    @FXML
    private Button savett;

    @FXML
    private Button deleteTransaction;

    @FXML
    private TableView<Transactionns> transacTable;

    @FXML
    private TableColumn<Transactionns, String> categoryColumn;

    @FXML
    private TableColumn<Transactionns, String> dateColumn;

    @FXML
    private TableColumn<Transactionns, String> accountColumn;

    @FXML
    private TableColumn<Transactionns, Double> amountColumn;

    @FXML
    private TableColumn<Transactionns, String> transactionTypeColumn;

    @FXML
    private Label userText;

    private ObservableList<Transactionns> transactionList = FXCollections.observableArrayList();
    private PersistenceHandler persistenceHandler;

    public TransactionController() {
        this.persistenceHandler = new MySQL();
    }

    @FXML
    public void saveTransaction(ActionEvent event) {
        String category = cat.getText();
        String transactionDate = date.getValue() != null ? date.getValue().toString() : "";
        String account = accountdd.getValue();
        String amountStr = amt.getText();

        String transactionTypeStr = null;
        if (incrb.isSelected()) {
            transactionTypeStr = "Incoming";
        } else if (exprb.isSelected()) {
            transactionTypeStr = "Outgoing";
        }

        // Validation for empty fields
        if (category.isEmpty() || transactionDate.isEmpty() || account == null || amountStr.isEmpty() || transactionTypeStr == null) {
            showAlert(Alert.AlertType.ERROR, "All fields must be filled out.");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
            // Validation for negative amount
            if (amount < 0) {
                showAlert(Alert.AlertType.ERROR, "Amount cannot be negative.");
                amt.clear(); // Clear the amount field
                return;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Please enter a valid amount.");
            amt.clear(); // Clear the amount field
            return;
        }

        // Save transaction if validations pass
        boolean isSaved = persistenceHandler.saveTransaction(category, transactionDate, account, amount, transactionTypeStr);

        if (isSaved) {
            showAlert(Alert.AlertType.INFORMATION, "Transaction saved successfully.");
            clearFields(); 
            loadTransactionData();  
        } else {
            showAlert(Alert.AlertType.ERROR, "Failed to save the transaction.");
        }
    }


    @FXML
    public void deleteSelectedTransaction(ActionEvent event) {
        // Get the selected transaction from the table
        Transactionns selectedTransaction = transacTable.getSelectionModel().getSelectedItem();

        if (selectedTransaction == null) {
            showAlert(Alert.AlertType.ERROR, "Please select a transaction to delete.");
            return;
        }

        // Confirmation alert
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Delete Confirmation");
        confirmationAlert.setHeaderText(null);
        confirmationAlert.setContentText("Are you sure you want to delete the selected transaction?");
        if (confirmationAlert.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }

        // Perform the delete action
        String category = selectedTransaction.getCategory();
        String transactionDate = selectedTransaction.getTransactionDate().toString();
        String account = selectedTransaction.getAccount();
        double amount = selectedTransaction.getAmount();
        String transactionType = selectedTransaction.getTransactionType();

        boolean isDeleted = persistenceHandler.deleteTransaction(category, transactionDate, account, amount, transactionType);

        if (isDeleted) {
            showAlert(Alert.AlertType.INFORMATION, "Transaction deleted successfully.");
            loadTransactionData(); // Reload the data after deletion
        } else {
            showAlert(Alert.AlertType.ERROR, "Failed to delete the transaction.");
        }
    }

    private void showAlert(Alert.AlertType alertType, String message) {
        alert = new Alert(alertType);
        alert.setTitle(alertType == Alert.AlertType.ERROR ? "Error" : "Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    
    private void clearFields() {
        cat.clear();             
        amt.clear();             
        accountdd.setValue(null); 
        date.setValue(null);    
        incrb.setSelected(false); 
        exprb.setSelected(false); 
    }

    
    @FXML
    private void initialize() {
        ObservableList<String> accountTypes = FXCollections.observableArrayList("Savings", "Current", "Other");
        accountdd.setItems(accountTypes);

        categoryColumn.setCellValueFactory(cellData -> cellData.getValue().categoryProperty());
        dateColumn.setCellValueFactory(cellData -> cellData.getValue().transactionDateProperty().asString());
        accountColumn.setCellValueFactory(cellData -> cellData.getValue().accountProperty());
        amountColumn.setCellValueFactory(cellData -> cellData.getValue().amountProperty().asObject());
        transactionTypeColumn.setCellValueFactory(cellData -> cellData.getValue().transactionTypeProperty());

        loadTransactionData();
    }

    private void loadTransactionData() {
        String query = "SELECT category, transaction_date, account, amount, transaction_type FROM Transactions";
        try {
            ResultSet resultSet = persistenceHandler.executeQuery(query);
            transactionList.clear(); // Clear existing data in the list

            while (resultSet.next()) {
                String category = resultSet.getString("category");
                String transactionDateStr = resultSet.getString("transaction_date");
                LocalDate transactionDate = LocalDate.parse(transactionDateStr); // Convert string to LocalDate
                String account = resultSet.getString("account");
                double amount = resultSet.getDouble("amount");
                String transactionType = resultSet.getString("transaction_type");

                Transactionns transaction = new Transactionns(category, transactionDate, account, amount, transactionType);
                transactionList.add(transaction);
            }

            transacTable.setItems(transactionList);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error loading transaction data.");
        }
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
    private void applyFilter(ActionEvent event) {
       
        String minAmount = famin.getText();
        String maxAmount = famax.getText();
        LocalDate startDate = fsdate.getValue();
        LocalDate endDate = fedate.getValue();

        ObservableList<Transactionns> filteredList = FXCollections.observableArrayList();

        for (Transactionns transaction : transactionList) {
            boolean matches = true;
            
            if (!minAmount.isEmpty()) {
                try {
                    double min = Double.parseDouble(minAmount);
                    if (transaction.getAmount() < min) {
                        matches = false;
                    }
                } catch (NumberFormatException e) {
                    showAlert(Alert.AlertType.ERROR, "Invalid minimum amount value.");
                    return;
                }
            }
            if (!maxAmount.isEmpty()) {
                try {
                    double max = Double.parseDouble(maxAmount);
                    if (transaction.getAmount() > max) {
                        matches = false;
                    }
                } catch (NumberFormatException e) {
                    showAlert(Alert.AlertType.ERROR, "Invalid maximum amount value.");
                    return;
                }
            }
            if (startDate != null && transaction.getTransactionDate() != null && transaction.getTransactionDate().isBefore(startDate)) {
                matches = false;
            }
            if (endDate != null && transaction.getTransactionDate() != null && transaction.getTransactionDate().isAfter(endDate)) {
                matches = false;
            }

            if (matches) {
                filteredList.add(transaction);
            }
        }

        transacTable.setItems(filteredList);
    }

    
    @FXML
    public void clearFilters(ActionEvent event) {
        loadTransactionData();
    }
    
}