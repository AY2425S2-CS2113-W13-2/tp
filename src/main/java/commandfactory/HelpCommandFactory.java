package commandfactory;

import command.Command;
import command.HelpCommand;

/**
 * Factory class responsible for creating a HelpCommand.
 * This factory creates a command that provides help or guidance about available commands.
 */
public class HelpCommandFactory implements CommandFactory {

    /**
     * Creates a HelpCommand to provide help information.
     *
     * @return A new HelpCommand that provides help or guidance about available commands
     */
    public Command createCommand() {
        return new HelpCommand();
    }
}
