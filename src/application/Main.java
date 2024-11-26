package application;
import application.*;

import application.Services.PersistenceHandler;
import application.Services.PersistenceHandlerFactory;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            // Initialize the PersistenceHandlerFactory
            PersistenceHandlerFactory factory = PersistenceHandlerFactory.getInstance();
            PersistenceHandler persistenceHandler = factory.getPersistenceHandler();

            // Load the FXML file
            Parent root = FXMLLoader.load(getClass().getResource("FXML/Dashboard.fxml"));
            Scene scene = new Scene(root);

            // Add the CSS file
            String css = this.getClass().getResource("application.css").toExternalForm();
            scene.getStylesheets().add(css);

            primaryStage.setTitle("FinPlan");
            primaryStage.setMinHeight(450);
            primaryStage.setMinWidth(600);
            primaryStage.setScene(scene);
            primaryStage.show();




        } catch (Exception e) {
            e.printStackTrace(); // Prints the stack trace to help identify the issue
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
