package logger;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class EventSyncLogger {
    private static final Logger LOGGER = Logger.getLogger(EventSyncLogger.class.getName());

    public static void setupLogger() {
        LogManager.getLogManager().reset();

        LOGGER.setLevel(Level.ALL);

        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.INFO);
        LOGGER.addHandler(consoleHandler);

        try {
            FileHandler fileHandler = new FileHandler("app.log");
            fileHandler.setFormatter(new SimpleFormatter());
            fileHandler.setLevel(Level.ALL);
            LOGGER.addHandler(fileHandler);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error initializing file handler", e);
        }

        LOGGER.info("Logger initialized successfully");
    }

    public static Logger getLogger() {
        return LOGGER;
    }
}
