package command;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import event.Event;
import event.EventManager;
import exception.SyncException;
import participant.Participant;
import participant.ParticipantManager;
import ui.UI;

public class EditEventCommand extends Command {
    private final int index;
    private final Scanner scanner;

    public EditEventCommand(int index) {
        this.scanner = new Scanner(System.in);
        this.index = index;
    }

    public void execute(EventManager events, UI ui, ParticipantManager participantManager) throws SyncException {
        if (!participantManager.isCurrentUserAdmin()) {
            throw new SyncException("You are not currently administrator.");
        }

        Event event = events.getEvent(index);
        boolean editing = true;

        while (editing) {
            ui.showEditCommandMessage(event);

            if (!scanner.hasNextInt()) {
                ui.showEditCommandCorrectFormat();
                scanner.next(); // Clear invalid input
                continue;
            }

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    ui.showEditCommandStep1();
                    String newName = scanner.nextLine().trim();
                    if (newName.equalsIgnoreCase("exit")) {
                        editing = false;
                        break;
                    }
                    event.setName(newName);
                    break;

                case 2:
                    ui.showEditCommandStep2();
                    try {
                        LocalDateTime newStart = getValidDateTime(ui, "start");
                        if (newStart == null) break;

                        if (newStart.isAfter(event.getEndTime())) {
                            throw new SyncException(SyncException.startTimeAfterEndTimeMessage());
                        }

                        if (!checkParticipantAvailability(event, newStart, event.getEndTime(), ui)) {
                            throw new SyncException(SyncException.participantConflictMessage());
                        }

                        event.setStartTime(newStart);
                    } catch (SyncException e) {
                        ui.showMessage(e.getMessage());
                        break;
                    }
                    break;

                case 3:
                    ui.showEditCommandStep3();
                    try {
                        LocalDateTime newEnd = getValidDateTime(ui, "end");
                        if (newEnd == null) break;

                        if (newEnd.isBefore(event.getStartTime())) {
                            throw new SyncException(SyncException.endTimeBeforeStartTimeMessage());
                        }

                        if (!checkParticipantAvailability(event, event.getStartTime(), newEnd, ui)) {
                            throw new SyncException(SyncException.participantConflictMessage());
                        }

                        event.setEndTime(newEnd);
                    } catch (SyncException e) {
                        ui.showMessage(e.getMessage());
                        break;
                    }
                    break;

                case 4:
                    ui.showEditCommandStep4();
                    String newLocation = scanner.nextLine().trim();
                    if (newLocation.equalsIgnoreCase("exit")) {
                        editing = false;
                        break;
                    }
                    event.setLocation(newLocation);
                    break;

                case 5:
                    ui.showEditCommandStep5();
                    String newDesc = scanner.nextLine().trim();
                    if (newDesc.equalsIgnoreCase("exit")) {
                        editing = false;
                        break;
                    }
                    event.setDescription(newDesc);
                    break;

                case 6:
                    editing = false;
                    System.out.println("✅ Event editing completed.");
                    break;

                default:
                    ui.showEditCommandCorrectFormat();
            }

            events.updateEvent(index, event);
        }
    }

    private LocalDateTime getValidDateTime(UI ui, String type) {
        while (true) {
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("exit")) {
                return null;
            }

            try {
                return LocalDateTime.parse(input, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            } catch (Exception e) {
                ui.showMessage(SyncException.invalidDateTimeFormatMessage(type));
                System.out.print("Enter " + type + " time again or type 'exit': ");
            }
        }
    }

    private boolean checkParticipantAvailability(Event event, LocalDateTime newStart,
                                                 LocalDateTime newEnd, UI ui) {
        for (Participant p : event.getParticipants()) {
            if (!p.isAvailableDuring(newStart, newEnd)) {
                ui.showMessage(SyncException.participantUnavailableDuringEditError(p.getName(), newStart, newEnd));
                return false;
            }
        }
        return true;
    }
}
