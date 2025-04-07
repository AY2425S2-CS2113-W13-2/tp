package commandfactory;

import java.util.logging.Level;
import java.util.logging.Logger;

import command.Command;
import command.FindCommand;
import exception.SyncException;

/**
 * Factory class for creating FindCommand instances.
 * Handles the creation of commands to find events based on a keyword.
 */
public class FindCommandFactory implements CommandFactory {
    private static final Logger LOGGER = Logger.getLogger(FindCommandFactory.class.getName());

    private final String keyword;

    /**
     * Constructs a FindCommandFactory with the specified search keyword.
     *
     * @param keyword The keyword to search for in events
     */
    public FindCommandFactory(String keyword) {
        this.keyword = keyword;
        LOGGER.log(Level.FINE, "FindCommandFactory initialized with keyword: {0}", keyword);
    }

    /**
     * Creates a FindCommand after validating the keyword.
     *
     * @return FindCommand with the specified keyword
     * @throws SyncException if the keyword is invalid
     */
    public Command createCommand() throws SyncException {
        // Validate keyword
        assert keyword != null : "Keyword should not be null";
        assert !keyword.isEmpty() : "Keyword should not be empty";

        // Log the find command creation
        LOGGER.log(Level.INFO, "Creating find command with keyword: {0}", keyword);

        try {
            // Validate keyword length (optional, adjust as needed)
            if (keyword.length() > 100) {  // Example max length
                LOGGER.log(Level.WARNING, "Keyword too long: {0} characters", keyword.length());
                throw new SyncException("Search keyword is too long.");
            }

            // Create and return the FindCommand
            return new FindCommand(keyword);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error creating find command", e);
            throw new SyncException("Error creating find command: " + e.getMessage());
        }
    }
}
