package modelClasses;

import javafx.beans.property.*;

import java.time.LocalDate;

public class Budgett {

    private final StringProperty category;
    private final DoubleProperty amount;
    private final ObjectProperty<LocalDate> date;
    private final StringProperty status;
    private final StringProperty actions;

    public Budgett(String category, double amount, LocalDate date, String status, String actions) {
        this.category = new SimpleStringProperty(category);
        this.amount = new SimpleDoubleProperty(amount);
        this.date = new SimpleObjectProperty<>(date);
        this.status = new SimpleStringProperty(status);
        this.actions = new SimpleStringProperty(actions);
    }

    public String getCategory() {
        return category.get();
    }

    public void setCategory(String category) {
        this.category.set(category);
    }

    public StringProperty categoryProperty() {
        return category;
    }

    public double getAmount() {
        return amount.get();
    }

    public void setAmount(double amount) {
        this.amount.set(amount);
    }

    public DoubleProperty amountProperty() {
        return amount;
    }

    public LocalDate getDate() {
        return date.get();
    }

    public void setDate(LocalDate date) {
        this.date.set(date);
    }

    public ObjectProperty<LocalDate> dateProperty() {
        return date;
    }

    public String getStatus() {
        return status.get();
    }

    public void setStatus(String status) {
        this.status.set(status);
    }

    public StringProperty statusProperty() {
        return status;
    }

    public String getActions() {
        return actions.get();
    }

    public void setActions(String actions) {
        this.actions.set(actions);
    }

    public StringProperty actionsProperty() {
        return actions;
    }
}