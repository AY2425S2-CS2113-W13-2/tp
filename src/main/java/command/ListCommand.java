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
import java.util.stream.Collectors;

/**
 * Command to list events assigned to the current user, optionally sorted by a specified criterion.
 */
public class ListCommand extends Command {
    private static final Logger LOGGER = Logger.getLogger(ListCommand.class.getName());
    private final String sortType;

    /**
     * Constructs a ListCommand with the specified sorting criterion.
     *
     * @param sortType the type of sorting ('priority', 'start', or 'end')
     */
    public ListCommand(String sortType) {
        this.sortType = sortType.toLowerCase();
    }

    /**
     * Executes the command to list events assigned to the current user,
     * with optional sorting based on the provided sort type.
     *
     * @param events the EventManager instance that manages the events
     * @param ui the UI instance used to display messages to the user
     * @param participants the ParticipantManager instance that manages participants
     * @throws SyncException if there is an issue with logging in or processing events
     */
    @Override
    public void execute(EventManager events, UI ui, ParticipantManager participants) throws SyncException {
        assert sortType != null : "Sort type cannot be null";
        LOGGER.info("Listing events with sort type: " + sortType);
        Participant currentUser = ensureUserLoggedIn(events, ui, participants);
        if (currentUser == null) {
            return;
        }

        List<Event> userEvents = getUserEvents(events, currentUser);
        if (userEvents.isEmpty()) {
            ui.showMessage("No events assigned to you.");
            return;
        }

        Sort sorter = chooseSortStrategy(ui);
        if (sorter == null) {
            events.viewEvents(userEvents);
            return;
        }

        displaySortedEvents(ui, userEvents, sorter);
    }

    /**
     * Ensures that the current user is logged in. If not, prompts for login and retries.
     *
     * @param events the EventManager instance that manages the events
     * @param ui the UI instance used to display messages to the user
     * @param participants the ParticipantManager instance that manages participants
     * @return the current user if logged in, or null if login is unsuccessful
     * @throws SyncException if an error occurs during login
     */
    private Participant ensureUserLoggedIn(EventManager events, UI ui, ParticipantManager participants)
            throws SyncException {
        Participant user = participants.getCurrentUser();
        if (user == null) {
            ui.showMessage("No user logged in. Do you want to log in?(yes/no)");
            if (ui.readLine().equalsIgnoreCase("yes")) {
                new LoginCommand().execute(events, ui, participants);
                return participants.getCurrentUser(); // Try again after login
            } else {
                return null;
            }
        }
        return user;
    }

    /**
     * Retrieves the list of events assigned to the current user.
     *
     * @param events the EventManager instance that manages the events
     * @param user the current participant
     * @return a list of events assigned to the user
     */
    private List<Event> getUserEvents(EventManager events, Participant user) {
        return events.getEvents().stream()
                .filter(event -> event.hasParticipant(user))
                .collect(Collectors.toList());
    }

    /**
     * Chooses the sorting strategy based on the specified sort type.
     *
     * @param ui the UI instance used to display messages to the user
     * @return the chosen sorting strategy, or null if the sort type is unknown
     */
    private Sort chooseSortStrategy(UI ui) {
        switch (sortType) {
        case "priority":
            return new SortByPriority();
        case "start":
            return new SortByStartTime();
        case "end":
            return new SortByEndTime();
        default:
            ui.showMessage("Unknown sort type. Showing unsorted list.");
            return null;
        }
    }

    /**
     * Displays the sorted events to the user.
     *
     * @param ui the UI instance used to display messages to the user
     * @param events the list of events to display
     * @param sorter the sorting strategy used to sort the events
     */
    private void displaySortedEvents(UI ui, List<Event> events, Sort sorter) {
        ArrayList<Event> sortedEvents = new ArrayList<>(events);
        ArrayList<String> priorities = new ArrayList<>(Priority.getAllPriorities());
        sorter.sort(sortedEvents, priorities);

        if (sortedEvents.isEmpty()) {
            ui.showMessage("No events to display.");
        } else {
            for (int i = 0; i < sortedEvents.size(); i++) {
                ui.showEventWithIndex(sortedEvents.get(i), i + 1, priorities.get(i));
            }
        }
    }
}
