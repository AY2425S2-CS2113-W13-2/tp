package logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EventSyncLoggerTest {

    @BeforeEach
    public void setUp() {
        EventSyncLogger.setupLogger();
    }

    @Test
    public void testConsoleHandlerLevel() {
        Logger logger = EventSyncLogger.getLogger();

        ConsoleHandler consoleHandler = null;
        for (var handler : logger.getHandlers()) {
            if (handler instanceof ConsoleHandler) {
                consoleHandler = (ConsoleHandler) handler;
                break;
            }
        }

        assertNotNull(consoleHandler, "ConsoleHandler should be initialized");
        assertEquals(Level.INFO, consoleHandler.getLevel(), "ConsoleHandler level should be INFO");
    }

    @Test
    public void testFileHandlerInitialization() throws IOException {
        Logger logger = EventSyncLogger.getLogger();

        FileHandler fileHandler = null;
        for (var handler : logger.getHandlers()) {
            if (handler instanceof FileHandler) {
                fileHandler = (FileHandler) handler;
                break;
            }
        }

        assertNotNull(fileHandler, "FileHandler should be initialized");

        assertEquals(Level.ALL, fileHandler.getLevel(), "FileHandler level should be ALL");
    }

    @Test
    public void testLoggerInfoMessage() {
        Logger logger = EventSyncLogger.getLogger();

        LogCaptureHandler captureHandler = new LogCaptureHandler(Level.INFO);
        logger.addHandler(captureHandler);

        logger.info("Logger initialized successfully");

        assertTrue(captureHandler.getLogMessages().contains("Logger initialized successfully"),
                "Log should contain 'Logger initialized successfully'");
    }

    @Test
    public void testFileNotCreatedInTest() throws IOException {
        System.setProperty("java.util.logging.ConsoleHandler.file", "temp.log");

        Logger logger = EventSyncLogger.getLogger();
        FileHandler fileHandler = new FileHandler("temp.log");
        logger.addHandler(fileHandler);

        logger.info("Test info message");

        File logFile = new File("temp.log");
        assertTrue(logFile.exists(), "Log file should exist after logging");
        logFile.delete();
    }

    public static class LogCaptureHandler extends ConsoleHandler {
        private StringBuilder logMessages = new StringBuilder();

        public LogCaptureHandler(Level level) {
            setLevel(level);
        }

        @Override
        public void publish(java.util.logging.LogRecord record) {
            logMessages.append(record.getMessage()).append("\n");
        }

        public String getLogMessages() {
            return logMessages.toString();
        }
    }
}
