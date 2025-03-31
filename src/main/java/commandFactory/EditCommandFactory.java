package commandFactory;

import java.util.Scanner;

import command.Command;
import command.EditEventCommand;
import exception.SyncException;

public class EditCommandFactory implements CommandFactory {
    public Command createCommand() throws SyncException {
        int index = readEditEventIndex();
        return new EditEventCommand(index);
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
