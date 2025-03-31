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
import java.util.stream.Collectors;

public class ListCommand extends Command {
    private final String sortType;

    public ListCommand(String sortType) {
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

        List<Event> userEvents = events.getEvents().stream()
                .filter(event -> event.hasParticipant(currentUser))
                .collect(Collectors.toList());

        if (userEvents.isEmpty()) {
            ui.showMessage("No events assigned to you.");
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
            events.viewEvents(userEvents);
            return;
        }

        sequence.sort(userEvents, Priority.getAllPriorities());
        events.viewEvents(userEvents);
        ArrayList<Event> eventCopies = new ArrayList<>(events.getEvents());
        ArrayList<String> priorityCopies = new ArrayList<>(Priority.getAllPriorities());

        sequence.sort(eventCopies, priorityCopies);

        if (eventCopies.isEmpty()) {
            ui.showMessage("No events to display.");
        } else {
            for (int i = 0; i < eventCopies.size(); i++) {
                Event event = eventCopies.get(i);
                String priority = priorityCopies.get(i);
                ui.showEventWithIndex(event, i + 1, priority);
            }
        }
    }
}
