package application.Controllers;
import application.*;

import application.DB.MySQL;
import application.Services.PersistenceHandler;
import application.Services.UserSession;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import modelClasses.inccome;
import javafx.fxml.FXMLLoader;
import java.math.BigDecimal;
import java.sql.Date;

import modelClasses.exxpense;

public class DashboardController {
	private Stage stage;
    private Scene scene;
    
    // Instance of PersistenceHandler
    private PersistenceHandler persistenceHandler = new MySQL();

    @FXML
    private Label currExp;

    @FXML
    private Label currInc;
    
    @FXML
    private Label totalExp;

    @FXML
    private Label totalBal;


    @FXML
    private Button finGoalBtn;
    
    @FXML
    private Label userText;
    
    @FXML
    private ComboBox<String> userType;
    
    @FXML
    private Button transacBtn;  // Button to redirect to the transaction page
    
    @FXML
    private Button expBtn;
    
    @FXML
    private Button budgetBtn;
    
    @FXML
    private Button logoutBtn;

    @FXML
    private Button mgmtAccBtn;
    

    @FXML
    private Button incBtn;
    
    @FXML
    private Button reportBtn;
    
    @FXML
    private TableView<exxpense> Lexp;

    @FXML
    private TableView<inccome> Linc;

    
    //expense and income table
    @FXML
    private TableColumn<inccome, String> titleColumn1;

    @FXML
    private TableColumn<inccome, String> accountColumn1;

    @FXML
    private TableColumn<inccome, BigDecimal> amountColumn1;

    @FXML
    private TableColumn<inccome, Date> dateColumn1;

    @FXML
    private TableColumn<exxpense, String> titleColumn;

    @FXML
    private TableColumn<exxpense, String> accountColumn;

    @FXML
    private TableColumn<exxpense, BigDecimal> amountColumn;

    @FXML
    private TableColumn<exxpense, Date> dateColumn;

    
    

    @FXML
    void selectUser(ActionEvent event) {
        if (userType != null) {
            String selectedReport = userType.getSelectionModel().getSelectedItem();
            if (selectedReport != null && userText != null) {
                userText.setText(selectedReport);
            }
        }
    }

    @FXML
    private void initialize() {
        // Set user role options
        if (userType != null) {
            ObservableList<String> list = FXCollections.observableArrayList("Student", "Admin");
            userType.setItems(list);
        }

        // Display the logged-in user's name
        String username = UserSession.getInstance().getLoggedInUsername();
        if (userText != null) {
            userText.setText("Welcome, " + (username != null ? username : "Guest"));
        }
        
     // Update dashboard labels with the latest financial data
        updateDashboardData();
        populateIncomeTable();
        populateExpenseTable();
    }
    
    @FXML
    private void populateIncomeTable() {
        titleColumn1.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        accountColumn1.setCellValueFactory(cellData -> cellData.getValue().accountProperty());
        amountColumn1.setCellValueFactory(cellData -> cellData.getValue().amountProperty());
        dateColumn1.setCellValueFactory(cellData -> cellData.getValue().dateProperty());

        try {
            ObservableList<inccome> incomes = persistenceHandler.fetchLatestIncomes();
            Linc.setItems(incomes);
        } catch (Exception e) {
            showAlert("Error", "Failed to load latest incomes: " + e.getMessage());
        }
    }

    @FXML
    private void populateExpenseTable() {
        titleColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        accountColumn.setCellValueFactory(cellData -> cellData.getValue().accountProperty());
        amountColumn.setCellValueFactory(cellData -> cellData.getValue().amountProperty());
        dateColumn.setCellValueFactory(cellData -> cellData.getValue().dateProperty());

        try {
            ObservableList<exxpense> expenses = persistenceHandler.fetchLatestExpenses();
            Lexp.setItems(expenses);
        } catch (Exception e) {
            showAlert("Error", "Failed to load latest expenses: " + e.getMessage());
        }
    }
    
    @FXML
    private void updateDashboardData() {
        try {
            // Use the instance of PersistenceHandler to call non-static methods
            double currentIncome = persistenceHandler.fetchCurrentIncome();
            double currentExpense = persistenceHandler.fetchCurrentExpense();
            double totalExpenses = persistenceHandler.fetchTotalExpense();
            double totalBalance = persistenceHandler.fetchTotalBalance();

            currInc.setText(String.format("$%.2f", currentIncome));
            currExp.setText(String.format("$%.2f", currentExpense));
            totalExp.setText(String.format("$%.2f", totalExpenses));
            totalBal.setText(String.format("$%.2f", totalBalance));
        } catch (Exception e) {
            showAlert("Error", "Failed to load financial data: " + e.getMessage());
        }
    }


    // Function to handle transaction button click
    @FXML
    void goToTransactionPage(ActionEvent event) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/application/FXML/Transaction.fxml"));
        stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    
    
    @FXML
    void goToIncomePage(ActionEvent event) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/application/FXML/Income.fxml"));
        stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    
    @FXML
    void goToExpensePage(ActionEvent event) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/application/FXML/Expense.fxml"));
        stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    
    @FXML
    void goToFinGoalPage(ActionEvent event) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/application/FXML/FinancialGoals.fxml"));
        stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    
    @FXML
    void goToBudgetPage(ActionEvent event) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/application/FXML/Budget.fxml"));
        stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    
    @FXML
    void goToReportPage(ActionEvent event) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/application/FXML/GenerateReport.fxml"));
        stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    
    @FXML
    void goToManageAccountPage(ActionEvent event) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/application/FXML/ManageAccount.fxml"));
        stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    
    @FXML
    void logout(ActionEvent event) throws Exception {
        // Clear user session
        UserSession.getInstance().clearSession();

        // Redirect to login page
        Parent root = FXMLLoader.load(getClass().getResource("/application/FXML/LoginPage.fxml"));
        stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
   
}
