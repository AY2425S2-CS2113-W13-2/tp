package commandfactory;

import java.util.logging.Logger;

import command.Command;
import command.HelpCommand;
import command.LoginCommand;

/**
 * Factory class responsible for creating a HelpCommand.
 * This factory creates a command that provides help or guidance about available commands.
 */
public class HelpCommandFactory implements CommandFactory {
    private static final Logger LOGGER = Logger.getLogger(LoginCommand.class.getName());


    /**
     * Creates a HelpCommand to provide help information.
     *
     * @return A new HelpCommand that provides help or guidance about available commands
     */
    public Command createCommand() {
        LOGGER.info("Attempting FilterCommandFactory");
        return new HelpCommand();
    }
}
