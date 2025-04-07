package command;

import event.Event;
import event.EventManager;
import exception.SyncException;
import ui.UI;
import participant.Participant;
import participant.ParticipantManager;
import label.Priority;
import sort.Sort;
import sort.SortByPriority;
import sort.SortByStartTime;
import sort.SortByEndTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Represents a command to list all events in the system.
 * This command allows administrators to view all events with optional sorting.
 */
public class ListAllCommand extends Command {
    private final String sortType;
    private final UI ui;
    private static final Logger LOGGER = Logger.getLogger(ListAllCommand.class.getName());

    /**
     * Constructs a new ListAllCommand with the specified sort type and UI.
     *
     * @param sortType The type of sorting to apply ("priority", "start", "end")
     * @param ui The user interface for displaying messages
     * @throws AssertionError if sortType or ui is null
     */
    public ListAllCommand(String sortType, UI ui) {
        this.sortType = sortType.toLowerCase();
        this.ui = ui;

        assert sortType != null : "Sort type cannot be null";
        assert ui != null : "UI cannot be null";

        LOGGER.info("ListAllCommand created with sort type: " + this.sortType);
    }

    /**
     * Executes the command to list all events.
     * Checks if the current user has admin access, sorts events according to the
     * specified sort type, and displays them through the UI.
     *
     * @param events The event manager containing the events
     * @param ui The user interface for displaying messages and events
     * @param participants The participant manager for accessing user information
     * @throws SyncException if there is no logged-in user or other errors occur
     * @throws AssertionError if any of the parameters are null
     */
    @Override
    public void execute(EventManager events, UI ui, ParticipantManager participants) throws SyncException {
        LOGGER.info("Executing ListAllCommand with sort type: " + sortType);

        assert events != null : "EventManager cannot be null";
        assert ui != null : "UI cannot be null";
        assert participants != null : "ParticipantManager cannot be null";

        try {
            Participant currentUser = participants.getCurrentUser();
            if (currentUser == null) {
                LOGGER.warning("No user logged in when attempting to list all events");
                ui.showMessage("No user logged in. Do you want to log in?");
                if(ui.readLine().equalsIgnoreCase("yes")) {
                    LOGGER.info("User agreed to log in");
                    new LoginCommand().execute(events, ui, participants);
                    // Refresh user after login attempt
                    currentUser = participants.getCurrentUser();
                    // If still null after login attempt, throw exception
                    if (currentUser == null) {
                        LOGGER.warning("Login attempt failed");
                        throw new SyncException("Login failed. Unable to list events.");
                    }
                } else {
                    LOGGER.warning("User declined to log in");
                    throw new SyncException("No user logged in. Please enter 'login' to log in " +
                            "or 'create' to create a new user and then log in.");
                }
            }

            LOGGER.info("Current user: " + currentUser.getName() +
                    " with access level: " + currentUser.getAccessLevel());

            if (currentUser.getAccessLevel() != Participant.AccessLevel.ADMIN) {
                LOGGER.warning("Non-admin user attempted to list all events");
                ui.showMessage("Sorry, you need to be an ADMIN to access all events.");
                return;
            }

            List<Event> eventList = new ArrayList<>(events.getEvents());
            LOGGER.info("Retrieved " + eventList.size() + " events from event manager");

            if (events.size() == 0) {
                LOGGER.info("No events in the system");
                ui.showMessage("No events in the system.");
                return;
            }

            Sort sequence;
            switch (sortType) {
            case "priority":
                LOGGER.info("Sorting events by priority");
                sequence = new SortByPriority();
                break;
            case "start":
                LOGGER.info("Sorting events by start time");
                sequence = new SortByStartTime();
                break;
            case "end":
                LOGGER.info("Sorting events by end time");
                sequence = new SortByEndTime();
                break;
            default:
                sequence = null;
                LOGGER.warning("Unknown sort type: " + sortType);
                ui.showMessage("Unknown sort type. Showing unsorted list.");
                break;
            }

            if (sequence != null) {
                sequence.sort(eventList, Priority.getAllPriorities());
                LOGGER.info("Events sorted successfully");
            }

            LOGGER.info("Displaying " + eventList.size() + " events");
            events.viewEvents(eventList);
        } catch (SyncException e) {
            LOGGER.log(Level.SEVERE, "SyncException during list all operation", e);
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error during list all operation", e);
            throw new SyncException("Error listing events: " + e.getMessage());
        }
    }
}
