//package commandfactory;
//
//import command.Command;
//import command.ListCommand;
//import event.EventManager;
//import exception.SyncException;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import participant.AvailabilitySlot;
//import participant.Participant;
//import participant.ParticipantManager;
//import storage.Storage;
//import storage.UserStorage;
//import ui.UI;
//
//import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
//import java.io.PrintStream;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class ListCommandFactoryTest {
//
//    private ListCommandFactory listCommandFactory;
//    private EventManager eventManager;
//    private UI ui;
//    private ParticipantManager participantManager;
//    private Participant participant;
//    private ByteArrayOutputStream outputStream;
//    private PrintStream originalOut;
//
//    @BeforeEach
//    void setUp() throws SyncException {
//        ui = new UI();
//        participant = new Participant("john_doe", "password123", Participant.AccessLevel.MEMBER, new ArrayList<>());
//
//        UserStorage userStorage = new UserStorage("./data/test-users.txt");
//
//        Storage eventStorage = new Storage("./data/commandTest/AddEventCommandTest.txt", userStorage);
//        ui = new UI();
//        eventManager = new EventManager(new ArrayList<>(), ui, eventStorage, userStorage);
//
//        participantManager = new ParticipantManager(new ArrayList<>(), ui, userStorage);
//
//        eventManager = new EventManager(new ArrayList<>(), ui, eventStorage, userStorage);
//        List<AvailabilitySlot> availableTimes = new ArrayList<>();
//        availableTimes.add(new AvailabilitySlot(LocalDateTime.of(2020, 5, 10, 14, 0),
//                LocalDateTime.of(2020, 5, 10, 16, 0)));
//        participant = new Participant("john_doe", "password123", Participant.AccessLevel.ADMIN, availableTimes);
//        ArrayList<Participant> participants = new ArrayList<>();
//        participants.add(participant);
//        participantManager = new ParticipantManager(participants, ui, null);
//        participantManager.setCurrentUser(participant);
//        listCommandFactory = new ListCommandFactory(participantManager, ui);
//        outputStream = new ByteArrayOutputStream();
//        originalOut = System.out;
//        System.setOut(new PrintStream(outputStream));
//    }
//
//    @Test
//    void testCreateCommandWithValidInput() throws SyncException {
//        // 正确模拟输入，确保输入流中包含换行符
//        String simulatedInput = "end\n";
//        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes())); // 设置输入流
//
//        Command command = listCommandFactory.createCommand();
//
//        assertNotNull(command, "Command should not be null");
//        assertTrue(command instanceof ListCommand, "Command should be an instance of ListCommand");
//    }
//
//    @Test
//    void testCreateCommandWithInvalidInput() {
//        String simulatedInput = "invalid\n"; // 无效输入
//        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes())); // 设置输入流
//
//        // 期望 SyncException 被抛出
//        SyncException exception = assertThrows(SyncException.class, () -> {
//            listCommandFactory.createCommand();
//        });
//
//        assertEquals("Invalid sort type", exception.getMessage());
//    }
//
//    @Test
//    void testCreateCommandWithEmptyInput() {
//        String simulatedInput = "\n"; // 空输入，模拟换行符
//        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes())); // 设置输入流
//
//        // 期望 SyncException 被抛出
//        SyncException exception = assertThrows(SyncException.class, () -> {
//            listCommandFactory.createCommand();
//        });
//
//        assertEquals("Invalid sort type", exception.getMessage(), "Exception message should be 'Invalid sort type'");
//    }
//}
