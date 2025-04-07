package command;

import event.EventManager;
import participant.Participant;
import participant.ParticipantManager;
import ui.UI;
import exception.SyncException;
import event.Event;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Represents a command to find events based on a search keyword.
 * The command searches for events whose name or description contains
 * the provided keyword and displays the matching events to the user.
 */
public class FindCommand extends Command {

    /** The keyword to search for in event names and descriptions. */
    private static final Logger LOGGER = Logger.getLogger(FindCommand.class.getName());

    private final String keyword;

    /**
     * Constructs a new FindCommand with the specified search keyword.
     *
     * @param keyword the keyword to search for in event names and descriptions
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Executes the find command, searching for events that match the keyword
     * in their names or descriptions. Displays the matching events to the user.
     *
     * @param events the EventManager instance that manages the events
     * @param ui the UI instance used to display messages to the user
     * @param participantManager the ParticipantManager instance that manages participants
     * @throws SyncException if an error occurs during the find operation
     */
    @Override
    public void execute(EventManager events, UI ui, ParticipantManager participantManager) throws SyncException {
        assert keyword != null : "Search keyword cannot be null";
        LOGGER.info("Searching for events with keyword: " + keyword);
        try {
            Participant participant = participantManager.getCurrentUser();

            if (participant == null) {
                throw new SyncException("You are not logged in. Enter 'login' to log in first.");
            }

            ArrayList<Event> matchingEvents = new ArrayList<>();
            ArrayList<Event> allEvents = new ArrayList<>();

            if (participant.isAdmin()) {
                allEvents = events.getEvents();
            } else {
                allEvents = events.getEventsByParticipant(participantManager);
            }

            for (Event event : allEvents) {
                String eventName = event.getName().trim().toLowerCase();
                String eventDescription = event.getDescription().trim().toLowerCase();
                String searchKeyword = keyword.trim().toLowerCase();

                if (eventName.contains(searchKeyword) || eventDescription.contains(searchKeyword)) {
                    matchingEvents.add(event);
                }
            }

            ui.printMatchingEvents(matchingEvents);

        } catch (Exception e) {
            throw new SyncException("Error during find operation: " + e.getMessage());
        }
    }
}
