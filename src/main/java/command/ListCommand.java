package command;

import event.Event;
import event.EventManager;
import exception.SyncException;
import participant.Participant;
import participant.ParticipantManager;
import ui.UI;
import label.Priority;
import sort.Sort;
import sort.SortByPriority;
import sort.SortByStartTime;
import sort.SortByEndTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * Represents a command to list events assigned to the current user.
 * This command supports sorting events by priority, start time, or end time.
 */
public class ListCommand extends Command {
    private final String sortType;
    private static final Logger LOGGER = Logger.getLogger(ListCommand.class.getName());

    /**
     * Constructs a new ListCommand with the specified sort type.
     *
     * @param sortType The type of sorting to apply ("priority", "start", "end")
     * @throws AssertionError if sortType is null
     */
    public ListCommand(String sortType) {
        this.sortType = sortType.toLowerCase();

        assert sortType != null : "Sort type cannot be null";

        LOGGER.info("ListCommand created with sort type: " + this.sortType);
    }

    /**
     * Executes the command to list events assigned to the current user.
     * Ensures a user is logged in, retrieves their events, sorts them according to
     * the specified sort type, and displays them through the UI.
     *
     * @param events The event manager containing the events
     * @param ui The user interface for displaying messages and events
     * @param participants The participant manager for accessing user information
     * @throws SyncException if there is an error during execution
     * @throws AssertionError if any of the parameters are null
     */
    @Override
    public void execute(EventManager events, UI ui, ParticipantManager participants) throws SyncException {
        LOGGER.info("Executing ListCommand with sort type: " + sortType);

        assert events != null : "EventManager cannot be null";
        assert ui != null : "UI cannot be null";
        assert participants != null : "ParticipantManager cannot be null";

        try {
            Participant currentUser = ensureUserLoggedIn(events, ui, participants);
            if (currentUser == null) {
                LOGGER.info("No user logged in after prompt, command aborted");
                return;
            }

            List<Event> userEvents = getUserEvents(events, currentUser);
            LOGGER.info("Retrieved " + userEvents.size() + " events for user: " + currentUser.getName());

            if (userEvents.isEmpty()) {
                LOGGER.info("No events assigned to current user");
                ui.showMessage("No events assigned to you.");
                return;
            }

            Sort sorter = chooseSortStrategy(ui);
            if (sorter == null) {
                LOGGER.info("Using default (unsorted) event display");
                events.viewEvents(userEvents);
                return;
            }

            LOGGER.info("Displaying sorted events using sort strategy: " + sorter.getClass().getSimpleName());
            displaySortedEvents(ui, userEvents, sorter);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error executing ListCommand", e);
            throw new SyncException("Error listing events: " + e.getMessage());
        }
    }

    /**
     * Ensures that a user is logged in, prompting for login if necessary.
     *
     * @param events The event manager
     * @param ui The user interface for interactions
     * @param participants The participant manager for user operations
     * @return The current user, or null if no user is logged in after prompting
     * @throws SyncException if there is an error during login
     */
    private Participant ensureUserLoggedIn(EventManager events, UI ui, ParticipantManager participants)
            throws SyncException {
        LOGGER.info("Checking if user is logged in");
        Participant user = participants.getCurrentUser();
        if (user == null) {
            LOGGER.info("No user logged in, prompting for login");
            ui.showMessage("No user logged in. Do you want to log in?(yes/no)");
            if (ui.readLine().equalsIgnoreCase("yes")) {
                LOGGER.info("User agreed to log in");
                new LoginCommand().execute(events, ui, participants);
                user = participants.getCurrentUser(); // Try again after login
                LOGGER.info("Login result: " + (user != null ? "success" : "failed"));
                return user;
            } else {
                LOGGER.info("User declined to log in");
                return null;
            }
        }
        LOGGER.info("User already logged in: " + user.getName());
        return user;
    }

    /**
     * Retrieves the events assigned to the specified user.
     *
     * @param events The event manager containing all events
     * @param user The user whose events to retrieve
     * @return A list of events assigned to the user
     */
    private List<Event> getUserEvents(EventManager events, Participant user) {
        assert events != null : "EventManager cannot be null";
        assert user != null : "User cannot be null";

        LOGGER.fine("Filtering events for user: " + user.getName());
        return events.getEvents().stream()
                .filter(event -> event.hasParticipant(user))
                .collect(Collectors.toList());
    }

    /**
     * Selects a sorting strategy based on the command's sort type.
     *
     * @param ui The user interface for displaying messages
     * @return The selected Sort strategy, or null if no valid strategy was specified
     */
    private Sort chooseSortStrategy(UI ui) {
        assert ui != null : "UI cannot be null";

        LOGGER.info("Selecting sort strategy for type: " + sortType);
        switch (sortType) {
        case "priority":
            LOGGER.info("Using priority sort");
            return new SortByPriority();
        case "start":
            LOGGER.info("Using start time sort");
            return new SortByStartTime();
        case "end":
            LOGGER.info("Using end time sort");
            return new SortByEndTime();
        default:
            LOGGER.warning("Unknown sort type: " + sortType);
            ui.showMessage("Unknown sort type. Showing unsorted list.");
            return null;
        }
    }

    /**
     * Displays events sorted according to the specified strategy.
     *
     * @param ui The user interface for displaying events
     * @param events The list of events to display
     * @param sorter The sorting strategy to apply
     */
    private void displaySortedEvents(UI ui, List<Event> events, Sort sorter) {
        assert ui != null : "UI cannot be null";
        assert events != null : "Events list cannot be null";
        assert sorter != null : "Sorter cannot be null";

        LOGGER.info("Sorting and displaying " + events.size() + " events");
        ArrayList<Event> sortedEvents = new ArrayList<>(events);
        ArrayList<String> priorities = new ArrayList<>(Priority.getAllPriorities());
        sorter.sort(sortedEvents, priorities);

        if (sortedEvents.isEmpty()) {
            LOGGER.info("No events to display after sorting");
            ui.showMessage("No events to display.");
        } else {
            LOGGER.info("Displaying " + sortedEvents.size() + " sorted events");
            for (int i = 0; i < sortedEvents.size(); i++) {
                ui.showEventWithIndex(sortedEvents.get(i), i + 1, priorities.get(i));
            }
        }
    }
}
