//package commandfactory;
//
//import command.Command;
//import command.CreateUserCommand;
//import exception.SyncException;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import participant.AvailabilitySlot;
//import participant.Participant;
//
//import java.io.ByteArrayInputStream;
//import java.io.InputStream;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class CreateUserCommandFactoryTest {
//
//    private CreateUserCommandFactory factory;
//
//    @BeforeEach
//    void setUp() {
//        factory = new CreateUserCommandFactory();
//    }
//
//    @Test
//    void testCreateCommandWithValidInput() throws SyncException {
//        String input = "TestUser\npassword123\n1\n1\n2025-04-03 10:00\n2025-04-03 11:00\n";
//        InputStream in = new ByteArrayInputStream(input.getBytes());
//        System.setIn(in);
//        Command command = factory.createCommand();
//
//        assertNotNull(command);
//        assertTrue(command instanceof CreateUserCommand);
//
//        CreateUserCommand createUserCommand = (CreateUserCommand) command;
//        Participant participant = createUserCommand.getParticipant();
//        assertEquals("TestUser", participant.getName());
//        assertEquals("password123", participant.getPassword());
//        assertEquals(Participant.AccessLevel.MEMBER, participant.getAccessLevel());
//
//        // 验证是否正确创建了1个时段
//        List<AvailabilitySlot> availabilitySlots = participant.getAvailableTimes();
//        assertEquals(1, availabilitySlots.size());
//
//        AvailabilitySlot slot = availabilitySlots.get(0);
//        assertEquals(LocalDateTime.of(2025, 4, 3, 10, 0), slot.getStartTime());
//        assertEquals(LocalDateTime.of(2025, 4, 3, 11, 0), slot.getEndTime());
//    }
//
//    @Test
//    void testCreateCommandWithInvalidTimeFormat() throws SyncException {
//        String input = "TestUser\npassword123\n 1 \n 1 \n2025-04-03 10:00\ninvalid_time\n\n\n";
//        InputStream in = new ByteArrayInputStream(input.getBytes());
//        System.setIn(in);
//        factory.createCommand();
//        assertThrows(SyncException.class, () -> {
//            new CreateUserCommandFactory().createCommand();
//        });
//    }
//
//    @Test
//    void testCreateCommandWithNegativeAvailabilitySlots() {
//        String input = "TestUser\npassword123\n-1\n";
//        InputStream in = new ByteArrayInputStream(input.getBytes());
//        System.setIn(in);
//
//        assertThrows(SyncException.class, () -> {
//            factory.createCommand();
//        });
//    }
//}
