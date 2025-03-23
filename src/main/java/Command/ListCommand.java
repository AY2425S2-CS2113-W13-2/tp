package command;

import event.EventManager;
import ui.UI;

public class ListCommand extends Command {
    private final String primary;
    private final String secondary;

    public ListCommand(String primary, String secondary) {
        this.primary = primary;
        this.secondary = secondary;
    }

    public void execute(EventManager events, UI ui) {
        events.viewAllEventsSorted(primary, secondary);
    }
}
