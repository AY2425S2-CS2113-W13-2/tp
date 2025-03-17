package Command;
import Event.*;
import UI.UI;
import Exception.*;

public class AddEventCommand extends Command {
    private final Event event;

    public AddEventCommand(Event event) {
        this.event = event;
    }

    public void execute(EventManager events, UI ui) throws SyncException {
        events.addEvent(event);
    }
}