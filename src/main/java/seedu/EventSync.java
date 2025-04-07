package seedu;

import commandfactory.CommandFactory;
import event.EventManager;
import parser.Parser;
import participant.ParticipantManager;
import storage.Storage;
import storage.UserStorage;
import ui.UI;
import exception.SyncException;
import command.Command;
import logger.EventSyncLogger;

import java.io.InputStream;
import java.util.Scanner;
import java.util.Objects;
import java.util.logging.Level;

/**
 * The main class for the EventSync application that coordinates all components.
 * <p>
 * This class serves as the entry point and central controller for the application,
 * managing the initialization of all components and the main execution loop.
 * </p>
 *
 * @author Your Name
 * @version 1.0
 */
public final class EventSync {
    private final UI ui;
    private final EventManager eventManager;
    private final ParticipantManager participantManager;
    private final Parser parser;
    private final Scanner scanner;
    private final Storage storage;
    private final UserStorage userStorage;

    static {
        EventSyncLogger.setupLogger();
    }

    /**
     * Constructs an EventSync instance with default input (System.in).
     *
     * @param filePathEvent the file path for event data storage (must not be null or empty)
     * @param filePathUser the file path for user data storage (must not be null or empty)
     * @throws SyncException if initialization fails
     * @throws IllegalArgumentException if file paths are invalid
     * @throws NullPointerException if file paths are null
     */
    public EventSync(String filePathEvent, String filePathUser) throws SyncException {
        this(System.in, filePathEvent, filePathUser);
    }

    /**
     * Constructs an EventSync instance with a specified input stream.
     *
     * @param inputStream the input stream to use for command input (must not be null)
     * @param filePathEvent the file path for event data storage (must not be null or empty)
     * @param filePathUser the file path for user data storage (must not be null or empty)
     * @throws SyncException if initialization fails
     * @throws IllegalArgumentException if file paths are invalid
     * @throws NullPointerException if any parameter is null
     */
    public EventSync(InputStream inputStream, String filePathEvent, String filePathUser)
            throws SyncException {
        Objects.requireNonNull(inputStream, "Input stream cannot be null");
        validateFilePath(filePathEvent, "Event data file path");
        validateFilePath(filePathUser, "User data file path");

        EventSyncLogger.getLogger().info("Initializing EventSync application");

        try {
            this.scanner = new Scanner(inputStream);
            this.ui = new UI();
            this.userStorage = new UserStorage(filePathUser);
            this.storage = new Storage(filePathEvent, userStorage);

            // Load data with proper error handling
            this.participantManager = new ParticipantManager(
                    userStorage.loadUsers(), ui, userStorage);
            this.eventManager = new EventManager(
                    storage.loadEvents(), ui, storage, userStorage);
            this.parser = new Parser(eventManager, participantManager, ui, this.scanner);

            EventSyncLogger.getLogger().info("EventSync initialized successfully");
        } catch (SyncException e) {
            EventSyncLogger.getLogger().log(Level.SEVERE, "Failed to initialize EventSync", e);
            throw e;
        } catch (Exception e) {
            String errorMsg = "Unexpected error during initialization";
            EventSyncLogger.getLogger().log(Level.SEVERE, errorMsg, e);
            throw new SyncException(e.getMessage());
        }
    }

    /**
     * Validates that a file path is not null or empty.
     *
     * @param filePath the file path to validate
     * @param description description of the file path for error messages
     * @throws IllegalArgumentException if file path is empty
     * @throws NullPointerException if file path is null
     */
    private void validateFilePath(String filePath, String description) {
        Objects.requireNonNull(filePath, description + " cannot be null");
        if (filePath.trim().isEmpty()) {
            throw new IllegalArgumentException(description + " cannot be empty");
        }
    }

    /**
     * Starts the main application loop.
     * <p>
     * This method displays the welcome message and processes user commands
     * until an exit command is received.
     * </p>
     */
    public void run() {
        assert ui != null : "UI should be initialized";
        assert scanner != null : "Scanner should be initialized";

        ui.showWelcomeMessage();
        boolean isExit = false;

        EventSyncLogger.getLogger().info("Starting EventSync main loop");

        while (!isExit && scanner.hasNextLine()) {
            System.out.print("\nEnter your command: ");
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                continue;
            }

            EventSyncLogger.getLogger().fine("Processing command: " + input);

            try {
                CommandFactory factory = parser.parse(input);
                Command command = factory.createCommand();
                command.execute(eventManager, ui, participantManager);
                isExit = command.isExit();

                if (isExit) {
                    EventSyncLogger.getLogger().info("Exit command received");
                }
            } catch (SyncException e) {
                EventSyncLogger.getLogger().log(Level.WARNING, "Command execution failed", e);
                ui.showMessage(e.getMessage());
            } catch (Exception e) {
                String errorMsg = "Unexpected error processing command";
                EventSyncLogger.getLogger().log(Level.SEVERE, errorMsg, e);
                ui.showMessage(errorMsg + ": " + e.getMessage());
            }
        }

        scanner.close();
        EventSyncLogger.getLogger().info("EventSync application terminated");
    }

    /**
     * The main entry point for the EventSync application.
     *
     * @param args command line arguments (not used)
     * @throws SyncException if application initialization fails
     */
    public static void main(String[] args) throws SyncException {
        try {
            new EventSync("./data/seedu.EventSync.txt", "./data/seedu.UserSync.txt").run();
        } catch (SyncException e) {
            EventSyncLogger.getLogger().log(Level.SEVERE, "Application failed", e);
            System.err.println("Fatal error: " + e.getMessage());
            System.exit(1);
        }
    }
}