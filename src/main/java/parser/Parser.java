package parser;

import java.util.Scanner;
import java.util.logging.Logger;
import commandFactory.AddEventCommandFactory;
import commandFactory.AddParticipantCommandFactory;
import commandFactory.ByeCommandFactory;
import commandFactory.CommandFactory;
import commandFactory.CreateUserCommandFactory;
import commandFactory.DeleteCommandFactory;
import commandFactory.DuplicateCommandFactory;
import commandFactory.EditCommandFactory;
import commandFactory.FilterCommandFactory;
import commandFactory.FindCommandFactory;
import commandFactory.ListCommandFactory;
import commandFactory.ListParticipantsCommandFactory;
import commandFactory.LoginCommandFactory;
import commandFactory.LogOutCommandFactory;
import logger.EventSyncLogger;
import event.EventManager;
import participant.ParticipantManager;
import ui.UI;
import exception.SyncException;


public class Parser {
    private static final Logger logger = EventSyncLogger.getLogger();
    private EventManager eventManager;
    private ParticipantManager participantManager;
    private final UI ui;
    private final Scanner scanner;

    public Parser(EventManager eventManager, ParticipantManager participantManager, UI ui) {
        this.eventManager = eventManager;
        this.participantManager = participantManager;
        this.ui = ui;
        this.scanner = new Scanner(System.in);
        logger.info("Parser initialized with default scanner.");
    }

    public Parser(EventManager eventManager, ParticipantManager participantManager, UI ui, Scanner scanner) {
        this.eventManager = eventManager;
        this.participantManager = participantManager;
        this.ui = ui;
        this.scanner = scanner;
        logger.info("Parser initialized with custom scanner.");
    }

    public CommandFactory parse(String input) throws SyncException {
        logger.info("Parsing command: " + input);

        String[] parts = input.trim().toLowerCase().split(" ", 2); // Split input

        if (parts.length > 0) {
            String commandWord = parts[0];
            switch (commandWord.toLowerCase()) {
            case "bye":
                logger.info("Bye command received.");
                return new ByeCommandFactory(this.participantManager, this.ui);
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
                return new EditCommandFactory();
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
                return new AddParticipantCommandFactory(this.participantManager, this.ui);
            case "listparticipants":
                logger.info("ListParticipants command received.");
                return new ListParticipantsCommandFactory(this.ui);
            case "filter":
                logger.info("Filter command received.");
                return new FilterCommandFactory();
            case "login":
                logger.info("Login command received.");
                return new LoginCommandFactory();
            case "logout":
                logger.info("Logout command received.");
                return new LogOutCommandFactory();
            case "create":
                logger.info("Create command received.");
                return new CreateUserCommandFactory();
            default:
                logger.warning("Invalid command received: " + input);
                throw new SyncException(SyncException.invalidCommandErrorMessage(input));
            }
        } else {
            logger.warning("Empty input received: " + input);
            throw new SyncException("Please provide a command");
        }
    }
}
