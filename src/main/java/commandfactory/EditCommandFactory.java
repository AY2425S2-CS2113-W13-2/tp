package commandfactory;

import java.util.Scanner;

import command.Command;
import command.EditEventCommand;
import exception.SyncException;
import participant.ParticipantManager;

public class EditCommandFactory implements CommandFactory {
    private final ParticipantManager participantManager;

    public EditCommandFactory(ParticipantManager participantManager) {
        this.participantManager = participantManager;
    }

    public Command createCommand() throws SyncException {
        if (participantManager.getCurrentUser() == null) {
            throw new SyncException("You are not logged in. Please enter 'login' to login.");
        } else if (!participantManager.isCurrentUserAdmin()) {
            throw new SyncException("Only admin can edit events!");
        } else {
            int index = readEditEventIndex();
            return new EditEventCommand(index);
        }
    }

    private int readEditEventIndex() throws SyncException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter event index to edit: ");
        try {
            int index = Integer.parseInt(scanner.nextLine()) - 1;
            return index;
        } catch (Exception e) {
            throw new SyncException(SyncException.invalidEventDetailsErrorMessage());
        }
    }
}
