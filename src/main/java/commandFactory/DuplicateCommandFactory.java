package commandFactory;

import command.Command;
import command.DuplicateCommand;
import event.Event;
import event.EventManager;
import exception.SyncException;
import participant.ParticipantManager;

public class DuplicateCommandFactory implements CommandFactory {
    private final ParticipantManager participantManager;
    private final ui.UI ui;
    private final EventManager eventManager;

    public DuplicateCommandFactory(ParticipantManager participantManager, ui.UI ui, EventManager eventManager) {
        this.participantManager = participantManager;
        this.ui = ui;
        this.eventManager = eventManager;
    }

    public Command createCommand() throws SyncException {
        String input = ui.readDuplicateEventInput();
        String[] parts = input.split(" ", 2);

        if (parts.length < 2) {
            throw new SyncException("Invalid duplicate command format. Use: duplicate index New Event Name");
        }

        try {
            int index = Integer.parseInt(parts[0]) - 1;
            if (index >= 0 && index < eventManager.getEvents().size()) {
                Event eventToDuplicate = eventManager.getEvents().get(index);
                String newName = parts[1];
                return new DuplicateCommand(eventToDuplicate, newName);
            } else {
                throw new SyncException("Invalid event index.");
            }
        } catch (NumberFormatException e) {
            throw new SyncException("Invalid index format. Use a number.");
        }
    }
}
