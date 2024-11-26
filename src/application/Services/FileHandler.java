package application.Services;
import application.*;

import modelClasses.Transactionns;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class FileHandler {

    // Create a new file with a header
    public void createFileWithHeader(String filePath, String header) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(header);
            writer.newLine();
        }
    }

    // Append a single transaction to the file
    public boolean appendTransactionToFile(String filePath, Transactionns transaction) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(transaction.getCategory() + "," +
                    transaction.getTransactionDate() + "," +
                    transaction.getAccount() + "," +
                    transaction.getAmount() + "," +
                    transaction.getTransactionType());
            writer.newLine();
            return true;
        } catch (IOException e) {
            System.err.println("Error appending transaction to file: " + e.getMessage());
            return false;
        }
    }
}
