package command;
import event.*;
import ui.UI;
import exception.*;

public class DuplicateCommand extends Command {
    private final Event eventToDuplicate;
    private final String newName;

    public DuplicateCommand(Event eventToDuplicate, String newName) {
        this.eventToDuplicate = eventToDuplicate;
        this.newName = newName;
    }

    public void execute(EventManager events, UI ui) throws SyncException {
        events.duplicateEvent(eventToDuplicate, newName);
        Event duplicatedEvent = eventToDuplicate.duplicate(newName);
        ui.showMessage("Event duplicated: " + duplicatedEvent.toString());
    }
}
