package parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import exception.SyncException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import participant.Participant;
import ui.UI;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.util.Scanner;

public class CommandParserTest {
    private UI ui;
    private CommandParser parser;

    @BeforeEach
    public void setUp() throws SyncException {
        ui = new UI();
        parser = new CommandParser();
        parser.setUi(ui);
    }

    @Test
    public void testParseDateTimeValidFormat() throws SyncException {
        String validDate = "2025-05-10 14:00";
        LocalDateTime result = CommandParser.parseDateTime(validDate);

        assertEquals(LocalDateTime.of(2025, 5, 10, 14, 0), result);
    }

    @Test
    public void testParseDateTimeInvalidFormat() {
        String invalidDate = "2025-05-10 14:00:00";
        assertThrows(SyncException.class, () -> CommandParser.parseDateTime(invalidDate));
    }

    @Test
    public void testSplitAddCommandInputValid() throws SyncException {
        String input = "Meeting | 2025-05-10 14:00 | 2025-05-10 15:00 | Room 101 | Discuss project";
        String[] result = CommandParser.splitAddCommandInput(input);

        assertEquals(5, result.length);
    }

    @Test
    public void testSplitAddCommandInputInvalid() {
        String input = "Meeting | 2025-05-10 14:00 | Room 101 | Discuss project"; // 少于5个部分
        assertThrows(SyncException.class, () -> CommandParser.splitAddCommandInput(input));
    }

    @Test
    public void testAskAccessLevelValidAdminInput() throws SyncException {
        String simulatedInput = "1\n";
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));
        ui.setScanner(new Scanner(System.in));

        Participant.AccessLevel level = parser.askAccessLevel();

        assertEquals(Participant.AccessLevel.ADMIN, level);
    }

    @Test
    public void testAskAccessLevelValidMemberInput() throws SyncException {
        String simulatedInput = "2\n";
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));
        ui.setScanner(new Scanner(System.in));

        Participant.AccessLevel level = parser.askAccessLevel();

        assertEquals(Participant.AccessLevel.MEMBER, level);
    }

    @Test
    public void testAskAccessLevelInvalidInput() throws SyncException {
        String simulatedInput = "3\n";
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));
        ui.setScanner(new Scanner(System.in));

        Participant.AccessLevel level = parser.askAccessLevel();

        assertEquals(Participant.AccessLevel.MEMBER, level);
    }

    @Test
    public void testAskAccessLevelInvalidFormat() {
        String simulatedInput = "no\n";
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));
        ui.setScanner(new Scanner(System.in));

        assertThrows(SyncException.class, () -> parser.askAccessLevel());
    }
}