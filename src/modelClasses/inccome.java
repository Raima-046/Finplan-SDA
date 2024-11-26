package modelClasses;

import java.math.BigDecimal;
import java.sql.Date;
import javafx.beans.property.*;

public class inccome {

    private StringProperty name; 
    private StringProperty category;
    private StringProperty account;
    private ObjectProperty<BigDecimal> amount;
    private StringProperty description;
    private ObjectProperty<Date> date;

    public inccome(String name, String category, BigDecimal amount, String account, String description, Date date) {
        this.name = new SimpleStringProperty(name); 
        this.category = new SimpleStringProperty(category);
        this.amount = new SimpleObjectProperty<>(amount);
        this.account = new SimpleStringProperty(account);
        this.description = new SimpleStringProperty(description);
        this.date = new SimpleObjectProperty<>(date);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public StringProperty categoryProperty() {
        return category;
    }

    public ObjectProperty<BigDecimal> amountProperty() {
        return amount;
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public ObjectProperty<Date> dateProperty() {
        return date;
    }

    public StringProperty accountProperty() {
        return account; 
    }

    public String getAccount() {
        return account.get();
    }

    public void setAccount(String account) {
        this.account.set(account);
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getCategory() {
        return category.get();
    }

    public void setCategory(String category) {
        this.category.set(category);
    }

    public BigDecimal getAmount() {
        return amount.get();
    }

    public void setAmount(BigDecimal amount) {
        this.amount.set(amount);
    }

    public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public Date getDate() {
        return date.get();
    }

    public void setDate(Date date) {
        this.date.set(date);
    }

    @Override
    public String toString() {
        return "Income{" +
                "name='" + name.get() + '\'' + 
                ", category='" + category.get() + '\'' +
                ", amount=" + amount.get() +
                ", account='" + account.get() + '\'' +
                ", description='" + description.get() + '\'' +
                ", date=" + date.get() +
                '}';
    }
}
