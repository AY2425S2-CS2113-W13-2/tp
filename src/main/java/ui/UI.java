package ui;

import java.util.ArrayList;
import java.util.Scanner;

import event.Event;
import event.EventManager;
import exception.SyncException;
import participant.Participant;

/**
 * Handles all user interface interactions for the EventSync application.
 * <p>
 * This class manages displaying information to users and collecting user input,
 * providing a consistent interface throughout the application.
 * </p>
 */

public class UI {
    public static Scanner scanner = new Scanner(System.in);

    public void setScanner(Scanner newScanner) {
        UI.scanner = newScanner;
    }

    public void showMessage(String message) {
        System.out.println(message);
    }

    public void showMenu() {
        System.out.println("╔═════════════════════════════════════════╗");
        System.out.println("║          EVENT SYNC COMMAND MENU        ║");
        System.out.println("╠═════════════════════════════════════════╣");
        System.out.println("║  === Event Management Commands ===      ║");
        System.out.println("║  add            - Add new event         ║");
        System.out.println("║  listall        - List all events       ║");
        System.out.println("║  delete         - Delete an event       ║");
        System.out.println("║  edit           - Edit an event         ║");
        System.out.println("║  duplicate [ID] - Duplicate an event    ║");
        System.out.println("║  addparticipant - Add to event          ║");
        System.out.println("║                                         ║");
        System.out.println("║  === Participant Commands ===           ║");
        System.out.println("║  list           - List your events      ║");
        System.out.println("║  find [KEYWORD] - Search events         ║");
        System.out.println("║  filter         - Filter events         ║");
        System.out.println("║  listparticipants- List participants    ║");
        System.out.println("║                                         ║");
        System.out.println("║  === Session Commands ===               ║");
        System.out.println("║  create         - Create new user       ║");
        System.out.println("║  login          - Login to system       ║");
        System.out.println("║  logout         - Logout                ║");
        System.out.println("║                                         ║");
        System.out.println("║  === System Commands ===                ║");
        System.out.println("║  bye            - Exit program          ║");
        System.out.println("║  help           - Show this menu        ║");
        System.out.println("╚═════════════════════════════════════════╝");
    }

    public void showEventWithIndex(Event event, int index, String priority) {
        System.out.println("The event " + index + " is: ");
        System.out.println(event.toString());
        System.out.println("Priority: " + priority);
        System.out.println();
    }

    public void showEmptyListMessage() {
        System.out.println("There is nothing to view");
    }

    public void printMatchingEvents(ArrayList<Event> events) {
        if (events.isEmpty()){
            System.out.println("No matching tasks found.");
        } else {
            System.out.println("Found " + events.size() + " matching events.");
            System.out.println("Here are the matching events in your list: ");
            for (int i = 0; i < events.size(); i++) {
                System.out.println(" " + (i + 1) + ". " + events.get(i).toString());
            }
        }
    }

    public void showAddFormat() {
        System.out.println("Enter event details (format: Event Name | " +
                "Start Date | End Date | Location | Description):");
    }

    public void showAddedMessage(Event event) {
        System.out.println("The event\n" + event.toString() + "\nhas been added to the list.");
    }

    public void showEditCommandMessage(Event event) {
        System.out.println("\nEditing Event: " + event.getName());
        System.out.println("1. Edit Name");
        System.out.println("2. Edit Start Time");
        System.out.println("3. Edit End Time");
        System.out.println("4. Edit Location");
        System.out.println("5. Edit Description");
        System.out.println("6. Done Editing");
        System.out.print("Select an option: ");
    }

    public void showEditCommandCorrectFormat() {
        System.out.println("Invalid input! Please enter a number from 1 to 5");
    }

    public void showEditCommandStep1() {
        System.out.print("Enter New Event Name  (or type 'exit' to cancel): ");
    }

    public void showEditCommandStep2() {
        System.out.print("Enter New Start Time (YYYY-MM-DD HH:MM) (or type 'exit' to cancel): ");
    }

    public void showEditCommandStep3() {
        System.out.print("Enter New End Time (YYYY-MM-DD HH:MM) (or type 'exit' to cancel): ");
    }

    public void showEditCommandStep4() {
        System.out.print("Enter New Event Location (or type 'exit' to cancel): ");
    }

    public void showEditCommandStep5() {
        System.out.print("Enter New Event Description (or type 'exit' to cancel): ");
    }

    public void showEditedEvent(Event event) {
        System.out.println("\nUpdated Event Details:");
        System.out.println(event);
    }

    public void showByeMessage() {
        System.out.println("Bye!");
    }

    public void showCollisionWarning(Event newEvent, ArrayList<Event> collisions) {
        System.out.println("Warning: Scheduling Conflict");
        System.out.println("Time of the new event overlaps with the following event(s):");
        for (Event collision : collisions) {
            System.out.println(collision.toString());
        }
        System.out.println("Please edit your events to resolve the conflict.");
    }

    public void showParticipantSlotCollisionWarning(Event event, ArrayList<Event> collisions) {
        System.out.println("Warning: Scheduling Conflict");
        System.out.println("Participants are not able to attend");
        for (Event collision : collisions) {
            System.out.println(collision.toString());
        }
        System.out.println("Please find another participant");
    }

