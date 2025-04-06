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
import java.util.stream.Collectors;

public class ListCommand extends Command {
    private final String sortType;

    public ListCommand(String sortType) {
        this.sortType = sortType.toLowerCase();
    }

    @Override
    public void execute(EventManager events, UI ui, ParticipantManager participants) throws SyncException {
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

    private Participant ensureUserLoggedIn(EventManager events, UI ui, ParticipantManager participants)
            throws SyncException {
        Participant user = participants.getCurrentUser();
        if (user == null) {
            ui.showMessage("No user logged in. Do you want to log in?");
            if (ui.readLine().equalsIgnoreCase("yes")) {
                new LoginCommand().execute(events, ui, participants);
                return participants.getCurrentUser(); // Try again after login
            } else {
                return null;
            }
        }
        return user;
    }

    private List<Event> getUserEvents(EventManager events, Participant user) {
        return events.getEvents().stream()
                .filter(event -> event.hasParticipant(user))
                .collect(Collectors.toList());
    }

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
