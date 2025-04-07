package commandfactory;

import java.util.logging.Level;
import java.util.logging.Logger;

import command.Command;
import command.HelpCommand;

/**
 * Factory class for creating HelpCommand instances.
 * Responsible for generating a command that provides user guidance.
 */
public class HelpCommandFactory implements CommandFactory {
    private static final Logger LOGGER = Logger.getLogger(HelpCommandFactory.class.getName());

    /**
     * Creates a HelpCommand to display available commands and their usage.
     *
     * @return A new HelpCommand instance
     */
    public Command createCommand() {
        LOGGER.log(Level.INFO, "Creating help command");

        try {
            HelpCommand helpCommand = new HelpCommand();

            // Optional assertion to validate help command creation
            assert helpCommand != null : "Help command creation failed";

            return helpCommand;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error creating help command", e);
            throw new RuntimeException("Failed to create help command", e);
        }
    }
}
