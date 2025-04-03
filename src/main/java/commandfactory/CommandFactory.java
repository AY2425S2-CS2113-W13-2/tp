package commandfactory;

import command.Command;
import exception.SyncException;

public interface CommandFactory {
    Command createCommand() throws SyncException;
}
