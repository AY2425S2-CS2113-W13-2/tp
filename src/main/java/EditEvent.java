import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;


public class EditEvent {
    private final Scanner scanner;
    private final EventManager eventManager;

    public EditEvent(EventManager eventManager) {
        this.scanner = new Scanner(System.in);
        this.eventManager = eventManager;
    }

    public void editEvent(int eventIndex) {
        try {
            Event event = eventManager.getEvent(eventIndex);
            boolean editing = true;

            while (editing) {
                System.out.println("\nEditing Event: " + event.getName());
                System.out.println("1. Edit Name");
                System.out.println("2. Edit Start Time");
                System.out.println("3. Edit End Time");
                System.out.println("4. Edit Location");
                System.out.println("5. Edit Description");
                System.out.println("6. Done Editing");
                System.out.print("Select an option: ");

                // Fix: Properly consume input
                if (!scanner.hasNextInt()) {
                    System.out.println("Invalid input! Please enter a number.");
                    scanner.next(); // Clear invalid input
                    continue;
                }

                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline after nextInt()

                switch (choice) {
                    case 1:
                        System.out.print("Enter New Event Name: ");
                        event.setName(scanner.nextLine().trim());
                        break;
                    case 2:
                        System.out.print("Enter New Start Time (YYYY-MM-DD HH:MM): ");
                        event.setStartTime(validateDateTime(scanner.nextLine().trim()));
                        break;
                    case 3:
                        System.out.print("Enter New End Time (YYYY-MM-DD HH:MM): ");
                        event.setEndTime(validateDateTime(scanner.nextLine().trim()));
                        break;
                    case 4:
                        System.out.print("Enter New Event Location: ");
                        event.setLocation(scanner.nextLine().trim());
                        break;
                    case 5:
                        System.out.print("Enter New Event Description: ");
                        event.setDescription(scanner.nextLine().trim());
                        break;
                    case 6:
                        editing = false;
                        System.out.println("Event editing completed.");
                        break;
                    default:
                        System.out.println("Invalid choice. Try again.");
                }

                System.out.println("\nUpdated Event Details:");
                System.out.println(event);
            }
        } catch (SyncException e) {
            System.out.println(e.getMessage());
        }
    }

    private LocalDateTime validateDateTime(String input) {
        try {
            return LocalDateTime.parse(input, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        } catch (Exception e) {
            System.out.println("Invalid format! Please enter in YYYY-MM-DD HH:MM format.");
            return null;
        }
    }
}
