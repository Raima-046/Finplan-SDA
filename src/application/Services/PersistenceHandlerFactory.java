package application.Services;
import application.DB.MySQL;
import application.*;

public class PersistenceHandlerFactory {

    // Static instance of the factory (Singleton instance)
    private static PersistenceHandlerFactory instance;

    // Static instance of the PersistenceHandler (Singleton instance)
    private static PersistenceHandler persistenceHandlerInstance;

    // Private constructor to prevent direct instantiation
    private PersistenceHandlerFactory() {}

    // Public static method to get the Singleton instance of the factory
    public static synchronized PersistenceHandlerFactory getInstance() {
        if (instance == null) {
            instance = new PersistenceHandlerFactory();
        }
        return instance;
    }

    // Method to get the PersistenceHandler instance (Factory method)
    public PersistenceHandler getPersistenceHandler() {
        if (persistenceHandlerInstance == null) {
            // You can decide here which implementation to return
            // Example: Choose a concrete subclass like MySQLPersistenceHandler
            persistenceHandlerInstance = new MySQL();
        }
        return persistenceHandlerInstance;
    }
}
