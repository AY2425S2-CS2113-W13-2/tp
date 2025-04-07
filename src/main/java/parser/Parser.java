package parser;

import java.util.Scanner;
import java.util.logging.Logger;

import command.LoginCommand;
import commandfactory.AddEventCommandFactory;
import commandfactory.AddParticipantCommandFactory;
import commandfactory.ByeCommandFactory;
import commandfactory.CommandFactory;
import commandfactory.CreateUserCommandFactory;
import commandfactory.DeleteCommandFactory;
import commandfactory.DuplicateCommandFactory;
import commandfactory.EditCommandFactory;
import commandfactory.FilterCommandFactory;
import commandfactory.FindCommandFactory;
import commandfactory.HelpCommandFactory;
import commandfactory.ListAllCommandFactory;
import commandfactory.ListCommandFactory;
import commandfactory.ListParticipantsCommandFactory;
import commandfactory.LogOutCommandFactory;
import commandfactory.LoginCommandFactory;
import logger.EventSyncLogger;
import event.EventManager;
import participant.ParticipantManager;
import ui.UI;
import exception.SyncException;

/**
 * The Parser class is responsible for parsing user input and delegating the appropriate
 * command creation to the relevant
 * CommandFactory based on the user's input. It processes the input command, validates it, and returns a corresponding
 * CommandFactory object that creates a command to be executed.
 */
public class Parser {
    private static final Logger LOGGER = Logger.getLogger(LoginCommand.class.getName());


    /**
     * The logger instance for logging information and warnings.
     */
    private static final Logger logger = EventSyncLogger.getLogger();

    /**
     * The EventManager used to manage events.
     */
    private EventManager eventManager;

    /**
     * The ParticipantManager used to manage participants.
     */
    private ParticipantManager participantManager;

    /**
     * The UI instance for user interaction.
     */
    private final UI ui;

    /**
     * The scanner used to read user input from the console.
     */
    private final Scanner scanner;

    /**
     * Constructs a Parser with the specified EventManager, ParticipantManager, and UI.
     * Uses the default scanner for input.
     *
     * @param eventManager The EventManager to manage events.
     * @param participantManager The ParticipantManager to manage participants.
     * @param ui The UI instance to interact with the user.
     */
    public Parser(EventManager eventManager, ParticipantManager participantManager, UI ui) {
        this.eventManager = eventManager;
        this.participantManager = participantManager;
        this.ui = ui;
        this.scanner = new Scanner(System.in);
        logger.info("Parser initialized with default scanner.");
    }

    /**
     * Constructs a Parser with the specified EventManager, ParticipantManager, UI, and custom scanner.
     *
     * @param eventManager The EventManager to manage events.
     * @param participantManager The ParticipantManager to manage participants.
     * @param ui The UI instance to interact with the user.
     * @param scanner The custom scanner to read user input.
     */
    public Parser(EventManager eventManager, ParticipantManager participantManager, UI ui, Scanner scanner) {
        this.eventManager = eventManager;
        this.participantManager = participantManager;
        this.ui = ui;
        this.scanner = scanner;
        logger.info("Parser initialized with custom scanner.");
    }

    /**
     * Parses the user input and returns the corresponding CommandFactory to create the appropriate command.
     *
     * @param input The user input to be parsed.
     * @return The CommandFactory that will create the corresponding command.
     * @throws SyncException If the input is invalid or the command is not recognized.
     */
    public CommandFactory parse(String input) throws SyncException {
        assert input != null;
        logger.info("Parsing command: " + input);

        String[] parts = input.trim().toLowerCase().split(" ", 2); // Split input

        if (parts.length > 0) {
            String commandWord = parts[0];
            switch (commandWord.toLowerCase()) {
            case "bye":
                logger.info("Bye command received.");
                return new ByeCommandFactory(this.participantManager, this.ui);
            case "listall":
                logger.info("List all command received.");
                return new ListAllCommandFactory(this.participantManager, this.ui);
            case "list":
                logger.info("List command received.");
                return new ListCommandFactory(this.participantManager, this.ui);
            case "add":
                logger.info("Add command received.");
                return new AddEventCommandFactory(this.participantManager, this.ui);
            case "delete":
                logger.info("Delete command received.");
                return new DeleteCommandFactory(this.participantManager, this.ui, this.eventManager);
            case "duplicate":
                logger.info("Duplicate command received.");
                return new DuplicateCommandFactory(this.participantManager, this.ui, this.eventManager);
            case "edit":
                logger.info("Edit command received.");
                return new EditCommandFactory(this.participantManager,this.eventManager, this.ui);
            case "find":
                if (parts.length > 1) {
                    logger.info("Find command received with keyword: " + parts[1]);
                    return new FindCommandFactory(parts[1]);
                } else {
                    logger.warning("Find command received without keyword.");
                    throw new SyncException("Please provide a keyword");
                }
            case "addparticipant":
                logger.info("AddParticipant command received.");
                return new AddParticipantCommandFactory(this.eventManager, this.participantManager, this.ui);
            case "listparticipants":
                logger.info("ListParticipants command received.");
                return new ListParticipantsCommandFactory(this.ui, this.eventManager, this.participantManager);
            case "filter":
                logger.info("Filter command received.");
                return new FilterCommandFactory(this.participantManager, this.ui);
            case "login":
                logger.info("Login command received.");
                return new LoginCommandFactory(this.participantManager);
            case "logout":
                logger.info("Logout command received.");
                return new LogOutCommandFactory(this.participantManager);
            case "create":
                logger.info("Create command received.");
                return new CreateUserCommandFactory(this.ui, this.participantManager);
            case "help":
                logger.info("Help command received.");
                return new HelpCommandFactory();
            default:
                logger.warning("Invalid command received: " + input);
                throw new SyncException(SyncException.invalidCommandErrorMessage(input, this.participantManager));
            }
        } else {
            logger.warning("Empty input received: " + input);
            throw new SyncException("Please provide a command");
        }
    }
}
