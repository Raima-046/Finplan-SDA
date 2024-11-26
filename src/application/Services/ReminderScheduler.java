package application.Services;
import application.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Timer;
import java.util.TimerTask;

import application.DB.MySQL;
import javafx.application.Platform;
import javafx.scene.control.Alert;

public class ReminderScheduler {

    private Timer timer;
    private PersistenceHandler persistenceHandler;

    public ReminderScheduler() {
        this.timer = new Timer(true);  // Daemon thread
        this.persistenceHandler = new MySQL();
    }

    public void startReminderTask() {
        // Schedule the task to run once a day (at midnight)
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                checkReminders();
            }
        }, 0, 24 * 60 * 60 * 1000);  // Every 24 hours
    }

    private void checkReminders() {
        String query = "SELECT goal_name, reminders, deadline FROM Goals";
        try {
            ResultSet resultSet = persistenceHandler.executeQuery(query);
            while (resultSet.next()) {
                String goalName = resultSet.getString("goal_name");
                String reminders = resultSet.getString("reminders");
                String deadline = resultSet.getString("deadline");

                // Calculate remaining time for the deadline
                String timeLeftMessage = calculateTimeLeft(deadline);

                // Check for daily, weekly, or monthly reminders
                if (reminders.contains("daily")) {
                    sendReminder(goalName, "Daily reminder for your goal: " + goalName + "\n" + timeLeftMessage);
                }
                if (reminders.contains("weekly")) {
                    sendReminder(goalName, "Weekly reminder for your goal: " + goalName + "\n" + timeLeftMessage);
                }
                if (reminders.contains("monthly")) {
                    sendReminder(goalName, "Monthly reminder for your goal: " + goalName + "\n" + timeLeftMessage);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String calculateTimeLeft(String deadline) {
        try {
            LocalDate currentDate = LocalDate.now();
            LocalDate deadlineDate = LocalDate.parse(deadline, DateTimeFormatter.ISO_DATE);

            long daysRemaining = ChronoUnit.DAYS.between(currentDate, deadlineDate);

            if (daysRemaining > 0) {
                return daysRemaining + " day(s) remaining until the deadline.";
            } else if (daysRemaining == 0) {
                return "Deadline is today!";
            } else {
                return "The deadline has passed.";
            }
        } catch (Exception e) {
            return "Invalid deadline format.";
        }
    }

    private void sendReminder(String goalName, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Reminder");
            alert.setHeaderText("Reminder for goal: " + goalName);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}
