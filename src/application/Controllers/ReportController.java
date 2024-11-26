package application.Controllers;
import application.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;

import application.DB.MySQL;
import application.Services.PersistenceHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class ReportController {

    private Stage stage;
    private Scene scene;

    // Injecting FXML components
    @FXML
    private DatePicker dfrom;

    @FXML
    private DatePicker dto;

    @FXML
    private Button genRepBtn;

    @FXML
    private Button goback;

    @FXML
    private AnchorPane reped;

    @FXML
    private AnchorPane repns;

    @FXML
    private AnchorPane reprb;

    @FXML
    private AnchorPane repsd;

    @FXML
    private AnchorPane repte;

    @FXML
    private AnchorPane repti;

    @FXML
    private Label repEndDate;

    @FXML
    private Label repNetSavings;

    @FXML
    private Label repRemainingBudget;

    @FXML
    private Label repStartDate;

    @FXML
    private Label repTotalExpenses;

    @FXML
    private Label repTotalIncome;

    // Create a PersistenceHandler instance to fetch data from the database
    private PersistenceHandler persistenceHandler = new MySQL();

    @FXML
    void generateReport(ActionEvent event) {
        if (dfrom.getValue() == null || dto.getValue() == null) {
            showError("Please select both start and end dates.");
            return;
        }

        Date startDate = Date.valueOf(dfrom.getValue());
        Date endDate = Date.valueOf(dto.getValue());

        // Get total income and expenses from database using PersistenceHandler
        BigDecimal totalIncome = getTotalIncomeInDateRange(startDate, endDate);
        BigDecimal totalExpenses = getTotalExpensesInDateRange(startDate, endDate);

        // Calculate net savings and remaining budget
        BigDecimal netSavings = totalIncome.subtract(totalExpenses);
        BigDecimal remainingBudget = calculateRemainingBudget(totalIncome, totalExpenses);

        // Add labels to respective anchor panes instead of just updating text in labels
        addLabelToAnchorPane(repsd, "      " + startDate.toString());
        addLabelToAnchorPane(reped, "      " + endDate.toString());
        addLabelToAnchorPane(repti, "         Rs." + totalIncome.setScale(2, BigDecimal.ROUND_HALF_UP));
        addLabelToAnchorPane(repte, "         Rs." + totalExpenses.setScale(2, BigDecimal.ROUND_HALF_UP));
        addLabelToAnchorPane(repns, "         Rs." + netSavings.setScale(2, BigDecimal.ROUND_HALF_UP));
        addLabelToAnchorPane(reprb, "         Rs." + remainingBudget.setScale(2, BigDecimal.ROUND_HALF_UP));
    }

    private void addLabelToAnchorPane(AnchorPane anchorPane, String text) {
        // Create a new label with the text
        Label label = new Label(text);
        
        // Set some styling for the label if needed (e.g., font size, color)
        label.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");
        
        // Add label to the anchor pane (make sure the pane has enough space or adjust positioning)
        anchorPane.getChildren().clear();  // Clear previous content
        anchorPane.getChildren().add(label);
        AnchorPane.setTopAnchor(label, 10.0); // Adjust positioning as needed
        AnchorPane.setLeftAnchor(label, 10.0);
    }



    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Input Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Fetch total income within the specified date range
    private BigDecimal getTotalIncomeInDateRange(Date startDate, Date endDate) {
        // Fetch total income for the given date range from the database
        // Example using SQL: SELECT SUM(amount) FROM income WHERE date BETWEEN startDate AND endDate
        // You can replace the hardcoded value with an actual query in the PersistenceHandler
        return persistenceHandler.getTotalIncomeInDateRange(startDate, endDate);
    }

    // Fetch total expenses within the specified date range
    private BigDecimal getTotalExpensesInDateRange(Date startDate, Date endDate) {
        // Fetch total expenses for the given date range from the database
        // Example using SQL: SELECT SUM(amount) FROM expense WHERE date BETWEEN startDate AND endDate
        // You can replace the hardcoded value with an actual query in the PersistenceHandler
        return persistenceHandler.getTotalExpensesInDateRange(startDate, endDate);
    }

    // Calculate the remaining budget based on total income and expenses
    private BigDecimal calculateRemainingBudget(BigDecimal totalIncome, BigDecimal totalExpenses) {
        // Simple calculation for remaining budget (can be adjusted based on actual logic)
        return totalIncome.subtract(totalExpenses);
    }

    // Go back to the dashboard screen
    @FXML
    void goBackToDashboard(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/application/FXML/Dashboard.fxml"));
        stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
