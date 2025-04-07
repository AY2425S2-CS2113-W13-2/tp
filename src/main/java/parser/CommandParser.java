package parser;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

import exception.SyncException;
import participant.AvailabilitySlot;
import participant.Participant;
import ui.UI;

public final class CommandParser {
    private static UI ui = new UI();

    public void setUi(UI ui) {
        this.ui = ui;
    }

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static LocalDateTime parseDateTime(String dateStr) throws SyncException {
        try {
            return LocalDateTime.parse(dateStr.trim(), DATE_FORMATTER);
        } catch (DateTimeException e) {
            throw new SyncException("Invalid date-time format. Use yyyy-MM-dd HH:mm. " +
                    "Enter any command word to continue.");
        }
    }

    public static String[] splitAddCommandInput(String input) throws SyncException {
        String[] parts = input.split("\\|");
        if (parts.length != 5) {
            throw new SyncException(SyncException.invalidEventDetailsErrorMessage());
        }
        return parts;
    }

    public static Participant.AccessLevel askAccessLevel() throws SyncException {
        ui.showMessage("Enter participant's access level (1 for Admin, 2 for Member) (or type 'exit' to cancel): ");
        String input = ui.readLine().trim();
        ui.checkForExit(input);
        try {
            int choice = Integer.parseInt(input);
            if (choice == 1) {
                return Participant.AccessLevel.ADMIN;
            } else if (choice == 2) {
                return Participant.AccessLevel.MEMBER;
            } else {
                ui.showMessage("Invalid choice. Defaulting to MEMBER.");
                return Participant.AccessLevel.MEMBER;
            }
        } catch (NumberFormatException e) {
            throw new SyncException("You can only enter 1 or 2. Enter 'create' to try again.");
        }
    }
}
