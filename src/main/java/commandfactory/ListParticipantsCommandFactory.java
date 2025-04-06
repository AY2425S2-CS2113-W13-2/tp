package commandfactory;

import command.Command;
import command.ListParticipantsCommand;
import exception.SyncException;
import parser.CommandParser;
import ui.UI;

public class ListParticipantsCommandFactory implements CommandFactory {
    private final UI ui;

    public ListParticipantsCommandFactory(UI ui) {
        this.ui = ui;
    }

    public Command createCommand() throws SyncException {
        ui.showMessage("Enter event index to list participants:");
        String input = ui.readLine();
        try {
            int eventIndex = Integer.parseInt(input.trim()) - 1;
            return new ListParticipantsCommand(eventIndex);
        } catch (NumberFormatException e) {
            throw new SyncException("Invalid event index. Please enter a number.");
        }
    }

}
