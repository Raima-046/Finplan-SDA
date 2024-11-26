package modelClasses;

import java.time.LocalDate;

import javafx.beans.property.*;

public class Transactionns {
    private StringProperty category;
    private ObjectProperty<LocalDate> transactionDate;
    private StringProperty account;
    private DoubleProperty amount;
    private StringProperty transactionType;

    // Constructor
    public Transactionns(String category, LocalDate transactionDate, String account, double amount, String transactionType) {
        this.category = new SimpleStringProperty(category);
        this.transactionDate = new SimpleObjectProperty<>(transactionDate);
        this.account = new SimpleStringProperty(account);
        this.amount = new SimpleDoubleProperty(amount);
        this.transactionType = new SimpleStringProperty(transactionType);
    }

    // Getters and setters for properties
    public StringProperty categoryProperty() {
        return category;
    }

    public ObjectProperty<LocalDate> transactionDateProperty() {
        return transactionDate;
    }

    public StringProperty accountProperty() {
        return account;
    }

    public DoubleProperty amountProperty() {
        return amount;
    }

    public StringProperty transactionTypeProperty() {
        return transactionType;
    }

    public String getCategory() {
        return category.get();
    }

    public void setCategory(String category) {
        this.category.set(category);
    }

    public LocalDate getTransactionDate() {
        return transactionDate.get();
    }

    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate.set(transactionDate);
    }

    public String getAccount() {
        return account.get();
    }

    public void setAccount(String account) {
        this.account.set(account);
    }

    public double getAmount() {
        return amount.get();
    }

    public void setAmount(double amount) {
        this.amount.set(amount);
    }

    public String getTransactionType() {
        return transactionType.get();
    }

    public void setTransactionType(String transactionType) {
        this.transactionType.set(transactionType);
    }
}
