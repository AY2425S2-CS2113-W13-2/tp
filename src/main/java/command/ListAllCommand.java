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

public class ListAllCommand extends Command {
    private final String sortType;
    private final UI ui;

    public ListAllCommand(String sortType, UI ui) {
        this.sortType = sortType.toLowerCase();
        this.ui = ui;
    }

    @Override
    public void execute(EventManager events, UI ui, ParticipantManager participants) throws SyncException {
        Participant currentUser = participants.getCurrentUser();
        if (currentUser == null) {
            ui.showMessage("No user logged in. Do you want to log in?");
            if(ui.readLine().equalsIgnoreCase("yes")) {
                new LoginCommand().execute(events, ui, participants);
            } else {
                throw new SyncException("No user logged in. Please enter 'login' to log in " +
                        "or 'create' to create a new user and then log in.");
            }
        }

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
            sequence =null;
            ui.showMessage("Unknown sort type. Showing unsorted list.");
            break;
        }

        if (sequence != null) {
            sequence.sort(eventList, Priority.getAllPriorities());
        }
        events.viewEvents(eventList);
    }
}
