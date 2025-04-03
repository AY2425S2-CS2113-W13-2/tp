package command;

import event.Event;
import event.EventManager;
import exception.SyncException;
import parser.CommandParser;
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

public class ListAllCommand extends Command {
    private final String sortType;

    public ListAllCommand(String sortType) {
        this.sortType = sortType.toLowerCase();
    }

    @Override
    public void execute(EventManager events, UI ui, ParticipantManager participants) throws SyncException {
        Participant currentUser = participants.getCurrentUser();
        if (currentUser == null) {
            ui.showMessage("No user logged in. Do you want to log in?");
            if(CommandParser.readInput().equalsIgnoreCase("yes")) {
                new LoginCommand().execute(events, ui, participants);
            } else {
                return;
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
            ui.showMessage("Unknown sort type. Showing unsorted list.");

            return;
        }

        sequence.sort(eventList, Priority.getAllPriorities());
        events.viewEvents(eventList);
    }
}
