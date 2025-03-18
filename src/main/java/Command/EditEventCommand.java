package command;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import event.Event;
import event.EventManager;
import exception.SyncException;
import ui.UI;

public class EditEventCommand extends Command {
    private final int index;
    private final Scanner scanner;

    public EditEventCommand(int index) {
        this.scanner = new Scanner(System.in);
        this.index = index;
    }


    public void execute(EventManager events, UI ui) {
        try {
            Event event = events.getEvent(index);
            boolean editing = true;

            while (editing) {
                ui.showEditCommandMessage(event);

                // Fix: Properly consume input
                if (!scanner.hasNextInt()) {
                    ui.showEditCommandCorrectFormat();
                    scanner.next(); // Clear invalid input
                    continue;
                }

                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline after nextInt()

                switch (choice) {
                case 1:
                    ui.showEditCommandStep1();
                    event.setName(scanner.nextLine().trim());
                    break;
                case 2:
                    ui.showEditCommandStep2();
                    event.setStartTime(validateDateTime(scanner.nextLine().trim()));
                    break;
                case 3:
                    ui.showEditCommandStep3();
                    event.setEndTime(validateDateTime(scanner.nextLine().trim()));
                    break;
                case 4:
                    ui.showEditCommandStep4();
                    event.setLocation(scanner.nextLine().trim());
                    break;
                case 5:
                    ui.showEditCommandStep5();
                    event.setDescription(scanner.nextLine().trim());
                    break;
                case 6:
                    editing = false;
                    System.out.println("Event editing completed.");
                    break;

                default:
                    ui.showEditCommandCorrectFormat();
                }
                events.updateEvent(index, event);

            }
        } catch (SyncException e) {
            ui.showMessage(e.getMessage());
        }
    }
// Ensure time and date inputs are correct
    private LocalDateTime validateDateTime(String input) {
        while (true) {
            try {
                return LocalDateTime.parse(input, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            } catch (Exception e) {
                System.out.println("❌ Invalid format! Please enter in YYYY-MM-DD HH:MM format.");
                System.out.print("Enter again: ");
                input = scanner.nextLine().trim(); // Prompt the user again for a valid input
            }
        }
    }
}


