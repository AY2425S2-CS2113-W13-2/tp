package ui;

import java.util.ArrayList;
import java.util.Scanner;

import event.Event;
import event.EventManager;
import exception.SyncException;
import participant.Participant;

/**
 * The UI class is responsible for handling the user interface of the EventSync system.
 * It provides methods to display menus, show event details, handle user inputs,
 * and display messages related to events and participants.
 */
public class UI {
    public static Scanner scanner = new Scanner(System.in);

    /**
     * Sets a new Scanner instance for reading user input.
     *
     * @param newScanner the new Scanner instance to use
     */
    public void setScanner(Scanner newScanner) {
        UI.scanner = newScanner;
    }

    /**
     * Displays a given message to the user.
     *
     * @param message the message to display
     */
    public void showMessage(String message) {
        System.out.println(message);
    }

    /**
     * Displays the main command menu for the user, listing available options for event,
     * participant, session, and system commands.
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
     * Displays an event along with its index and priority.
     *
     * @param event the event to display
     * @param index the index of the event in the list
     * @param priority the priority of the event
     */
    public void showEventWithIndex(Event event, int index, String priority) {
        System.out.println("The event " + index + " is: ");
        System.out.println(event.toString());
        System.out.println("Priority: " + priority);
        System.out.println();
    }

    /**
     * Displays a message when the list of events is empty.
     */
    public void showEmptyListMessage() {
        System.out.println("There is nothing to view");
    }

    /**
     * Displays the events that match a search query.
     *
     * @param events the list of matching events
     */
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

    /**
     * Prompts the user to enter event details in a specific format.
     */
    public void showAddFormat() {
        System.out.println("Enter event details (format: Event Name | " +
                "Start Date | End Date | Location | Description):");
    }

    /**
     * Displays a message confirming the successful addition of an event.
     *
     * @param event the event that was added
     */
    public void showAddedMessage(Event event) {
        System.out.println("The event\n" + event.toString() + "\nhas been added to the list.");
    }

    /**
     * Displays a list of options to edit an event.
     *
     * @param event the event to be edited
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
     * Displays an error message when the user enters an invalid input for editing.
     */
    public void showEditCommandCorrectFormat() {
        System.out.println("Invalid input! Please enter a number from 1 to 5");
    }

    /**
     * Displays a prompt to enter a new event name.
     */
    public void showEditCommandStep1() {
        System.out.print("Enter New Event Name: ");
    }

    /**
     * Displays a prompt to enter a new start time for the event.
     */
    public void showEditCommandStep2() {
        System.out.print("Enter New Start Time (YYYY-MM-DD HH:MM): ");
    }

    /**
     * Displays a prompt to enter a new end time for the event.
     */
    public void showEditCommandStep3() {
        System.out.print("Enter New End Time (YYYY-MM-DD HH:MM): ");
    }

    /**
     * Displays a prompt to enter a new location for the event.
     */
    public void showEditCommandStep4() {
        System.out.print("Enter New Event Location: ");
    }

    /**
     * Displays a prompt to enter a new description for the event.
     */
    public void showEditCommandStep5() {
        System.out.print("Enter New Event Description: ");
    }

    /**
     * Displays the updated details of an event after editing.
     *
     * @param event the edited event
     */
    public void showEditedEvent(Event event) {
        System.out.println("\nUpdated Event Details:");
        System.out.println(event);
    }

    /**
     * Displays a goodbye message when the user exits the program.
     */
    public void showByeMessage() {
        System.out.println("Bye!");
    }

    /**
     * Displays a warning message when there is a scheduling conflict for an event.
     *
     * @param newEvent the new event being scheduled
     * @param collisions the list of events that conflict with the new event
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
     * Displays a warning message when there is a participant slot conflict for an event.
     *
     * @param event the event being scheduled
     * @param collisions the list of events that conflict with the participant's availability
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
     * Displays a message confirming the deletion of an event.
     *
     * @param event the event that was deleted
     */
    public void showDeletedMessage(Event event) {
        System.out.println("\"" + event.getName() + "\" has been deleted.");
    }

    /**
     * Prompts the user for input to duplicate an event.
     *
     * @return the input command for duplicating an event
     */
    public String readDuplicateEventInput() {
        System.out.print("Enter duplicate command (format: <index> <New Event Name>): ");
        return scanner.nextLine();
    }

    /**
     * Displays the events that match a search query, with their indices.
     *
     * @param matchingEvents the list of matching events
     * @param eventManager the event manager handling the events
     */
    public void showMatchingEventsWithIndices(ArrayList<Event> matchingEvents, EventManager eventManager) {
        System.out.println("\nMatching Events:");
        for (int i = 0; i < matchingEvents.size(); i++) {
            System.out.println((i + 1) + ". " + matchingEvents.get(i).getName());
        }
    }

