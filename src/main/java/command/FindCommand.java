package command;

import event.EventManager;
import participant.Participant;
import participant.ParticipantManager;
import ui.UI;
import exception.SyncException;
import event.Event;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Represents a command to find events by keyword.
 * This command searches for events that contain the specified keyword in their name or description.
 */
public class FindCommand extends Command {
    private final String keyword;
    private static final Logger LOGGER = Logger.getLogger(FindCommand.class.getName());

    /**
     * Constructs a new FindCommand with the specified search keyword.
     *
     * @param keyword The keyword to search for in event names and descriptions
     * @throws AssertionError if the keyword is null or empty
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;

        assert keyword != null && !keyword.trim().isEmpty() : "Search keyword cannot be null or empty";

        LOGGER.info("FindCommand created with keyword: " + keyword);
    }

    /**
     * Executes the command to find events matching the keyword.
     * Searches in event names and descriptions and displays matching events.
     *
     * @param events The event manager containing the events
     * @param ui The user interface for displaying results
     * @param participantManager The participant manager for accessing user information
     * @throws SyncException if the user is not logged in or other errors occur
     * @throws AssertionError if any of the parameters are null
     */
    @Override
    public void execute(EventManager events, UI ui, ParticipantManager participantManager) throws SyncException {
        LOGGER.info("Executing FindCommand with keyword: " + keyword);

        assert events != null : "EventManager cannot be null";
        assert ui != null : "UI cannot be null";
        assert participantManager != null : "ParticipantManager cannot be null";

        try {
            Participant participant = participantManager.getCurrentUser();

            if (participant == null) {
                LOGGER.warning("Find command attempted without login");
                throw new SyncException("You are not logged in. Enter 'login' to log in first.");
            }
            LOGGER.info("Current user: " + participant.getName() + " (Admin: " + participant.isAdmin() + ")");

            ArrayList<Event> matchingEvents = new ArrayList<>();
            ArrayList<Event> allEvents = new ArrayList<>();

            if (participant.isAdmin()) {
                allEvents = events.getEvents();
                LOGGER.info("Admin user - searching all events (" + allEvents.size() + ")");
            } else {
                allEvents = events.getEventsByParticipant(participantManager);
                LOGGER.info("Regular user - searching only assigned events (" + allEvents.size() + ")");
            }

            String searchKeyword = keyword.trim().toLowerCase();
            LOGGER.info("Normalized search keyword: '" + searchKeyword + "'");

            for (Event event : allEvents) {
                String eventName = event.getName().trim().toLowerCase();
                String eventDescription = event.getDescription().trim().toLowerCase();

                if (eventName.contains(searchKeyword) || eventDescription.contains(searchKeyword)) {
                    LOGGER.fine("Match found - Event: " + event.getName());
                    matchingEvents.add(event);
                }
            }

            LOGGER.info("Found " + matchingEvents.size() + " matching events");
            ui.printMatchingEvents(matchingEvents);
        } catch (SyncException e) {
            LOGGER.log(Level.SEVERE, "SyncException during find operation", e);
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error during find operation", e);
            throw new SyncException("Error during find operation: " + e.getMessage());
        }
    }
}
