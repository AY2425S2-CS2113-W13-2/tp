package command;

import event.Event;
import event.EventManager;
import participant.AvailabilitySlot;
import participant.ParticipantManager;
import participant.Participant;
import storage.Storage;
import storage.UserStorage;
import ui.UI;
import exception.SyncException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AddEventCommandTest {
    private EventManager eventManager;
    private UI ui;
    private ParticipantManager participantManager;
    private Event event;
    private AddEventCommand command;
    private Storage eventStorage;

    @BeforeEach
    void setUp() throws SyncException {
        ui = new UI();
        UserStorage userStorage = new UserStorage("./data/test-users.txt");
        Storage eventStorage = new Storage("./data/commandTest/AddEventCommandTest.txt", userStorage);
        participantManager = new ParticipantManager(new ArrayList<>(), ui, userStorage);

        List<AvailabilitySlot> availableTimes = new ArrayList<>();
        availableTimes.add(new AvailabilitySlot(LocalDateTime.of(2020, 5, 10, 14, 0),
                LocalDateTime.of(2020, 5, 10, 16, 0)));

        Participant testUser = new Participant("john_doe", "password123", Participant.AccessLevel.ADMIN, availableTimes);
        participantManager.setCurrentUser(testUser);

        eventManager = new EventManager(new ArrayList<>(), ui, eventStorage, userStorage);

        event = new Event("Test Event",
                LocalDateTime.of(2020, 5, 10, 14, 0),
                LocalDateTime.of(2020, 5, 10, 16, 0),
                "Lab", "Test Desc");

        command = new AddEventCommand(event);
    }


    @Test
    void testExecute() throws SyncException {
        command.execute(eventManager, ui, participantManager);
        assertEquals(1, eventManager.size());
    }

    @Test
    void testGetEvent() {
        assertEquals(event, command.getEvent());
    }

    @Test
    void testExecuteThrowsSyncException() {
        Event invalidEvent = new Event("Invalid Event",
                LocalDateTime.of(2025, 5, 10, 16, 0),
                LocalDateTime.of(2025, 5, 10, 14, 0),  // 结束时间 < 开始时间
                "Lab", "Invalid Desc");

        AddEventCommand invalidCommand = new AddEventCommand(invalidEvent);

        assertThrows(SyncException.class, () -> invalidCommand.execute(eventManager, ui, participantManager));
    }
}
