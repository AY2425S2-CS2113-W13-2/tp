package commandfactory;

import command.Command;
import command.EditEventCommand;
import event.EventManager;
import exception.SyncException;
import participant.ParticipantManager;
import ui.UI;

public class EditCommandFactory implements CommandFactory {
    private final ParticipantManager participantManager;
    private final EventManager eventManager;
    private final UI ui;

    public EditCommandFactory(ParticipantManager participantManager, EventManager eventManager, UI ui) {
        this.participantManager = participantManager;
        this.eventManager = eventManager;
        this.ui = ui;
    }

    @Override
    public Command createCommand() throws SyncException {
        if (participantManager.getCurrentUser() == null) {
            throw new SyncException("You are not logged in. Please enter 'login' to login.");
        }

        if (!participantManager.isCurrentUserAdmin()) {
            throw new SyncException("Only admin can edit events!");
        }

        // Show event list before prompting for index
        ui.showMessage("\n📅 Here are the available events to edit:\n");
        eventManager.viewAllEvents();

        int index = readEditEventIndex();
        return new EditEventCommand(index, this.participantManager);
    }

    private int readEditEventIndex() throws SyncException {
        ui.showMessage("\nEnter event index to edit (or type 'exit' to cancel): ");
        if (!UI.scanner.hasNextLine()) {
            throw new SyncException("❌ No input received. Edit cancelled.");
        }

        String input = ui.readLine().trim().toLowerCase();

        ui.checkForExit(input);

        try {
            int index = Integer.parseInt(input) - 1;
            return index;
        } catch (NumberFormatException e) {
            throw new SyncException(SyncException.invalidEventIndexErrorMessage());
        }
    }

}
