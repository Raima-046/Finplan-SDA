package application.Services;
import application.*;

public class UserSession {
    private static UserSession instance;

    private String loggedInUserId; // Track the logged-in user's ID
    private String loggedInUsername; // Optional: Track the username

    // Private constructor to prevent instantiation
    private UserSession() {
    }

    // Get the singleton instance
    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    // Getter and setter for the logged-in user ID
    public String getLoggedInUserId() {
        return loggedInUserId;
    }

    public void setLoggedInUserId(String loggedInUserId) {
        this.loggedInUserId = loggedInUserId;
    }

    // Getter and setter for the username
    public String getLoggedInUsername() {
        return loggedInUsername;
    }

    public void setLoggedInUsername(String loggedInUsername) {
        this.loggedInUsername = loggedInUsername;
    }

    // Clear session for logout
    public void clearSession() {
        loggedInUserId = null;
        loggedInUsername = null;
    }
}
