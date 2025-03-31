package commandFactory;

import command.Command;
import command.FilterCommand;
import exception.SyncException;
import label.Priority;
import parser.CommandParser;

public class FilterCommandFactory implements CommandFactory{
    public Command createCommand() throws SyncException {
        //logger.info("Creating filter command.");
        String input = CommandParser.readFilterInput();
        //logger.fine("Input for filter event: " + input);

        assert input != null : "Input string should not be null";
        assert !input.trim().isEmpty() : "Input string should not be empty";

        String[] stringParts = input.split(" ");
        assert stringParts.length > 0 : "Split result should not be empty";

        if (stringParts.length != 2) {
            //logger.warning("Invalid number of parts in input: " + stringParts.length);
            throw new SyncException("Please provide two priority levels (e.g., 'LOW MEDIUM')");
        }

        try {
            String lowerPriority = stringParts[0].toUpperCase();
            String upperPriority = stringParts[1].toUpperCase();

            if (!Priority.isValid(lowerPriority) || !Priority.isValid(upperPriority)) {
                throw new SyncException(SyncException.invalidBoundErrorMessage());
            }

            int lower = Priority.getValue(lowerPriority);
            int upper = Priority.getValue(upperPriority);

            if (lower > upper) {
                throw new SyncException(SyncException.invalidBoundErrorMessage());
            }
            return new FilterCommand(lower, upper);
        } catch (SyncException e) {
            throw e;
        } catch (Exception e) {
            //logger.severe("Unexpected exception: " + e.getMessage());
            throw new SyncException(SyncException.invalidBoundErrorMessage());
        }
    }
}