    public void showDeletedMessage(Event event) {
        System.out.println("\"" + event.getName() + "\" has been deleted.");
    }

    public String readDuplicateEventInput() throws SyncException{
        System.out.print("Enter duplicate command (format: <index> <New Event Name>) (or type 'exit' to cancel): ");
        String input = scanner.nextLine().trim();
        checkForExit(input);
        return input;
    }

    public void showMatchingEventsWithIndices(ArrayList<Event> matchingEvents, EventManager eventManager) {
        System.out.println("\nMatching Events:");
        for (int i = 0; i < matchingEvents.size(); i++) {
            System.out.println((i + 1) + ". " + matchingEvents.get(i).getName());
        }
    }

    public boolean confirmDeletion(String eventName) {
        System.out.print("Confirm deletion of \"" + eventName + "\"? (yes/no): ");
        String confirmation = scanner.nextLine().trim().toLowerCase();
        return confirmation.equals("yes");
    }

    public void showDeletionCancelledMessage() {
        System.out.println("Deletion cancelled.");
    }

    public void showAddParticipantFormat() {
        System.out.println("Enter participant details (format: Name | Email | AccessLevel[ADMIN/MEMBER]):");
    }

    public void showParticipantAdded(Participant p) {
        System.out.println("Participant added: " + p);
    }

    public void showLogOutMessage() {
        System.out.println("Bye! Press 'login' to log in or 'create' to create a new user." );
    }
    public boolean askConfirmation(String message) {
        System.out.println(message);
        String input = scanner.nextLine().trim().toLowerCase();

        return input.equals("y");
    }

    public void showSuccessLoginMessage() {
        System.out.println("Successfully logged in.");
    }

    public void showSuccessCreateMessage(Participant participant) {
        System.out.println("Successfully created: " + participant.getName());
        System.out.println("Please enter 'login' to log in or continue with your previous command.");
    }

    public void showWelcomeMessage() {
        System.out.println("Welcome to EventSync!");
        System.out.println("Press 'login' to log in or 'create' to create a new user." );
    }

    public String readLine() {
        return scanner.nextLine();
    }

    public Integer readInt() {
        String input = scanner.nextLine().trim();
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public void showEditCommandMessageWithOptions(event.Event event) {
        System.out.println("\n--- Editing Event ---");
        System.out.println(event);
        System.out.println("What would you like to edit?");
        System.out.println("1. Name");
        System.out.println("2. Start Time (format: yyyy-MM-dd HH:mm)");
        System.out.println("3. End Time (format: yyyy-MM-dd HH:mm)");
        System.out.println("4. Location");
        System.out.println("5. Description");
        System.out.println("6. Done");
        System.out.print("Enter your choice (1-6): ");
    }

    public String readAddCommandInput() throws SyncException {
        System.out.print("Enter event details (format: Event Name | " +
                "Start Date | End Date | Location | Description) (or type 'exit' to cancel) : \n");
        String input = scanner.nextLine().trim();
        checkForExit(input);
        return input;
    }

    public String[] splitAddParticipantCommandInput() throws SyncException {
        System.out.println("Use: <EventIndex> | <Participant Name>");
        System.out.println("Type 'exit' to cancel.");

        String input = scanner.nextLine().trim();
        checkForExit(input);
        String[] parts = input.split("\\|");
        if (parts.length != 2) {
            throw new SyncException("Invalid format. Use: <EventIndex> | <Participant Name>. " +
                    "Enter 'addparticipant' to try again.");
        }

        return parts;
    }

    public String readDeleteName() throws SyncException {
        System.out.print("Enter name to search for events to delete (or type 'exit' to cancel): ");
        String input = scanner.nextLine().trim();
        checkForExit(input);
        return input;
    }

    public String readFilterInput() throws SyncException {
        System.out.print("Enter a priority or a range (or type 'exit' to cancel): ");
        String input = scanner.nextLine().trim();
        checkForExit(input);
        return input;
    }


    public String askParticipantName() throws SyncException {
        System.out.print("Enter participant's name (or type 'exit' to cancel): ");
        String input = scanner.nextLine().trim();
        checkForExit(input);
        return input;
    }

    public String askPassword() throws SyncException {
        System.out.print("Enter participant's password (or type 'exit' to cancel): ");
        String input = scanner.nextLine().trim();
        checkForExit(input);
        return input;
    }


    public String readListCommandInput() throws SyncException {
        System.out.print("Enter your sort type (priority, start, end) or type 'exit' to cancel: ");
        String input = scanner.nextLine().trim();
        checkForExit(input);
        return input;
    }

    public String readAddParticipantInput() {
        System.out.print("Follow this format: <EventIndex> | <Participant Name> | <AccessLevel> | <Availability> \n");
        return scanner.nextLine().trim();
    }
    public void checkForExit(String input) throws SyncException {
        if (input.trim().equalsIgnoreCase("exit")) {
            throw new SyncException("Operation cancelled.");
        }
    }

}
