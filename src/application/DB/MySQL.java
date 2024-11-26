package application.DB;
import application.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import application.Services.FileHandler;
import application.Services.PersistenceHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import modelClasses.Reportt;
import modelClasses.Transactionns;
import modelClasses.exxpense;
import modelClasses.inccome;


public class MySQL extends PersistenceHandler {

    private static final String URL = "jdbc:mysql://127.0.0.1:3306/finplan"; // Replace with your DB URL
    private static final String USER = "root"; // Replace with your DB username
    private static final String PASSWORD = "mysql7890"; // Replace with your DB password

    private static Connection connection;

    // Establish a single connection to the database
    public Connection getConnection() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Database connected successfully!");
            } catch (SQLException e) {
                System.err.println("Failed to connect to the database: " + e.getMessage());
            }
        }
        return connection;
    }

    // Method to execute SELECT queries
    public ResultSet executeQuery(String query, Object... params) throws SQLException {
        PreparedStatement statement = prepareStatement(query, params);
        return statement.executeQuery();
    }

    // Method to execute INSERT, UPDATE, DELETE queries
    public int executeUpdate(String query, Object... params) throws SQLException {
        PreparedStatement statement = prepareStatement(query, params);
        return statement.executeUpdate();
    }

    // Helper method to prepare a statement with parameters
    private PreparedStatement prepareStatement(String query, Object... params) throws SQLException {
        PreparedStatement statement = getConnection().prepareStatement(query);
        for (int i = 0; i < params.length; i++) {
            statement.setObject(i + 1, params[i]);
        }
        return statement;
    }

    private static final String FILE_PATH = "transactions.txt"; // File path for transactions
    private final FileHandler fileHandler = new FileHandler(); // File handler instance

    // Method to save a transaction
    public boolean saveTransaction(String category, String transactionDate, String account, double amount, String transactionType) {
        String query = "INSERT INTO Transactions (category, transaction_date, account, amount, transaction_type) VALUES (?, ?, ?, ?, ?)";
        try {
            // Save to database
            int rowsAffected = executeUpdate(query, category, transactionDate, account, amount, transactionType);
            System.out.println("Rows affected: " + rowsAffected); // Debug: Check how many rows were affected

            if (rowsAffected > 0) {
                // Prepare the transaction details as a string
                String transactionDetails =
                        "Category: " + category + "\n" +
                                "Date: " + transactionDate + "\n" +
                                "Account: " + account + "\n" +
                                "Amount: " + amount + "\n" +
                                "Type: " + transactionType + "\n" +
                                "----------------------------------\n";

                // Check if file exists and create it if not
                File file = new File(FILE_PATH);
                if (!file.exists()) {
                    file.createNewFile(); // Create the file if it doesn't exist
                }

                // Append transaction details to the file
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
                    writer.write(transactionDetails);
                }

                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error while saving transaction to database: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error while saving transaction to file: " + e.getMessage());
        }

        return false;
    }


    //Method to delete a transaction
    public boolean deleteTransaction(String category, String transactionDate, String account, double amount, String transactionType) {
        String query = "DELETE FROM Transactions WHERE category = ? AND transaction_date = ? AND account = ? AND amount = ? AND transaction_type = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, category);
            preparedStatement.setString(2, transactionDate);
            preparedStatement.setString(3, account);
            preparedStatement.setDouble(4, amount);
            preparedStatement.setString(5, transactionType);
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0; // Return true if a row was deleted
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Method to save an expense
    public boolean saveExpense(String name, String category, String expenseDate, String account, double amount, String description) {
        String sql = "INSERT INTO Expense (name, category, date, account, amount, description) VALUES (?, ?, ?, ?, ?, ?)";
        try {
            int rowsAffected = executeUpdate(sql, name, category, expenseDate, account, BigDecimal.valueOf(amount), description);
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteExpense(String name, String category, String date, String account, double amount) {
        String query = "DELETE FROM Expense WHERE name = ? AND category = ? AND date = ? AND account = ? AND amount = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, name);
            statement.setString(2, category);
            statement.setString(3, date);
            statement.setString(4, account);
            statement.setDouble(5, amount);

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;  // Return true if at least one row is deleted
        } catch (SQLException e) {
            e.printStackTrace();
            return false;  // Return false if any exception occurs
        }
    }



    // Method to save income
    public boolean saveIncome(String name, String category, String date, String account, double amount, String description) {
        String query = "INSERT INTO income (name, category, date, account, amount, description) VALUES (?, ?, ?, ?, ?, ?)";
        try {
            int rowsAffected = executeUpdate(query, name, category, date, account, BigDecimal.valueOf(amount), description);
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteIncome(String name, String category, String date, String account, double amount) {
        String query = "DELETE FROM income WHERE name = ? AND category = ? AND date = ? AND account = ? AND amount = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, category);
            preparedStatement.setString(3, date);  // Ensure date is in the correct format
            preparedStatement.setString(4, account);
            preparedStatement.setDouble(5, amount);

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0; // Return true if a row was deleted
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean saveGoal(String goalName, String goalType, double targetAmount, String deadline, String reminders) {
        String insertQuery = "INSERT INTO Goals (goal_name, goal_type, target_amount, deadline, reminders) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
            preparedStatement.setString(1, goalName);
            preparedStatement.setString(2, goalType);
            preparedStatement.setDouble(3, targetAmount);
            preparedStatement.setString(4, deadline);
            preparedStatement.setString(5, reminders);

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;  // Return true if one or more rows were inserted
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean deleteGoal(String goalName) {
        String query = "DELETE FROM Goals WHERE goal_name = ?";  // Corrected to use a parameter

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, goalName);  // Set the goal name as the parameter
            int rowsAffected = stmt.executeUpdate();

            // If rowsAffected is greater than 0, the goal was successfully deleted
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean saveBudget(String category, double amount, java.sql.Date date) {
        String query = "INSERT INTO Budget (category, amount, date) VALUES (?, ?, ?)";
        try {
            int rowsAffected = executeUpdate(query, category, BigDecimal.valueOf(amount), date);
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error while saving budget: " + e.getMessage());
            return false;
        }
    }

    // Method to fetch all budgets from the database
    public ResultSet fetchAllBudgets() {
        String query = "SELECT * FROM Budget";
        try {
            return executeQuery(query);
        } catch (SQLException e) {
            System.err.println("Error while fetching budgets: " + e.getMessage());
            return null;
        }
    }

    // Method to DELETE a budget entry from the database
    public boolean deleteBudget(String category, LocalDate date) {
        String query = "DELETE FROM Budget WHERE category = ? AND date = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, category);
            preparedStatement.setDate(2, java.sql.Date.valueOf(date));
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Transactionns> getAllTransactions() {
        String query = "SELECT category, transaction_date, account, amount, transaction_type FROM Transactions";
        List<Transactionns> transactions = new ArrayList<>();
        try (ResultSet resultSet = executeQuery(query)) {
            while (resultSet.next()) {
                transactions.add(new Transactionns(
                        resultSet.getString("category"),
                        resultSet.getDate("transaction_date").toLocalDate(),
                        resultSet.getString("account"),
                        resultSet.getDouble("amount"),
                        resultSet.getString("transaction_type")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }


    public boolean validateOldPassword(String username, String plainPassword) {
        String query = "SELECT password FROM users WHERE username = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String storedPassword = rs.getString("password").trim(); // Trim to avoid trailing spaces
                System.out.println("Stored password: " + storedPassword); // Debugging
                System.out.println("Input password: " + plainPassword); // Debugging
                return storedPassword.equals(plainPassword); // Compare plain passwords directly
            } else {
                System.out.println("No user found with username: " + username); // Debugging
            }
        } catch (SQLException e) {
            System.err.println("Error while validating old password: " + e.getMessage());
        }
        return false;
    }

    public boolean updatePassword(String username, String newPassword) {
        if (username == null || username.isEmpty()) {
            System.err.println("Username not provided; skipping username validation."); // Debugging
        }

        String updateQuery = "UPDATE users SET password = ? WHERE username = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(updateQuery)) {
            stmt.setString(1, newPassword); // Set the new plain password directly
            stmt.setString(2, username);
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0; // Return true if at least one row is updated
        } catch (SQLException e) {
            System.err.println("Error while updating password: " + e.getMessage());
            return false;
        }
    }

    public boolean updateUsername(String oldUsername, String newUsername) {
        String updateQuery = "UPDATE users SET username = ? WHERE username = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(updateQuery)) {
            stmt.setString(1, newUsername);  // New username
            stmt.setString(2, oldUsername); // Old username
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;  // Return true if at least one row is updated
        } catch (SQLException e) {
            System.err.println("Error while updating username: " + e.getMessage());
            return false;
        }
    }


    // Get total income within a date range from the database
    public BigDecimal getTotalIncomeInDateRange(java.sql.Date startDate, java.sql.Date endDate) {
        BigDecimal totalIncome = BigDecimal.ZERO;

        String query = "SELECT SUM(amount) FROM income WHERE date BETWEEN ? AND ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setDate(1, startDate);
            ps.setDate(2, endDate);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                totalIncome = rs.getBigDecimal(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return totalIncome != null ? totalIncome : BigDecimal.ZERO;
    }

    // Get total expenses within a date range from the database
    public BigDecimal getTotalExpensesInDateRange(java.sql.Date startDate, java.sql.Date endDate) {
        BigDecimal totalExpenses = BigDecimal.ZERO;

        String query = "SELECT SUM(amount) FROM expense WHERE date BETWEEN ? AND ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setDate(1, startDate);
            ps.setDate(2, endDate);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                totalExpenses = rs.getBigDecimal(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return totalExpenses != null ? totalExpenses : BigDecimal.ZERO;
    }

    public double fetchTotalBalance() {
        String query = "SELECT COALESCE((SELECT SUM(amount) FROM Income) - (SELECT SUM(amount) FROM Expense), 0) AS total_balance";
        try (ResultSet rs = executeQuery(query)) {
            if (rs.next()) return rs.getDouble("total_balance");
        } catch (SQLException e) {
            System.err.println("Error fetching total balance: " + e.getMessage());
        }
        return 0.0;
    }

    public double fetchCurrentExpense() {
        String query = "SELECT COALESCE(SUM(amount), 0) AS current_expense FROM Expense WHERE date = CURDATE()";
        try (ResultSet rs = executeQuery(query)) {
            if (rs.next()) return rs.getDouble("current_expense");
        } catch (SQLException e) {
            System.err.println("Error fetching current expense: " + e.getMessage());
        }
        return 0.0;
    }

    public double fetchTotalExpense() {
        String query = "SELECT COALESCE(SUM(amount), 0) AS total_expense FROM Expense";
        try (ResultSet rs = executeQuery(query)) {
            if (rs.next()) return rs.getDouble("total_expense");
        } catch (SQLException e) {
            System.err.println("Error fetching total expense: " + e.getMessage());
        }
        return 0.0;
    }

    // Financial data methods with double return type
    public double fetchCurrentIncome() {
        String query = "SELECT COALESCE(SUM(amount), 0) AS current_income FROM Income WHERE date = CURDATE()";
        try (ResultSet rs = executeQuery(query)) {
            if (rs.next()) return rs.getDouble("current_income");
        } catch (SQLException e) {
            System.err.println("Error fetching current income: " + e.getMessage());
        }
        return 0.0;
    }


    public List<Reportt> getReportData(LocalDate startDate, LocalDate endDate) {
        String query = "SELECT date, income, expenses, budget FROM Report WHERE date BETWEEN ? AND ?";
        List<Reportt> reportDataList = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setDate(1, Date.valueOf(startDate));
            stmt.setDate(2, Date.valueOf(endDate));
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                reportDataList.add(new Reportt(
                        resultSet.getString("date"),
                        resultSet.getDouble("income"),
                        resultSet.getDouble("expenses"),
                        resultSet.getDouble("budget")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reportDataList;
    }


    public ObservableList<inccome> fetchLatestIncomes() throws Exception {
        ObservableList<inccome> incomes = FXCollections.observableArrayList();
        String query = "SELECT name, category, amount, account, description, date FROM income ORDER BY date DESC LIMIT 5";

        ResultSet rs = executeQuery(query);
        while (rs.next()) {
            inccome income = new inccome(
                    rs.getString("name"),
                    rs.getString("category"),
                    rs.getBigDecimal("amount"),
                    rs.getString("account"),
                    rs.getString("description"),
                    rs.getDate("date")
            );
            incomes.add(income);
        }
        return incomes;
    }

    public ObservableList<exxpense> fetchLatestExpenses() throws Exception {
        ObservableList<exxpense> expenses = FXCollections.observableArrayList();
        String query = "SELECT name, category, amount, account, description, date FROM expense ORDER BY date DESC LIMIT 5";

        ResultSet rs = executeQuery(query);
        while (rs.next()) {
            exxpense expense = new exxpense(
                    rs.getString("name"),
                    rs.getString("category"),
                    rs.getBigDecimal("amount"),
                    rs.getString("account"),
                    rs.getString("description"),
                    rs.getDate("date")
            );
            expenses.add(expense);
        }
        return expenses;
    }


    // Close the connection (optional for cleanup)
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Database connection closed.");
            } catch (SQLException e) {
                System.err.println("Failed to close the connection: " + e.getMessage());
            }
        }
    }
}
