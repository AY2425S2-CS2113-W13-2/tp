package commandFactory;

import command.Command;
import command.CreateUserCommand;
import parser.CommandParser;
import participant.Participant;
import participant.ParticipantManager;
import participant.AvailabilitySlot;
import ui.UI;
import exception.SyncException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Scanner;

public class CreateUserCommandFactory implements CommandFactory {

    public Command createCommand() throws SyncException {
        String participantName = CommandParser.askParticipantName();
        String password = CommandParser.askPassword();
        Participant.AccessLevel accessLevel = CommandParser.askAccessLevel();
        ArrayList<AvailabilitySlot> availabilitySlots = askAvailability();

        Participant participant = new Participant(participantName, password, accessLevel);
        participant.getAvailableTimes().addAll(availabilitySlots);
        return new CreateUserCommand(participant);
    }


    private ArrayList<AvailabilitySlot> askAvailability() throws SyncException {
        ArrayList<AvailabilitySlot> slots = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter number of availability slots: ");
        int numSlots = Integer.parseInt(scanner.nextLine().trim());

        for (int i = 0; i < numSlots; i++) {
            System.out.print("Enter start time for availability slot " + (i + 1) + " (in format YYYY-MM-DD HH:mm): ");
            String startTime = scanner.nextLine().trim();

            System.out.print("Enter end time for availability slot " + (i + 1) + " (in format YYYY-MM-DD HH:mm): ");
            String endTime = scanner.nextLine().trim();

            LocalDateTime start = CommandParser.parseDateTime(startTime);
            LocalDateTime end = CommandParser.parseDateTime(endTime);

            if (start == null || end == null) {
                System.out.println("❌ Invalid date format, skipping this slot.");
                continue;
            }

            AvailabilitySlot availabilitySlot = new AvailabilitySlot(start, end);
            slots.add(availabilitySlot);
        }

        return slots;
    }
}
