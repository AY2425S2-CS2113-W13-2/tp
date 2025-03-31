package command;

import event.Event;
import event.EventManager;
import ui.UI;
import label.Priority;
import sort.Sort;
import sort.SortByPriority;
import sort.SortByStartTime;
import sort.SortByEndTime;

import java.util.ArrayList;
import java.util.Scanner;

public class ListCommand extends Command {

    @Override
    public void execute(EventManager events, UI ui) {
        Scanner scanner = new Scanner(System.in);
        ui.showMessage("How would you like to sort the events?");
        ui.showMessage("Options: Priority / Start Time / End Time");
        ui.showMessage("Enter sort type: ");
        String input = scanner.nextLine().trim().toLowerCase();

        Sort sortStrategy;
        if (input.startsWith("priority")) {
            sortStrategy = new SortByPriority();
        } else if (input.startsWith("start")) {
            sortStrategy = new SortByStartTime();
        } else if (input.startsWith("end")) {
            sortStrategy = new SortByEndTime();
        } else {
            ui.showMessage("Unknown sort type. Showing unsorted list.");
            events.viewAllEvents();
            return;
        }

        ArrayList<Event> eventCopies = new ArrayList<>(events.getEvents());
        ArrayList<String> priorityCopies = new ArrayList<>(Priority.getAllPriorities());

        sortStrategy.sort(eventCopies, priorityCopies);

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
