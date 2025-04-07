package commandfactory;

import java.util.ArrayList;
import java.util.Scanner;

import command.Command;
import command.DeleteCommand;
import event.Event;
import event.EventManager;
import exception.SyncException;
import participant.ParticipantManager;
import ui.UI;

/**
 * Factory class responsible for creating a DeleteCommand.
 * This factory handles the process of finding and deleting an event,
 * ensuring that the user is logged in and has admin privileges.
 */
public class DeleteCommandFactory implements CommandFactory {
    private final ParticipantManager participantManager;
    private final UI ui;
    private final EventManager eventManager;

    /**
     * Constructor to initialize the factory with participant manager, UI, and event manager.
     *
     * @param participantManager The participant manager to handle participant data
     * @param ui The UI used to interact with the user
     * @param eventManager The event manager to handle event data
     */
    public DeleteCommandFactory(ParticipantManager participantManager, UI ui, EventManager eventManager) {
        this.participantManager = participantManager;
        this.ui = ui;
        this.eventManager = eventManager;
    }

    /**
     * Creates a DeleteCommand based on the user's input.
     * This method will check if the user is logged in and has admin privileges,
     * and then prompt the user to select an event to delete.
     *
     * @return A new DeleteCommand to delete the selected event
     * @throws SyncException If an error occurs during the command creation, such as invalid input or lack of privileges
     */
    @Override
    public Command createCommand() throws SyncException {
        if (participantManager.getCurrentUser() == null) {
            throw new SyncException("You are not logged in. Please enter 'login' to login.");
        } else if (!participantManager.isCurrentUserAdmin()) {
            throw new SyncException("Only admin can delete events!");
        } else {
            String name = ui.readDeleteName();
            ArrayList<Event> matchingEvents = findMatchingEvents(name);

            if (matchingEvents.isEmpty()) {
                throw new SyncException("No events found with the name: " + name);
            }

            Event eventToDelete;
            if (matchingEvents.size() == 1) {
                eventToDelete = matchingEvents.get(0);
            } else {
                ui.showMatchingEventsWithIndices(matchingEvents, eventManager);
                int eventIndex = readDeleteEventIndex(matchingEvents);
                eventToDelete = matchingEvents.get(eventIndex);
            }

            int actualIndex = eventManager.getEvents().indexOf(eventToDelete);
            if (actualIndex == -1) {
                throw new SyncException("Event no longer exists.");
            } else {
                return new DeleteCommand(actualIndex);
            }
        }
    }

    /**
     * Finds all events that contain the specified name.
     *
     * @param name The name of the event to search for
     * @return A list of events matching the specified name
     */
    private ArrayList<Event> findMatchingEvents(String name) {
        ArrayList<Event> matchingEvents = new ArrayList<>();
        for (Event event : eventManager.getEvents()) {
            if (event.getName().toLowerCase().contains(name.toLowerCase())) {
                matchingEvents.add(event);
            }
        }
        return matchingEvents;
    }

    /**
     * Reads the index of the event to delete from the user input.
     *
     * @param matchingEvents A list of events to choose from
     * @return The actual index of the event in the event manager
     * @throws SyncException If the input is invalid or the index is out of bounds
     */
    private int readDeleteEventIndex(ArrayList<Event> matchingEvents) throws SyncException {
        ui.showMessage("Enter the index of the event you want to delete: ");
        try {
            Scanner scanner = new Scanner(System.in);
            int index = Integer.parseInt(scanner.nextLine()) - 1;
            if (index < 0 || index >= matchingEvents.size()) {
                throw new SyncException("Invalid event index. Please enter a valid index.");
            }
            return eventManager.getEvents().indexOf(matchingEvents.get(index));
        } catch (NumberFormatException e) {
            throw new SyncException("Invalid index format. Please enter a number.");
        }
    }
}
