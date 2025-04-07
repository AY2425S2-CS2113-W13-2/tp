package commandfactory;

import command.Command;
import exception.SyncException;

/**
 * Interface for factories that create Command instances.
 * Implementations of this interface are responsible for creating specific
 * Command objects that can be executed in the system.
 */
public interface CommandFactory {

    /**
     * Creates a new Command instance that can be executed.
     *
     * @return the Command object to be executed
     * @throws SyncException if there is an error during command creation
     */
    Command createCommand() throws SyncException;
}
