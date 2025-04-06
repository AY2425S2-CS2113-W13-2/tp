package commandfactory;

import java.util.ArrayList;
import java.util.Scanner;

import command.Command;
import command.DeleteCommand;
import event.Event;
import event.EventManager;
import exception.SyncException;
import parser.CommandParser;
import participant.ParticipantManager;

public class DeleteCommandFactory implements CommandFactory{
    private final ParticipantManager participantManager;
    private final ui.UI ui;
    private final EventManager eventManager;

    public DeleteCommandFactory(ParticipantManager participantManager, ui.UI ui, EventManager eventManager) {
        this.participantManager = participantManager;
        this.ui = ui;
        this.eventManager = eventManager;
    }

    public Command createCommand() throws SyncException {
        if (participantManager.getCurrentUser() == null) {
            throw new SyncException("You are not logged in. Please enter 'login' to login.");
        } else if (!participantManager.isCurrentUserAdmin()) {
            throw new SyncException("Only admin can delete events!");
        } else {
            String name =ui.readDeleteName();
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

    private ArrayList<Event> findMatchingEvents(String name) {
        ArrayList<Event> matchingEvents = new ArrayList<>();
        for (Event event : eventManager.getEvents()) {
            if (event.getName().toLowerCase().contains(name.toLowerCase())) {
                matchingEvents.add(event);
            }
        }
        return matchingEvents;
    }

    private int readDeleteEventIndex(ArrayList<Event> matchingEvents) throws SyncException {  // 🔹 Ask for event index
        ui.showMessage("Enter the index of the event you want to delete: ");
        try {
            Scanner scanner = new Scanner(System.in);
            int index = Integer.parseInt(scanner.nextLine()) - 1;
            if (index < 0 || index >= matchingEvents.size()) {
                throw new SyncException("Invalid event index. Please enter a valid index.");
            }
            return eventManager.getEvents().indexOf(matchingEvents.get(index));
            // Convert matching event index to actual event index
        } catch (NumberFormatException e) {
            throw new SyncException("Invalid index format. Please enter a number.");
        }
    }
}
