package command;
import event.Event;
import event.EventManager;
import participant.ParticipantManager;
import ui.UI;
import exception.SyncException;

public class AddEventCommand extends Command {
    private final Event event;

    public AddEventCommand(Event event) {
        this.event = event;
    }

    public void execute(EventManager events, UI ui, ParticipantManager participantManager) throws SyncException {
        events.addEvent(event, participantManager);
    }

    public Event getEvent() {
        return this.event;
    }
}
