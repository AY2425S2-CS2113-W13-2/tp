package commandfactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import exception.SyncException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import logger.EventSyncLogger;
import participant.AvailabilitySlot;
import participant.Participant;
import participant.ParticipantManager;
import storage.UserStorage;
import ui.UI;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

class ListAllCommandFactoryTest {

    private UI ui;
    private ParticipantManager participantManager;
    private ListAllCommandFactory listAllCommandFactory;
    private Participant participant;

    @BeforeAll
    static void setupLogger() {
        // Initialize the logger before running any tests
        EventSyncLogger.setupLogger();
    }

    @BeforeEach
    void setUp() throws SyncException {
        ui = new UI();

        List<AvailabilitySlot> availableTimes = new ArrayList<>();
        availableTimes.add(new AvailabilitySlot(LocalDateTime.of(2020, 5, 10, 14, 0),
                LocalDateTime.of(2020, 5, 10, 16, 0)));

        participant = new Participant("admin_user", "admin123", Participant.AccessLevel.ADMIN, availableTimes);
        ArrayList<Participant> participants = new ArrayList<>();
        participants.add(participant);

        UserStorage userStorage = new UserStorage("./data/test-users.txt");
        participantManager = new ParticipantManager(participants, ui, userStorage);
        participantManager.setCurrentUser(participant);

        listAllCommandFactory = new ListAllCommandFactory(participantManager, ui);
    }

    @Test
    void testCreateCommandWithoutLogin() {
        participantManager.setCurrentUser(null); // 模拟未登录

        SyncException exception = assertThrows(SyncException.class, () -> {
            listAllCommandFactory.createCommand();
        });

        assertEquals("You are not logged in. Please enter 'login' to login.", exception.getMessage());
    }

    @Test
    void testCreateCommandWithNonAdminUser() {
        Participant normalUser = new Participant("normal", "pw", Participant.AccessLevel.MEMBER, new ArrayList<>());
        participantManager.setCurrentUser(normalUser); // 模拟非 admin 用户

        SyncException exception = assertThrows(SyncException.class, () -> {
            listAllCommandFactory.createCommand();
        });

        assertEquals("Sorry, you need to be an ADMIN to access all events.", exception.getMessage());
    }
}
