package modelClasses;

public class Reportt {
    private String date;
    private double income;
    private double expenses;
    private double budget;

    public Reportt(String date, double income, double expenses, double budget) {
        this.date = date;
        this.income = income;
        this.expenses = expenses;
        this.budget = budget;
    }

    public String getDate() {
        return date;
    }

    public double getIncome() {
        return income;
    }

    public double getExpenses() {
        return expenses;
    }

    public double getBudget() {
        return budget;
    }
}

