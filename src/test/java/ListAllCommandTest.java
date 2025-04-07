import static org.junit.jupiter.api.Assertions.assertTrue;

import command.ListAllCommand;
import exception.SyncException;
import event.Event;
import event.EventManager;
import participant.Participant;
import participant.ParticipantManager;
import storage.Storage;
import storage.UserStorage;
import ui.UI;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

class ListAllCommandTest {
    private EventManager eventManager;
    private ParticipantManager participantManager;
    private UI ui;
    private Participant adminUser;
    private Participant memberUser;

    @BeforeEach
    void setUp() throws SyncException {
        ui = new ui.UI();
        UserStorage userStorage = new UserStorage("./data/UserListAllTest.txt");
        Storage storage = new Storage("./data/ListAllTest.txt", userStorage);
        ArrayList<Participant> participants = new ArrayList<>();
        participantManager = new ParticipantManager(participants, ui, userStorage);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");

        Event event1 = new Event("Team Meeting",
                LocalDateTime.parse("2025/05/10 14:00", formatter),
                LocalDateTime.parse("2025/05/10 16:00", formatter),
                "Conference Room",
                "Weekly Scheduled Team Meeting");
        Event event2 = new Event("Concert",
                LocalDateTime.parse("2025/05/11 19:00", formatter),
                LocalDateTime.parse("2025/05/11 22:00", formatter),
                "Hall",
                "Watch a Concert");

        eventManager = new EventManager(new ArrayList<>(), ui, storage, userStorage);
        eventManager.addEvent(event1);
        eventManager.addEvent(event2);

        adminUser = new Participant("Alice", "1234", Participant.AccessLevel.ADMIN);
        memberUser = new Participant("Bob", "5678", Participant.AccessLevel.MEMBER);
        participants.add(adminUser);
        participants.add(memberUser);
        event1.addParticipant(adminUser);
        event2.addParticipant(memberUser);
    }

    // Test ListAllCommand for Admin User
    @Test
    void testListAllCommandForAdmin_outputsTwoEvents() throws SyncException {
        participantManager.setCurrentUser(adminUser);
        ListAllCommand command = new ListAllCommand("priority", ui);

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        command.execute(eventManager, ui, participantManager);

        System.setOut(originalOut);
        String output = outContent.toString();

        assertTrue(output.contains("Team Meeting"));
        assertTrue(output.contains("Concert"));
    }

    // Test that Memeber cannot access ListAllCommand
    @Test
    void testListAllCommandForMember_showsAdminWarning() throws SyncException {
        participantManager.setCurrentUser(memberUser);
        ListAllCommand command = new ListAllCommand("priority", ui);

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        command.execute(eventManager, ui, participantManager);

        System.setOut(originalOut);
        String output = outContent.toString();

        assertTrue(output.contains("Sorry, you need to be an ADMIN to access all events."));
    }
}
