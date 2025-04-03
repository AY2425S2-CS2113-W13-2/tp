package commandfactory;

import command.Command;
import command.CreateUserCommand;
import parser.CommandParser;
import participant.Participant;
import participant.AvailabilitySlot;
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
        if (availabilitySlots.isEmpty()) {
            throw new SyncException("❌ No valid availability slots provided. Cannot create participant.");
        }

        Participant participant = new Participant(participantName, password, accessLevel);
        participant.setAvailableTimes(availabilitySlots);
        return new CreateUserCommand(participant);
    }

    private ArrayList<AvailabilitySlot> askAvailability() throws SyncException {
        ArrayList<AvailabilitySlot> slots = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);
        int numSlots;
        System.out.print("Enter number of availability slots (maximum 10): ");
        try {
            numSlots = Integer.parseInt(scanner.nextLine().trim());
            if (numSlots <= 0) {
                throw new SyncException("❌ Number of availability slots must be at least 1.");
            }
            numSlots = Math.min(numSlots, 10);
        } catch (NumberFormatException e) {
            throw new SyncException("❌ Invalid input. Please enter a valid number between 1 and 10.");
        }


        for (int i = 0; i < numSlots; i++) {
            try {
                System.out.print("Enter start time for availability slot " + (i + 1) +
                        " (in format yyyy-MM-dd HH:mm): ");
                String startTimeStr = scanner.nextLine().trim();

                System.out.print("Enter end time for availability slot " + (i + 1) +
                        " (in format yyyy-MM-dd HH:mm): ");
                String endTimeStr = scanner.nextLine().trim();

                LocalDateTime start = CommandParser.parseDateTime(startTimeStr);
                LocalDateTime end = CommandParser.parseDateTime(endTimeStr);

                if (end.isBefore(start)) {
                    System.out.println("❌ End time must be after start time. Skipping this slot.");
                    continue;
                }
                slots.add(new AvailabilitySlot(start, end));
            } catch (SyncException e) {
                System.out.println("❌ " + e.getMessage() + " Skipping this slot.");
            } catch (Exception e) {
                System.out.println("❌ Unexpected error: " + e.getMessage());
            }
        }

        return slots;
    }
}
