package command;
import event.Event;
import event.EventManager;
import ui.UI;
import exception.SyncException;

public class AddEventCommand extends Command {
    private final Event event;

    public AddEventCommand(Event event) {
        this.event = event;
    }

    public void execute(EventManager events, UI ui) throws SyncException {
        events.addEvent(event);
    }

    public Event getEvent() {
        return this.event;
    }
}
