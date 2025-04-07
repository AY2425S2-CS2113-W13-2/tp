package ui;

import java.util.ArrayList;
import java.util.Scanner;

import event.Event;
import event.EventManager;
import exception.SyncException;
import participant.Participant;

/**
 * The UI class provides methods for handling user interface operations related to event management.
 * It supports displaying menus, messages, event details, and input prompts to the user.
 */
public class UI {
    public static Scanner scanner = new Scanner(System.in);

    /**
     * Sets the scanner instance to a new one for handling user inputs.
     *
     * @param newScanner The new scanner instance to be used.
     */
    public void setScanner(Scanner newScanner) {
        UI.scanner = newScanner;
    }

    /**
     * Displays a message to the user.
     *
     * @param message The message to be displayed.
     */
    public void showMessage(String message) {
        System.out.println(message);
    }

    /**
     * Displays the main menu for the EventSync application.
     */
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

    /**
     * Displays the event with its index and priority.
     *
     * @param event    The event to be displayed.
     * @param index    The index of the event in the list.
     * @param priority The priority of the event.
     */
    public void showEventWithIndex(Event event, int index, String priority) {
        System.out.println("The event " + index + " is: ");
        System.out.println(event.toString());
        System.out.println("Priority: " + priority);
        System.out.println();
    }

    /**
     * Displays a message when the list is empty.
     */
    public void showEmptyListMessage() {
        System.out.println("There is nothing to view");
    }

    /**
     * Prints the matching events to the user.
     *
     * @param events A list of events to be displayed.
     */
    public void printMatchingEvents(ArrayList<Event> events) {
        if (events.isEmpty()) {
            System.out.println("No matching tasks found.");
        } else {
            System.out.println("Found " + events.size() + " matching events.");
            System.out.println("Here are the matching events in your list: ");
            for (int i = 0; i < events.size(); i++) {
                System.out.println(" " + (i + 1) + ". " + events.get(i).toString());
            }
        }
    }

    /**
     * Prompts the user to input event details for adding an event.
     */
    public void showAddFormat() {
        System.out.println("Enter event details (format: Event Name | " +
                "Start Date | End Date | Location | Description):");
    }

    /**
     * Displays a message confirming that the event has been added.
     *
     * @param event The event that has been added.
     */
    public void showAddedMessage(Event event) {
        System.out.println("The event\n" + event.toString() + "\nhas been added to the list.");
    }

    /**
     * Displays a message with the available options for editing an event.
     *
     * @param event The event that is being edited.
     */
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

    /**
     * Displays a message when an invalid input is entered for editing an event.
     */
    public void showEditCommandCorrectFormat() {
        System.out.println("Invalid input! Please enter a number from 1 to 5");
    }

    /**
     * Prompts the user to input a new event name for editing.
     */
    public void showEditCommandStep1() {
        System.out.print("Enter New Event Name  (or type 'exit' to cancel): ");
    }

    /**
     * Prompts the user to input a new start time for editing.
     */
    public void showEditCommandStep2() {
        System.out.print("Enter New Start Time (YYYY-MM-DD HH:MM) (or type 'exit' to cancel): ");
    }

    /**
     * Prompts the user to input a new end time for editing.
     */
    public void showEditCommandStep3() {
        System.out.print("Enter New End Time (YYYY-MM-DD HH:MM) (or type 'exit' to cancel): ");
    }

    /**
     * Prompts the user to input a new event location for editing.
     */
    public void showEditCommandStep4() {
        System.out.print("Enter New Event Location (or type 'exit' to cancel): ");
    }

    /**
     * Prompts the user to input a new event description for editing.
     */
    public void showEditCommandStep5() {
        System.out.print("Enter New Event Description (or type 'exit' to cancel): ");
    }

    /**
     * Displays the updated event details after editing.
     *
     * @param event The edited event.
     */
    public void showEditedEvent(Event event) {
        System.out.println("\nUpdated Event Details:");
        System.out.println(event);
    }

    /**
     * Displays a goodbye message to the user.
     */
    public void showByeMessage() {
        System.out.println("Bye!");
    }

