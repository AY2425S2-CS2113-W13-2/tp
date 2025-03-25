package command;

import event.EventManager;
import ui.UI;

public class ListCommand extends Command {
    public void execute(EventManager events, UI ui) {
        events.viewAllEvents();
    }
}
