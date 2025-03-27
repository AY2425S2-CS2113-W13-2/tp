package command;

import event.EventManager;
import ui.UI;
import label.Priority;
import sort.Sort;
import sort.SortByPriority;
import sort.SortByStartTime;
import sort.SortByEndTime;

import java.util.Scanner;

public class ListCommand extends Command {
    public void execute(EventManager events, UI ui) {
        Scanner scanner = new Scanner(System.in);

        ui.showMessage("How would you like to sort the events?");
        ui.showMessage("Options: Priority / Start Time / End Time");
        ui.showMessage("Enter sort type: ");
        String input = scanner.nextLine().trim().toLowerCase().split(" ")[0];

        Sort sequence;

        switch (input) {
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
            events.viewAllEvents();
            return;
        }

        sequence.sort(events.getEvents(), Priority.getAllPriorities());

        events.viewAllEvents();
    }
}