    /**
     * Displays a warning when there is a scheduling conflict with a new event.
     *
     * @param newEvent   The new event being scheduled.
     * @param collisions A list of events that overlap with the new event.
     */
    public void showCollisionWarning(Event newEvent, ArrayList<Event> collisions) {
        System.out.println("Warning: Scheduling Conflict");
        System.out.println("Time of the new event overlaps with the following event(s):");
        for (Event collision : collisions) {
            System.out.println(collision.toString());
        }
        System.out.println("Please edit your events to resolve the conflict.");
    }

    /**
     * Displays a warning when there is a scheduling conflict with participant availability.
     *
     * @param event      The event being scheduled.
     * @param collisions A list of events that have a scheduling conflict with the participants.
     */
    public void showParticipantSlotCollisionWarning(Event event, ArrayList<Event> collisions) {
        System.out.println("Warning: Scheduling Conflict");
        System.out.println("Participants are not able to attend");
        for (Event collision : collisions) {
            System.out.println(collision.toString());
        }
        System.out.println("Please find another participant");
    }

    /**
     * Displays a message confirming that an event has been deleted.
     *
     * @param event The event that has been deleted.
     */
    public void showDeletedMessage(Event event) {
        System.out.println("\"" + event.getName() + "\" has been deleted.");
    }

    /**
     * Prompts the user to input a command for duplicating an event.
     *
     * @return The input command for duplicating the event.
     * @throws SyncException If the user enters an invalid input or cancels the operation.
     */
    public String readDuplicateEventInput() throws SyncException {
        System.out.print("Enter duplicate command (format: <index> <New Event Name>) (or type 'exit' to cancel): ");
        String input = scanner.nextLine().trim();
        checkForExit(input);
        return input;
    }

    /**
     * Displays a list of matching events with their indices.
     *
     * @param matchingEvents A list of events that match the user's criteria.
     * @param eventManager   The event manager instance used to retrieve event details.
     */
    public void showMatchingEventsWithIndices(ArrayList<Event> matchingEvents, EventManager eventManager) {
        System.out.println("\nMatching Events:");
        for (int i = 0; i < matchingEvents.size(); i++) {
            System.out.println((i + 1) + ". " + matchingEvents.get(i).getName());
        }
    }

    /**
     * Asks the user for confirmation before deleting an event.
     *
     * @param eventName The name of the event to be deleted.
     * @return true if the user confirms deletion, false otherwise.
     */
    public boolean confirmDeletion(String eventName) {
        System.out.print("Confirm deletion of \"" + eventName + "\"? (yes/no): ");
        String confirmation = scanner.nextLine().trim().toLowerCase();
        return confirmation.equals("yes");
    }

    /**
     * Displays a message confirming that the deletion process has been cancelled.
     */
    public void showDeletionCancelledMessage() {
        System.out.println("Deletion cancelled.");
    }

    /**
     * Prompts the user to input participant details for adding a participant.
     */
    public void showAddParticipantFormat() {
        System.out.println("Enter participant details (format: Name | Email | AccessLevel[ADMIN/MEMBER]):");
    }

    /**
     * Displays a message confirming that a participant has been added to an event.
     *
     * @param p The participant that was added.
     */
    public void showParticipantAdded(Participant p) {
        System.out.println("Participant added: " + p);
    }

    /**
     * Displays a logout message to the user.
     */
    public void showLogOutMessage() {
        System.out.println("Bye! Press 'login' to log in or 'create' to create a new user.");
    }

    /**
     * Asks the user for a confirmation input.
     *
     * @param message The message to be displayed for confirmation.
     * @return true if the user confirms, false otherwise.
     */
    public boolean askConfirmation(String message) {
        System.out.println(message);
        String input = scanner.nextLine().trim().toLowerCase();

        return input.equals("y");
    }

    /**
     * Displays a success message when a user successfully logs in.
     */
    public void showSuccessLoginMessage() {
        System.out.println("Successfully logged in.");
    }

    /**
     * Displays a success message when a user is successfully created.
     *
     * @param participant The participant that was created.
     */
    public void showSuccessCreateMessage(Participant participant) {
        System.out.println("Successfully created: " + participant.getName());
        System.out.println("Please enter 'login' to log in or continue with your previous command.");
    }

