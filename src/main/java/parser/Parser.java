package parser;

import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import commandfactory.*;
import logger.EventSyncLogger;
import event.EventManager;
import participant.ParticipantManager;
import ui.UI;
import exception.SyncException;

/**
 * Parser class responsible for interpreting and routing user commands.
 * Converts user input into appropriate CommandFactory instances.
 */
public class Parser {
    private static final Logger logger = EventSyncLogger.getLogger();

    private EventManager eventManager;
    private ParticipantManager participantManager;
    private final UI ui;
    private final Scanner scanner;

    /**
     * Constructs a Parser with default system input scanner.
     *
     * @param eventManager Manages event-related operations
     * @param participantManager Manages participant-related operations
     * @param ui User interface for interaction
     */
    public Parser(EventManager eventManager, ParticipantManager participantManager, UI ui) {
        assert eventManager != null : "EventManager cannot be null";
        assert participantManager != null : "ParticipantManager cannot be null";
        assert ui != null : "UI cannot be null";

        this.eventManager = eventManager;
        this.participantManager = participantManager;
        this.ui = ui;
        this.scanner = new Scanner(System.in);

        logger.log(Level.INFO, "Parser initialized with default scanner.");
    }

    /**
     * Constructs a Parser with a custom scanner.
     *
     * @param eventManager Manages event-related operations
     * @param participantManager Manages participant-related operations
     * @param ui User interface for interaction
     * @param scanner Custom input scanner
     */
    public Parser(EventManager eventManager, ParticipantManager participantManager, UI ui, Scanner scanner) {
        this.eventManager = eventManager;
        this.participantManager = participantManager;
        this.ui = ui;
        this.scanner = scanner;

        logger.log(Level.INFO, "Parser initialized with custom scanner.");
    }

    /**
     * Parses user input and returns the corresponding CommandFactory.
     *
     * @param input User input string to parse
     * @return Appropriate CommandFactory based on the input
     * @throws SyncException if the input is invalid or cannot be processed
     */
    public CommandFactory parse(String input) throws SyncException {
        // Validate input
        assert input != null : "Input cannot be null";

        logger.log(Level.INFO, "Parsing command: {0}", input);

        // Trim and split input
        String[] parts = input.trim().toLowerCase().split(" ", 2);

        if (parts.length > 0) {
            String commandWord = parts[0];

            try {
                // Command routing with detailed logging
                switch (commandWord) {
                case "bye":
                    logger.log(Level.INFO, "Bye command received.");
                    return new ByeCommandFactory(this.participantManager, this.ui);

                case "listall":
                    logger.log(Level.INFO, "List all command received.");
                    return new ListAllCommandFactory(this.participantManager, this.ui);

                case "list":
                    logger.log(Level.INFO, "List command received.");
                    return new ListCommandFactory(this.participantManager, this.ui);

                case "add":
                    logger.log(Level.INFO, "Add command received.");
                    return new AddEventCommandFactory(this.participantManager, this.ui);

                case "delete":
                    logger.log(Level.INFO, "Delete command received.");
                    return new DeleteCommandFactory(this.participantManager, this.ui, this.eventManager);

                case "duplicate":
                    logger.log(Level.INFO, "Duplicate command received.");
                    return new DuplicateCommandFactory(this.participantManager, this.ui, this.eventManager);

                case "edit":
                    logger.log(Level.INFO, "Edit command received.");
                    return new EditCommandFactory(this.participantManager, this.eventManager, this.ui);

                case "find":
                    if (parts.length > 1) {
                        logger.log(Level.INFO, "Find command received with keyword: {0}", parts[1]);
                        return new FindCommandFactory(parts[1]);
                    } else {
                        logger.log(Level.WARNING, "Find command received without keyword.");
                        throw new SyncException("Please provide a keyword");
                    }

                case "addparticipant":
                    logger.log(Level.INFO, "AddParticipant command received.");
                    return new AddParticipantCommandFactory(this.eventManager, this.participantManager, this.ui);

                case "listparticipants":
                    logger.log(Level.INFO, "ListParticipants command received.");
                    return new ListParticipantsCommandFactory(this.ui, this.eventManager, this.participantManager);

                case "filter":
                    logger.log(Level.INFO, "Filter command received.");
                    return new FilterCommandFactory(this.participantManager, this.ui);

                case "login":
                    logger.log(Level.INFO, "Login command received.");
                    return new LoginCommandFactory(this.participantManager);

                case "logout":
                    logger.log(Level.INFO, "Logout command received.");
                    return new LogOutCommandFactory(this.participantManager);

                case "create":
                    logger.log(Level.INFO, "Create command received.");
                    return new CreateUserCommandFactory(this.ui, this.participantManager);

                case "help":
                    logger.log(Level.INFO, "Help command received.");
                    return new HelpCommandFactory();

                default:
                    logger.log(Level.WARNING, "Invalid command received: {0}", input);
                    throw new SyncException(SyncException.invalidCommandErrorMessage(input, this.participantManager));
                }
            } catch (SyncException e) {
                // Log and rethrow SyncExceptions
                logger.log(Level.WARNING, "Sync exception during command parsing", e);
                throw e;
            } catch (Exception e) {
                // Log unexpected exceptions
                logger.log(Level.SEVERE, "Unexpected error during command parsing", e);
                throw new SyncException("An unexpected error occurred while processing the command.");
            }
        } else {
            logger.log(Level.WARNING, "Empty input received: {0}", input);
            throw new SyncException("Please provide a command");
        }
    }
}
