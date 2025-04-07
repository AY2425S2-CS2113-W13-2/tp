package logger;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * This class sets up and provides a logger for event synchronization operations.
 * The logger writes log messages to both the console and a log file.
 * It supports various logging levels, with different output for console and file handlers.
 */
public class EventSyncLogger {

    /**
     * The logger instance used for logging event synchronization activities.
     */
    private static final Logger LOGGER = Logger.getLogger(EventSyncLogger.class.getName());

    /**
     * Sets up the logger configuration by initializing the console and file handlers.
     * The console handler logs messages at the INFO level, and the file handler logs all levels.
     * The log messages are formatted using a simple formatter.
     */
    public static void setupLogger() {
        LogManager.getLogManager().reset();

        LOGGER.setLevel(Level.ALL);

        // Console handler configuration
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.INFO);
        LOGGER.addHandler(consoleHandler);

        try {
            // File handler configuration
            FileHandler fileHandler = new FileHandler("app.log");
            fileHandler.setFormatter(new SimpleFormatter());
            fileHandler.setLevel(Level.ALL);
            LOGGER.addHandler(fileHandler);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error initializing file handler", e);
        }

        LOGGER.info("Logger initialized successfully");
    }

    /**
     * Returns the logger instance for use in other parts of the application.
     *
     * @return The logger instance.
     */
    public static Logger getLogger() {
        return LOGGER;
    }
}
