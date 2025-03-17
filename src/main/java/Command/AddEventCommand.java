package command;
import seedu.event.Event;
import seedu.event.EventManager;
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
}
