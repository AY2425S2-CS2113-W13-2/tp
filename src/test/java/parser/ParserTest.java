package parser;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import exception.SyncException;
import logger.EventSyncLogger;
import storage.Storage;
import storage.UserStorage;
import ui.UI;
import participant.ParticipantManager;
import event.EventManager;
import commandfactory.CommandFactory;

import java.util.ArrayList;
import java.util.Scanner;

public class ParserTest {
    private Parser parser;
    private UI ui;
    private EventManager eventManager;
    private ParticipantManager participantManager;
    private Scanner scanner;

    @BeforeAll
    static void setupLogger() {
        // Initialize the logger before running any tests
        EventSyncLogger.setupLogger();
    }

    @BeforeEach
    public void setUp() throws SyncException {
        ui = new UI();
        UserStorage userStorage = new UserStorage("./data/test-users.txt");
        Storage eventStorage = new Storage("./data/commandTest/AddEventCommandTest.txt", userStorage);
        participantManager = new ParticipantManager(new ArrayList<>(), ui, userStorage);
        eventManager = new EventManager(new ArrayList<>(), ui, eventStorage, userStorage);
        parser = new Parser(eventManager, participantManager, ui, scanner);
    }

    @Test
    public void testParseByeCommand() throws SyncException {
        String input = "bye";
        CommandFactory commandFactory = parser.parse(input);
        assertNotNull(commandFactory, "CommandFactory should not be null");
    }

    @Test
    public void testParseListAllCommand() throws SyncException {
        String input = "listall";
        CommandFactory commandFactory = parser.parse(input);
        assertNotNull(commandFactory, "CommandFactory should not be null");
    }

    @Test
    public void testParseListCommand() throws SyncException {
        String input = "list";
        CommandFactory commandFactory = parser.parse(input);
        assertNotNull(commandFactory, "CommandFactory should not be null");
    }

    @Test
    public void testParseAddCommand() throws SyncException {
        String input = "add event_name | 2024-05-01 12:00 | 2024-05-01 14:00 | description | details";
        CommandFactory commandFactory = parser.parse(input);
        assertNotNull(commandFactory, "CommandFactory should not be null");
    }

    @Test
    public void testParseDeleteCommand() throws SyncException {
        String input = "delete event_id";
        CommandFactory commandFactory = parser.parse(input);
        assertNotNull(commandFactory, "CommandFactory should not be null");
    }

    @Test
    public void testParseDuplicateCommand() throws SyncException {
        String input = "duplicate event_id";
        CommandFactory commandFactory = parser.parse(input);
        assertNotNull(commandFactory, "CommandFactory should not be null");
    }

    @Test
    public void testParseEditCommand() throws SyncException {
        String input = "edit event_id | new_description";
        CommandFactory commandFactory = parser.parse(input);
        assertNotNull(commandFactory, "CommandFactory should not be null");
    }

    @Test
    public void testParseFindCommandWithKeyword() throws SyncException {
        String input = "find event";
        CommandFactory commandFactory = parser.parse(input);
        assertNotNull(commandFactory, "CommandFactory should not be null");
    }

    @Test
    public void testParseFindCommandWithoutKeyword() {
        String input = "find";
        assertThrows(SyncException.class, () -> parser.parse(input), "Find command without keyword should throw SyncException");
    }

    @Test
    public void testParseAddParticipantCommand() throws SyncException {
        String input = "addparticipant participant_name | event_id";
        CommandFactory commandFactory = parser.parse(input);
        assertNotNull(commandFactory, "CommandFactory should not be null");
    }

    @Test
    public void testParseListParticipantsCommand() throws SyncException {
        String input = "listparticipants";
        CommandFactory commandFactory = parser.parse(input);
        assertNotNull(commandFactory, "CommandFactory should not be null");
    }

    @Test
    public void testParseFilterCommand() throws SyncException {
        String input = "filter event_type";
        CommandFactory commandFactory = parser.parse(input);
        assertNotNull(commandFactory, "CommandFactory should not be null");
    }

    @Test
    public void testParseLoginCommand() throws SyncException {
        String input = "login username password";
        CommandFactory commandFactory = parser.parse(input);
        assertNotNull(commandFactory, "CommandFactory should not be null");
    }

    @Test
    public void testParseLogoutCommand() throws SyncException {
        String input = "logout";
        CommandFactory commandFactory = parser.parse(input);
        assertNotNull(commandFactory, "CommandFactory should not be null");
    }

    @Test
    public void testParseCreateCommand() throws SyncException {
        String input = "create username password";
        CommandFactory commandFactory = parser.parse(input);
        assertNotNull(commandFactory, "CommandFactory should not be null");
    }

    @Test
    public void testParseHelpCommand() throws SyncException {
        String input = "help";
        CommandFactory commandFactory = parser.parse(input);
        assertNotNull(commandFactory, "CommandFactory should not be null");
    }

    @Test
    public void testParseInvalidCommand() {
        String input = "invalidCommand";
        assertThrows(SyncException.class, () -> parser.parse(input), "Invalid command should throw SyncException");
    }

    @Test
    public void testParseEmptyCommand() {
        String input = "";
        assertThrows(SyncException.class, () -> parser.parse(input), "Empty input should throw SyncException");
    }
}
