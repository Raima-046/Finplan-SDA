package modelClasses;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.StringProperty;

public class Goal {

    private StringProperty goalName;
    private StringProperty goalType;
    private SimpleDoubleProperty targetAmount;
    private StringProperty deadline;
    private StringProperty reminders;

    public Goal(String goalName, String goalType, double targetAmount, String deadline, String reminders) {
        this.goalName = new SimpleStringProperty(goalName);
        this.goalType = new SimpleStringProperty(goalType);
        this.targetAmount = new SimpleDoubleProperty(targetAmount);
        this.deadline = new SimpleStringProperty(deadline);
        this.reminders = new SimpleStringProperty(reminders);
    }

    public StringProperty goalNameProperty() {
        return goalName;
    }

    public StringProperty goalTypeProperty() {
        return goalType;
    }

    public SimpleDoubleProperty targetAmountProperty() {
        return targetAmount;
    }

    public StringProperty deadlineProperty() {
        return deadline;
    }

    public StringProperty remindersProperty() {
        return reminders;
    }

    // Getters and setters (optional, based on your usage)
    public String getGoalName() {
        return goalName.get();
    }

    public String getGoalType() {
        return goalType.get();
    }

    public double getTargetAmount() {
        return targetAmount.get();
    }

    public String getDeadline() {
        return deadline.get();
    }

    public String getReminders() {
        return reminders.get();
    }

    // Setters (optional, if you need them)
    public void setGoalName(String goalName) {
        this.goalName.set(goalName);
    }

    public void setGoalType(String goalType) {
        this.goalType.set(goalType);
    }

    public void setTargetAmount(double targetAmount) {
        this.targetAmount.set(targetAmount);
    }

    public void setDeadline(String deadline) {
        this.deadline.set(deadline);
    }

    public void setReminders(String reminders) {
        this.reminders.set(reminders);
    }
}
