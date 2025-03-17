package Command;

import Event.EventManager;
import UI.UI;

public class ListCommand extends Command {
    public void execute(EventManager events, UI ui) {
        events.viewAllEvents();
    }
}