    /**
     * Prompts the user to confirm the deletion of an event.
     *
     * @param eventName the name of the event to be deleted
     * @return true if the user confirms, false otherwise
     */
    public boolean confirmDeletion(String eventName) {
        System.out.print("Confirm deletion of \"" + eventName + "\"? (yes/no): ");
        String confirmation = scanner.nextLine().trim().toLowerCase();
        return confirmation.equals("yes");
    }

    /**
     * Displays a message when event deletion is cancelled.
     */
    public void showDeletionCancelledMessage() {
        System.out.println("Deletion cancelled.");
    }

    /**
     * Prompts the user to enter participant details.
     */
    public void showAddParticipantFormat() {
        System.out.println("Enter participant details (format: Name | Email | AccessLevel[ADMIN/MEMBER]):");
    }

    /**
     * Displays a message confirming the successful addition of a participant.
     *
     * @param p the participant that was added
     */
    public void showParticipantAdded(Participant p) {
        System.out.println("Participant added: " + p);
    }

    /**
     * Displays a message when a user logs out.
     */
    public void showLogOutMessage() {
        System.out.println("Bye! Press 'login' to log in or 'create' to create a new user." );
    }

    /**
     * Prompts the user for confirmation, typically for a "yes/no" decision.
     *
     * @param message the message to display
     * @return true if the user enters "y", false otherwise
     */
    public boolean askConfirmation(String message) {
        System.out.println(message);
        String input = scanner.nextLine().trim().toLowerCase();
        return input.equals("y");
    }

    /**
     * Displays a message confirming successful login.
     */
    public void showSuccessLoginMessage() {
        System.out.println("Successfully logged in.");
    }

    /**
     * Displays a message confirming the successful creation of a user.
     *
     * @param participant the participant that was created
     */
    public void showSuccessCreateMessage(Participant participant) {
        System.out.println("Successfully created: " + participant.getName());
        System.out.println("Please enter 'login' to log in or continue with your previous command.");
    }

    /**
     * Displays a welcome message to the user, with options to log in or create a new user.
     */
    public void showWelcomeMessage() {
        System.out.println("Welcome to EventSync!");
        System.out.println("Press 'login' to log in or 'create' to create a new user." );
    }

    /**
     * Reads a line of input from the user.
     *
     * @return the input string
     */
    public String readLine() {
        return scanner.nextLine();
    }

    /**
     * Reads an integer input from the user.
     *
     * @return the parsed integer, or null if the input is not a valid integer
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
     * Displays the options for editing an event and prompts the user to choose one.
     *
     * @param event the event to edit
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
     * Prompts the user to enter event details when adding a new event.
     *
     * @return the input command for adding an event
     */
    public String readAddCommandInput() {
        System.out.print("Enter event details (format: Event Name | " +
                "Start Date | End Date | Location | Description): \n");
        return scanner.nextLine();
    }

    /**
     * Splits the input string for adding a participant into event index and participant name.
     *
     * @return an array of two strings: the event index and the participant name
     * @throws SyncException if the input format is invalid
     */
    public String[] splitAddParticipantCommandInput() throws SyncException {
        System.out.println("Use: <EventIndex> | <Participant Name>");
        String input = this.scanner.nextLine();
        String[] parts = input.split("\\|");
        if (parts.length != 2) {
            throw new SyncException("Invalid format. Use: <EventIndex> | <Participant Name>. " +
                    "Enter 'addparticipant' to try again.");
        }
        return parts;
    }

    /**
     * Prompts the user to enter a name to search for events to delete.
     *
     * @return the name entered by the user
     */
    public String readDeleteName() {
        System.out.print("Enter name to search for events to delete: ");
        return scanner.nextLine().trim();
    }

    /**
     * Prompts the user to enter a priority or a range for filtering events.
     *
     * @return the priority or range input by the user
     */
    public String readFilterInput() {
        System.out.print("Enter a priority or a range: ");
        return scanner.nextLine().trim();
    }

    /**
     * Prompts the user to enter a participant's name.
     *
     * @return the participant's name entered by the user
     */
    public String askParticipantName() {
        System.out.print("Enter participant's name: ");
        return scanner.nextLine().trim();
    }

    /**
     * Prompts the user to enter a participant's password.
     *
     * @return the password entered by the user
     */
    public String askPassword() {
        System.out.print("Enter participant's password: ");
        return scanner.nextLine().trim();
    }

    /**
     * Prompts the user to enter their sort type preference (priority, start, or end).
     *
     * @return the sort type entered by the user
     */
    public String readListCommandInput() {
        System.out.print("Enter your sort type (priority, start, end): ");
        return scanner.nextLine().trim();
    }

    /**
     * Prompts the user to enter participant details in the format: <EventIndex> | <Participant Name> | <AccessLevel> | <Availability>.
     *
     * @return the participant details entered by the user
     */
    public String readAddParticipantInput() {
        System.out.print("Follow this format: <EventIndex> | <Participant Name> | <AccessLevel> | <Availability> \n");
        return scanner.nextLine().trim();
    }
}
