package application.Services;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.List;

import javafx.collections.ObservableList;
import modelClasses.Reportt;
import modelClasses.Transactionns;
import modelClasses.exxpense;
import modelClasses.inccome;
import application.*;

public abstract class PersistenceHandler {

    // Abstract method to get a database connection
    public abstract Connection getConnection();

    // Method to execute SELECT queries
    public abstract ResultSet executeQuery(String query, Object... params) throws SQLException;

    // Method to execute INSERT, UPDATE, DELETE queries
    public abstract int executeUpdate(String query, Object... params) throws SQLException;

    // Method to save a transaction
    public abstract boolean saveTransaction(String category, String transactionDate, String account, double amount, String transactionType);

    // Other abstract methods
    public abstract boolean saveExpense(String name, String category, String expenseDate, String account, double amount, String description);

    public abstract boolean saveIncome(String name, String category, String date, String account, double amount, String description);

    public abstract boolean saveGoal(String goalName, String goalType, double targetAmount, String deadline, String reminders);

    public abstract boolean deleteGoal(String goalName);

    public abstract boolean saveBudget(String category, double amount, java.sql.Date date);

    public abstract ResultSet fetchAllBudgets();

    public abstract List<Transactionns> getAllTransactions();

    public abstract boolean validateOldPassword(String username, String plainPassword);

    public abstract boolean updatePassword(String username, String newPassword);

    public abstract boolean updateUsername(String oldUsername, String newUsername);

    public abstract BigDecimal getTotalIncomeInDateRange(java.sql.Date startDate, java.sql.Date endDate);

    public abstract BigDecimal getTotalExpensesInDateRange(java.sql.Date startDate, java.sql.Date endDate);

    public abstract double fetchTotalBalance();

    public abstract double fetchCurrentExpense();

    public abstract double fetchTotalExpense();

    public abstract double fetchCurrentIncome();

    public abstract List<Reportt> getReportData(LocalDate startDate, LocalDate endDate);

    public abstract ObservableList<inccome> fetchLatestIncomes() throws Exception;

    public abstract ObservableList<exxpense> fetchLatestExpenses() throws Exception;

    public abstract boolean deleteExpense(String name, String category, String date, String account, double amount);

    public abstract boolean deleteTransaction(String category, String transactionDate, String account, double amount, String transactionType);

    public abstract boolean deleteBudget(String category, LocalDate date);

    public abstract boolean deleteIncome(String name, String category, String date, String account, double amount);

    // Method to close the connection
    public abstract void closeConnection();

}