    /**
     * Displays a welcome message to the user when the system starts.
     */
    public void showWelcomeMessage() {
        System.out.println("Welcome to EventSync!");
        System.out.println("Press 'login' to log in or 'create' to create a new user.");
    }

    /**
     * Reads a line of input from the user.
     *
     * @return The line of input entered by the user.
     */
    public String readLine() {
        return scanner.nextLine();
    }

    /**
     * Reads an integer input from the user.
     *
     * @return The integer input entered by the user, or null if the input is not a valid integer.
     */
    public Integer readInt() {
        String input = scanner.nextLine().trim();
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Displays the edit menu for an event with options to modify various event details.
     *
     * @param event The event being edited.
     */
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

    /**
     * Reads the input for adding an event from the user.
     *
     * @return The input string containing event details.
     * @throws SyncException If the user cancels the operation by typing 'exit'.
     */
    public String readAddCommandInput() throws SyncException {
        System.out.print("Enter event details (format: Event Name | " +
                "Start Date | End Date | Location | Description) (or type 'exit' to cancel) : \n");
        String input = scanner.nextLine().trim();
        checkForExit(input);
        return input;
    }

    /**
     * Splits the input string for adding a participant and validates the format.
     *
     * @return A string array with the event index and participant name.
     * @throws SyncException If the input format is invalid.
     */
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

    /**
     * Reads the name input from the user to search for events to delete.
     *
     * @return The name entered by the user.
     * @throws SyncException If the user cancels the operation by typing 'exit'.
     */
    public String readDeleteName() throws SyncException {
        System.out.print("Enter name to search for events to delete (or type 'exit' to cancel): ");
        String input = scanner.nextLine().trim();
        checkForExit(input);
        return input;
    }

    /**
     * Reads a filter input (priority or range) from the user.
     *
     * @return The input filter entered by the user (either a priority or a range).
     * @throws SyncException If the user types "exit", the operation is cancelled.
     */
    public String readFilterInput() throws SyncException {
        System.out.print("Enter a priority or a range (or type 'exit' to cancel): ");
        String input = scanner.nextLine().trim();
        checkForExit(input);
        return input;
    }

    /**
     * Prompts the user to enter a participant's name.
     *
     * @return The name of the participant entered by the user.
     * @throws SyncException If the user types "exit", the operation is cancelled.
     */
    public String askParticipantName() throws SyncException {
        System.out.print("Enter participant's name (or type 'exit' to cancel): ");
        String input = scanner.nextLine().trim();
        checkForExit(input);
        return input;
    }

    /**
     * Prompts the user to enter a participant's password.
     *
     * @return The password of the participant entered by the user.
     * @throws SyncException If the user types "exit", the operation is cancelled.
     */
    public String askPassword() throws SyncException {
        System.out.print("Enter participant's password (or type 'exit' to cancel): ");
        String input = scanner.nextLine().trim();
        checkForExit(input);
        return input;
    }

    /**
     * Reads the list sorting command input from the user (priority, start, or end).
     *
     * @return The sorting type (priority, start, or end) entered by the user.
     * @throws SyncException If the user types "exit", the operation is cancelled.
     */
    public String readListCommandInput() throws SyncException {
        System.out.print("Enter your sort type (priority, start, end) or type 'exit' to cancel: ");
        String input = scanner.nextLine().trim();
        checkForExit(input);
        return input;
    }

    /**
     * Reads input from the user to add a participant, following the specified format.
     *
     * @return The input containing event index, participant name, access level, and availability.
     */
    public String readAddParticipantInput() {
        System.out.print("Follow this format: <EventIndex> | <Participant Name> | <AccessLevel> | <Availability> \n");
        return scanner.nextLine().trim();
    }

    /**
     * Checks if the user input is "exit", and if so, throws a SyncException to cancel the operation.
     *
     * @param input The user input to be checked.
     * @throws SyncException If the user types "exit", the operation is cancelled.
     */
    public void checkForExit(String input) throws SyncException {
        if (input.trim().equalsIgnoreCase("exit")) {
            throw new SyncException("Operation cancelled.");
        }
    }
}