package parser;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

import exception.SyncException;
import participant.AvailabilitySlot;
import participant.Participant;
import sort.SortByPriority;
import sort.SortByStartTime;

public final class CommandParser {
    private static final Scanner scanner = new Scanner(System.in);

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static String readInput() throws SyncException {
        String input = scanner.nextLine();
        return input;
    }
    public static LocalDateTime parseDateTime(String dateStr) throws SyncException {
        try {
            return LocalDateTime.parse(dateStr.trim(), DATE_FORMATTER);
        } catch (DateTimeException e) {
            throw new SyncException("Invalid date-time format. Use yyyy-MM-dd HH:mm");
        }
    }

    public static String[] splitAddCommandInput(String input) throws SyncException {
        String[] parts = input.split("\\|");
        if (parts.length != 5) {
            throw new SyncException(SyncException.invalidEventDetailsErrorMessage());
        }
        return parts;
    }

    public static String[] splitAddParticipantCommandInput() throws SyncException {
        System.out.println("Use: <EventIndex> | <Participant Name>");
        String input = scanner.nextLine();
        String[] parts = input.split("\\|");
        if (parts.length != 2) {
            throw new SyncException("Invalid format. Use: <EventIndex> | <Participant Name>");
        }
        return parts;
    }

    public static ArrayList<AvailabilitySlot> parseAvailabilitySlots(String input) throws SyncException {
        ArrayList<AvailabilitySlot> slots = new ArrayList<>();
        String[] timeSlots = input.split(",");

        for (String slot : timeSlots) {
            String trimmedSlot = slot.trim();
            if (!trimmedSlot.isEmpty()) {
                String[] startEnd = trimmedSlot.split("-");
                if (startEnd.length == 2) {
                    try {
                        LocalDateTime start = parseDateTime(startEnd[0].trim());
                        LocalDateTime end = parseDateTime(startEnd[1].trim());
                        slots.add(new AvailabilitySlot(start, end));
                    } catch (DateTimeException e) {
                        throw new SyncException("Invalid time format. Use yyyy/MM/dd HH:mm - yyyy/MM/dd HH:mm");
                    }
                } else {
                    throw new SyncException("Availability slot must contain start and end time separated by '-'");
                }
            }
        }

        if (slots.isEmpty()) {
            throw new SyncException("At least one availability slot is required");
        }
        return slots;
    }

    public static String readDeleteName() {
        System.out.print("Enter name to search for events to delete: ");
        return scanner.nextLine().trim();
    }

    public static String readFilterInput() {
        System.out.print("Enter priority range (e.g., LOW MEDIUM): ");
        return scanner.nextLine();
    }

    public static String askParticipantName() {
        System.out.print("Enter participant's name: ");
        return scanner.nextLine().trim();
    }

    public static Participant.AccessLevel askAccessLevel() throws SyncException {
        System.out.print("Enter participant's access level (1 for Admin, 2 for Member): ");
        int choice = Integer.parseInt(scanner.nextLine().trim());
        try {
            if (choice == 1) {
                return Participant.AccessLevel.ADMIN;
            } else if (choice == 2) {
                return Participant.AccessLevel.MEMBER;
            } else {
                System.out.println("Invalid choice. Defaulting to PARTICIPANT.");
                return Participant.AccessLevel.MEMBER;
            }
        } catch (NumberFormatException e) {
            throw new SyncException("Please enter only 1 or 2");
        }
    }

    public static String askPassword() {
        System.out.print("Enter participant's password: ");
        return scanner.nextLine();
    }

    public static String readListCommandInput() {
        System.out.print("Enter your sort type: ");
        System.out.print("Now we have a list of available sort types: priority, start, end ");
        return scanner.nextLine();
    }

    public static String readAddCommandInput() {
        System.out.print("Enter event details (format: Event Name | Start Date | End Date | Location | Description): \n");
        return scanner.nextLine();
    }

    public static String readAddParticipantInput() {
        System.out.print("Follow this format: <EventIndex> | <Participant Name> | <AccessLevel> | <Availability> \n");
        return scanner.nextLine();
    }
}
