package parser;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

import exception.SyncException;
import participant.AvailabilitySlot;
import participant.Participant;

public final class CommandParser {
    private static final Scanner scanner = new Scanner(System.in);

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

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

    public static Participant.AccessLevel askAccessLevel() throws SyncException {
        System.out.print("Enter participant's access level (1 for Admin, 2 for Member): ");
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice == 1) {
                return Participant.AccessLevel.ADMIN;
            } else if (choice == 2) {
                return Participant.AccessLevel.MEMBER;
            } else {
                System.out.println("Invalid choice. Defaulting to MEMBER.");
                return Participant.AccessLevel.MEMBER;
            }
        } catch (NumberFormatException e) {
            throw new SyncException("Please enter only 1 or 2");
        }
    }
}
