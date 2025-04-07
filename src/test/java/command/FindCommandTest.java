package command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import event.Event;
import event.EventManager;
import exception.SyncException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import participant.Participant;
import participant.ParticipantManager;
import storage.Storage;
import storage.UserStorage;
import ui.UI;

import java.time.LocalDateTime;
import java.util.ArrayList;

class FindCommandTest {

    private UI ui;
    private EventManager eventManager;
    private ParticipantManager participantManager;
    private Participant admin;
    private Participant regularUser;
    private Event teamMeeting;
    private Event conference;
    private TestUI testUI;

    private static class TestUI extends UI {
        private ArrayList<Event> lastPrintedEvents;

        @Override
        public void printMatchingEvents(ArrayList<Event> events) {
            this.lastPrintedEvents = new ArrayList<>(events);
        }

        public ArrayList<Event> getLastPrintedEvents() {
            return lastPrintedEvents;
        }
    }

    @BeforeEach
    void setUp() throws SyncException {
        testUI = new TestUI();
        ui = testUI;

        UserStorage userStorage = new UserStorage("./data/test-users.txt");
        Storage eventStorage = new Storage("./data/test-events.txt", userStorage);

        admin = new Participant("admin", "pw", Participant.AccessLevel.ADMIN, new ArrayList<>());

        regularUser = new Participant("user", "pw", Participant.AccessLevel.MEMBER, new ArrayList<>());

        participantManager = new ParticipantManager(new ArrayList<>(), ui, userStorage);
        participantManager.addNewUser(admin);
        participantManager.addNewUser(regularUser);

        eventManager = new EventManager(new ArrayList<>(), ui, eventStorage, userStorage);

        teamMeeting = new Event(
                "Team Meeting",
                LocalDateTime.of(2025, 4, 10, 10, 0),
                LocalDateTime.of(2025, 4, 10, 12, 0),
                "Conference Room", "Weekly team sync-up discussion"
        );

        conference = new Event(
                "Annual Conference",
                LocalDateTime.of(2025, 4, 15, 9, 0),
                LocalDateTime.of(2025, 4, 15, 17, 0),
                "Convention Center", "Tech industry annual conference"
        );
        eventManager.addEvent(teamMeeting);
        eventManager.addEvent(conference);

        teamMeeting.addParticipant(regularUser);
    }

    @Test
    void testExecute_userNotLoggedIn() {
        participantManager.setCurrentUser(null);

        FindCommand findCommand = new FindCommand("Meeting");

        Exception exception = assertThrows(SyncException.class, () -> {
            findCommand.execute(eventManager, ui, participantManager);
        });

        assertTrue(exception.getMessage().contains("You are not logged in"));
    }

    @Test
    void testExecute_adminUserFindsEvents() throws SyncException {
        participantManager.setCurrentUser(admin);

        FindCommand findCommand = new FindCommand("Meeting");
        findCommand.execute(eventManager, ui, participantManager);

        ArrayList<Event> results = testUI.getLastPrintedEvents();
        assertEquals(1, results.size());
        assertEquals("Team Meeting", results.get(0).getName());
    }

    @Test
    void testExecute_regularUserFindsOwnEvents() throws SyncException {
        participantManager.setCurrentUser(regularUser);

        FindCommand findCommand = new FindCommand("Meeting");
        findCommand.execute(eventManager, ui, participantManager);
        ArrayList<Event> results = testUI.getLastPrintedEvents();
        assertEquals(1, results.size());
        assertEquals("Team Meeting", results.get(0).getName());
    }

    @Test
    void testExecute_findByDescription() throws SyncException {
        participantManager.setCurrentUser(admin);

        FindCommand findCommand = new FindCommand("sync");
        findCommand.execute(eventManager, ui, participantManager);

        ArrayList<Event> results = testUI.getLastPrintedEvents();
        assertEquals(1, results.size());
        assertEquals("Team Meeting", results.get(0).getName());
        assertTrue(results.get(0).getDescription().contains("sync"));
    }

    @Test
    void testExecute_findByIndustry() throws SyncException {
        participantManager.setCurrentUser(admin);

        FindCommand findCommand = new FindCommand("tech");
        findCommand.execute(eventManager, ui, participantManager);

        ArrayList<Event> results = testUI.getLastPrintedEvents();
        assertEquals(1, results.size());
        assertEquals("Annual Conference", results.get(0).getName());
        assertTrue(results.get(0).getDescription().contains("Tech"));
    }

    @Test
    void testExecute_noMatchingEvents() throws SyncException {
        participantManager.setCurrentUser(admin);

        FindCommand findCommand = new FindCommand("Nonexistent");
        findCommand.execute(eventManager, ui, participantManager);

        ArrayList<Event> results = testUI.getLastPrintedEvents();
        assertEquals(0, results.size());
    }

    @Test
    void testExecute_caseInsensitiveSearch() throws SyncException {
        participantManager.setCurrentUser(admin);

        FindCommand findCommand = new FindCommand("mEeTiNg");
        findCommand.execute(eventManager, ui, participantManager);

        ArrayList<Event> results = testUI.getLastPrintedEvents();
        assertEquals(1, results.size());
        assertEquals("Team Meeting", results.get(0).getName());
    }

    @Test
    void testExecute_multipleMatchingEvents() throws SyncException {
        participantManager.setCurrentUser(admin);

        Event anotherMeeting = new Event(
                "Department Meeting",
                LocalDateTime.of(2025, 4, 12, 14, 0),
                LocalDateTime.of(2025, 4, 12, 16, 0),
                "Meeting Room 2", "Department heads meeting"
        );
        eventManager.addEvent(anotherMeeting);

        FindCommand findCommand = new FindCommand("Meeting");
        findCommand.execute(eventManager, ui, participantManager);

        ArrayList<Event> results = testUI.getLastPrintedEvents();
        assertEquals(2, results.size());
        boolean foundTeamMeeting = false;
        boolean foundDepartmentMeeting = false;

        for (Event event : results) {
            if (event.getName().equals("Team Meeting")) {
                foundTeamMeeting = true;
            } else if (event.getName().equals("Department Meeting")) {
                foundDepartmentMeeting = true;
            }
        }

        assertTrue(foundTeamMeeting);
        assertTrue(foundDepartmentMeeting);
    }
}