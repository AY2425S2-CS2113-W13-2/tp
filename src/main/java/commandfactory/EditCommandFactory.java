package commandfactory;

import java.util.Scanner;

import command.Command;
import command.EditEventCommand;
import event.EventManager;
import exception.SyncException;
import participant.ParticipantManager;

public class EditCommandFactory implements CommandFactory {
    private final ParticipantManager participantManager;
    private final EventManager eventManager;

    public EditCommandFactory(ParticipantManager participantManager, EventManager eventManager) {
        this.participantManager = participantManager;
        this.eventManager = eventManager;
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
        System.out.println("\n📅 Here are the available events to edit:\n");
        eventManager.viewAllEvents();

        int index = readEditEventIndex();
        return new EditEventCommand(index);
    }

    private int readEditEventIndex() throws SyncException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("\nEnter event index to edit: ");
        try {
            int index = Integer.parseInt(scanner.nextLine().trim()) - 1;
            return index;
        } catch (Exception e) {
            throw new SyncException(SyncException.invalidEventDetailsErrorMessage());
        }
    }
}
