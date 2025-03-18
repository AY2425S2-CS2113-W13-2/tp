import java.util.ArrayList;
import java.util.Scanner;
import event.Event;
import event.EventManager;

public class UI {
    public final Scanner scanner = new Scanner(System.in);

    public void showMessage(String message) {
        System.out.println(message);
    }

    public void showEventList(EventManager eventManager) {
        for (int i = 0; i < eventManager.getEvents().size(); i++) {
            System.out.println((i + 1) + ". " + eventManager.getEvents().get(i));
        }
    }

    public void printMatchingEvents(ArrayList<Event> events) {
        if (events.isEmpty()){
            System.out.println("No matching tasks found.");
        } else {
            System.out.println("Found " + events.size() + " matching events.");
            System.out.println("Here are the matching events in your list: ");
            for (int i = 0; i < events.size(); i++) {
                System.out.println(" " + (i + 1) + ". " + events.get(i).toString());
            }
        }
    }

    public void showCollisionWarning(Event newEvent, ArrayList<Event> collisions) {
        System.out.println("Warning: Scheduling Conflict");
        System.out.println("Time of the new event overlaps with the following event(s):");
        for (Event collision : collisions) {
            System.out.println(collision.toString());
        }
        System.out.println("Please edit your events to resolve the conflict.");
    }
}
