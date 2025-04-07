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

import java.io.InputStream;
import java.util.Scanner;

/**
 * The EventSync class is the main entry point for the event synchronization system.
 * It initializes the UI, event manager, participant manager, parser, and storage for event and user data.
 * This class processes user input and manages the synchronization of events and participants through commands.
 */
public class EventSync {
    private final UI ui;
    private final EventManager eventManager;
    private final ParticipantManager participantManager;
    private final Parser parser;
    private final Scanner scanner;
    private final Storage storage;
    private final UserStorage userStorage;

    /**
     * Constructs an EventSync instance with specified file paths for event and user data.
     * Initializes the user interface, event manager, participant manager, parser, and storage components.
     *
     * @param filePathEvent The file path for event data storage.
     * @param filePathUser The file path for user data storage.
     * @throws SyncException If an error occurs during initialization.
     */
    public EventSync(String filePathEvent, String filePathUser) throws SyncException {
        scanner = new Scanner(System.in);
        ui = new UI();
        userStorage = new UserStorage(filePathUser);
        storage = new Storage(filePathEvent, userStorage);
        participantManager = new ParticipantManager(userStorage.loadUsers(), ui, userStorage);
        eventManager = new EventManager(storage.loadEvents(), ui, storage, userStorage);
        parser = new Parser(eventManager, participantManager, ui, scanner);
    }

    /**
     * Constructs an EventSync instance with an InputStream
     * for reading user input and specified file paths for event and user data.
     * Initializes the user interface, event manager, participant manager, parser, and storage components.
     *
     * @param inputStream The InputStream for reading user input (e.g., System.in).
     * @param filePathEvent The file path for event data storage.
     * @param filePathUser The file path for user data storage.
     * @throws SyncException If an error occurs during initialization.
     */
    public EventSync(InputStream inputStream,
                     String filePathEvent, String filePathUser) throws SyncException {
        userStorage = new UserStorage(filePathUser);
        ui = new UI();
        storage = new Storage(filePathEvent, userStorage);
        eventManager = new EventManager(storage.loadEvents(), ui, storage, userStorage);
        participantManager = new ParticipantManager(userStorage.loadUsers(), ui, userStorage);
        scanner = new Scanner(inputStream);
        parser = new Parser(eventManager, participantManager, ui, this.scanner);
    }

    /**
     * Starts the event synchronization application.
     * It continuously reads user input, processes commands, and executes them.
     * The loop will exit when an exit command is executed.
     */
    public void run() {
        ui.showWelcomeMessage();
        boolean isExit = false;
        while (!isExit && scanner.hasNextLine()) {
            String input = scanner.nextLine();
            try {
                CommandFactory factory = parser.parse(input);
                Command c = factory.createCommand();
                c.execute(eventManager, ui, participantManager);
                isExit = c.isExit();
            } catch (SyncException e) {
                ui.showMessage(e.getMessage());
            }
            if (!isExit) {
                System.out.print("\nEnter your command: ");
            }
        }
        scanner.close();
    }

    /**
     * Main method to run the EventSync application with default file paths for event and user data.
     *
     * @param args Command-line arguments (not used in this implementation).
     * @throws SyncException If an error occurs during initialization or execution.
     */
    public static void main(String[] args) throws SyncException {
        new EventSync("./data/seedu.EventSync.txt", "./data/seedu.UserSync.txt" ).run();
    }
}
