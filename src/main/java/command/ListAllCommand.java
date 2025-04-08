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

/**
 * Represents a command that lists all events for the current user, optionally sorted by a specified criterion.
 * This command requires the user to be an Admin.
 */
public class ListAllCommand extends Command {
    private static final Logger LOGGER = Logger.getLogger(ListAllCommand.class.getName());
    private final String sortType;
    private final UI ui;

    /**
     * Constructs a ListAllCommand with the specified sorting criterion.
     *
     * @param sortType the type of sorting ('priority', 'start', or 'end')
     * @param ui the UI instance used to display messages and interact with the user
     */
    public ListAllCommand(String sortType, UI ui) {
        this.sortType = sortType.toLowerCase();
        this.ui = ui;
    }

    /**
     * Executes the command to list all events for the current
     * user, optionally sorting them based on the given criterion.
     *
     * @param events the EventManager instance that manages the events
     * @param ui the UI instance used to display messages to the user
     * @param participants the ParticipantManager instance that manages participants
     * @throws SyncException if no user is logged in, or if the user is not an admin, or if an error occurs
     */
    @Override
    public void execute(EventManager events, UI ui, ParticipantManager participants) throws SyncException {
        assert sortType != null : "Sort type cannot be null";
        LOGGER.info("Attempting to list all events with sort type: " + sortType);
        Participant currentUser = participants.getCurrentUser();

        // Check if user is logged in
        if (currentUser == null) {
            ui.showMessage("No user logged in. Do you want to log in?");
            if (ui.readLine().equalsIgnoreCase("yes")) {
                new LoginCommand().execute(events, ui, participants);
            } else {
                throw new SyncException("No user logged in. Please enter 'login' to log in " +
                        "or 'create' to create a new user and then log in.");
            }
        }

        // Check if user has admin access
        if (currentUser.getAccessLevel() != Participant.AccessLevel.ADMIN) {
            ui.showMessage("Sorry, you need to be an ADMIN to access all events.");
            return;
        }

        List<Event> eventList = new ArrayList<>(events.getEvents());

        if (events.size() == 0) {
            ui.showMessage("No events in the system.");
            return;
        }

        Sort sequence;
        switch (sortType) {
        case "priority":
            sequence = new SortByPriority();
            break;
        case "start":
            sequence = new SortByStartTime();
            break;
        case "end":
            sequence = new SortByEndTime();
            break;
        default:
            sequence = null;
            ui.showMessage("Unknown sort type. Showing unsorted list.");
            break;
        }

        if (sequence != null) {
            sequence.sort(eventList, Priority.getAllPriorities());
        }

        // Display events
        events.viewEvents(eventList);
    }
}
