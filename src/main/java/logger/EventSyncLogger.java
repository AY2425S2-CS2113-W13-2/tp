package logger;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * A utility class for setting up and managing application logging.
 * <p>
 * This class provides static methods to configure and access a logger instance
 * that writes to both console and file. The logger can be used throughout
 * the application for consistent logging.
 * </p>
 *
 * @author Your Name
 * @version 1.0
 */
public final class EventSyncLogger {
    private static final Logger LOGGER = Logger.getLogger(EventSyncLogger.class.getName());
    private static boolean isInitialized = false;

    /**
     * Private constructor to prevent instantiation.
     */
    private EventSyncLogger() {
        throw new AssertionError("EventSyncLogger is a utility class and should not be instantiated");
    }

    /**
     * Initializes the logger with console and file handlers.
     * <p>
     * Configures the logger to:
     * <ul>
     *   <li>Log all levels to a file named "app.log"</li>
     *   <li>Log INFO level and above to the console</li>
     * </ul>
     * </p>
     *
     * @throws IllegalStateException if the logger initialization fails
     */
    public static synchronized void setupLogger() {
        if (isInitialized) {
            LOGGER.warning("Logger is already initialized");
            return;
        }

        try {
            LogManager.getLogManager().reset();
            LOGGER.setLevel(Level.ALL);

            setupConsoleHandler();
            setupFileHandler();

            isInitialized = true;
            LOGGER.info("Logger initialized successfully");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize logger", e);
            throw new IllegalStateException("Logger initialization failed", e);
        }
    }

    /**
     * Configures the console handler for the logger.
     */
    private static void setupConsoleHandler() {
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.INFO);
        LOGGER.addHandler(consoleHandler);
    }

    /**
     * Configures the file handler for the logger.
     *
     * @throws IOException if there's an error creating the log file
     */
    private static void setupFileHandler() throws IOException {
        assert LOGGER != null : "Logger should not be null when setting up file handler";

        FileHandler fileHandler = new FileHandler("app.log");
        fileHandler.setFormatter(new SimpleFormatter());
        fileHandler.setLevel(Level.ALL);
        LOGGER.addHandler(fileHandler);
    }

    /**
     * Returns the application logger instance.
     * <p>
     * Note: The logger should be initialized by calling {@link #setupLogger()}
     * before using this method.
     * </p>
     *
     * @return the configured Logger instance
     * @throws IllegalStateException if the logger has not been initialized
     */
    public static Logger getLogger() {
        if (!isInitialized) {
            throw new IllegalStateException("Logger has not been initialized. Call setupLogger() first.");
        }
        return LOGGER;
    }
}
