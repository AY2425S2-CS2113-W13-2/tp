//package command;
//
//import event.Event;
//import event.EventManager;
//import exception.SyncException;
//import participant.Participant;
//import participant.AvailabilitySlot;
//import participant.ParticipantManager;
//import storage.Storage;
//import storage.UserStorage;
//import ui.UI;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.io.ByteArrayOutputStream;
//import java.io.PrintStream;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class AddParticipantCommandTest {
//    private ListCommand listCommand;
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
//        listCommand = new ListCommand("priority");
//        outputStream = new ByteArrayOutputStream();
//        originalOut = System.out;
//        System.setOut(new PrintStream(outputStream));
//    }
//
//    @Test
//    void testExecuteWhenParticipantExists() throws SyncException {
//        participantManager.addNewUser(participant);
//
//        ArrayList<AvailabilitySlot> availabilitySlots = new ArrayList<>();
//        AvailabilitySlot availabilitySlot = new AvailabilitySlot(
//                testEvent.getStartTime().minusMinutes(30),
//                testEvent.getEndTime().plusMinutes(30)
//        );
//        availabilitySlots.add(availabilitySlot);
//        testParticipant.setAvailableTimes(availabilitySlots);
//
//        addParticipantCommand.execute(testEventManager, ui, testParticipantManager);
//
//        // Assert participant is added to the event
//        assertThrows(SyncException.class, () -> addParticipantCommand.execute(
//                testEventManager, ui, testParticipantManager));
//    }
//
//    @Test
//    void testExecuteWhenParticipantDoesNotExist() throws SyncException {
//        // 修改这个测试，使用自定义UI而不是反射
//        // 使用唯一名称
//        String uniqueName = "TestUser2";
//        testParticipant = new Participant(uniqueName, "password123", Participant.AccessLevel.MEMBER,
//        new ArrayList<>());
//        AddParticipantCommand newCommand = new AddParticipantCommand(0, uniqueName);
//
//        // 创建一个会返回"是"的UI
//        UI yesUI = new UI() {
//            @Override
//            public boolean askConfirmation(String message) {
//                return true;
//            }
//
//            @Override
//            public void showMessage(String message) {
//                // 什么都不做
//            }
//        };
//
//        // 设置参与者的可用时间
//        ArrayList<AvailabilitySlot> availabilitySlots = new ArrayList<>();
//        AvailabilitySlot availabilitySlot = new AvailabilitySlot(
//                testEvent.getStartTime().minusMinutes(30),
//                testEvent.getEndTime().plusMinutes(30)
//        );
//        availabilitySlots.add(availabilitySlot);
//        testParticipant.setAvailableTimes(availabilitySlots);
//
//        // 在执行命令前，我们先手动添加参与者以模拟CreateUserCommandFactory的行为
//        // 当执行命令时，我们希望参与者已经存在于参与者管理器中
//        testParticipantManager.addNewUser(testParticipant);
//
//        // 执行命令
//        newCommand.execute(testEventManager, yesUI, testParticipantManager);
//
//        // 验证参与者已添加到事件
//        assertTrue(testEvent.getParticipants().contains(testParticipant),
//                "Participant should be added to the event.");
//
//        // 尝试再次添加应该抛出异常
//        assertThrows(SyncException.class, () -> newCommand.execute(
//                testEventManager, yesUI, testParticipantManager));
//    }
//
//    @Test
//    void testExecuteWhenParticipantIsUnavailable() throws SyncException {
//        // 为这个测试创建一个新的UI
//        UI specificUI = new UI() {
//            @Override
//            public boolean askConfirmation(String message) {
//                return true; // 同意创建新参与者
//            }
//        };
//
//        // 使用唯一名称
//        String uniqueName = "TestUser3";
//        testParticipant = new Participant(uniqueName, "password123", Participant.AccessLevel.MEMBER,
//        new ArrayList<>());
//        AddParticipantCommand newCommand = new AddParticipantCommand(0, uniqueName);
//
//        // Add participant to the manager
//        testParticipantManager.addNewUser(testParticipant);
//
//        // 参与者在事件时间不可用
//        ArrayList<AvailabilitySlot> availabilitySlots = new ArrayList<>();
//        AvailabilitySlot availabilitySlot = new AvailabilitySlot(
//                testEvent.getStartTime().minusHours(3),
//                testEvent.getStartTime().minusHours(1)
//        );
//        availabilitySlots.add(availabilitySlot);
//        testParticipant.setAvailableTimes(availabilitySlots);
//
//        // Execute command
//        newCommand.execute(testEventManager, specificUI, testParticipantManager);
//
//        // Assert participant is not added to the event
//        assertFalse(testEvent.getParticipants().contains(testParticipant),
//                "Participant should not be added to the event when unavailable.");
//    }
//
//    @Test
//    void testExecuteWhenUserDeclinesToCreateNewParticipant() throws SyncException {
//        // 我们已经在setUp方法中设置了ui对象返回false
//        // 因此它会拒绝创建新参与者
//
//        // 确保使用干净的参与者名称
//        String uniqueName = "TestUser4";
//        AddParticipantCommand newCommand = new AddParticipantCommand(0, uniqueName);
//
//        // Execute command with our custom UI that returns false
//        newCommand.execute(testEventManager, ui, testParticipantManager);
//
//        // Assert no participant is added
//        assertNull(testParticipantManager.getParticipant(uniqueName),
//                "No participant should be created when user declines.");
//        assertEquals(0, testEvent.getParticipants().size(),
//                "No participant should be added to the event when user declines creation.");
//    }
//}
