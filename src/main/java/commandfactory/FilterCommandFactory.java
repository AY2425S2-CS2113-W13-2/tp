package commandfactory;

import command.Command;
import command.FilterCommand;
import exception.SyncException;
import label.Priority;
import participant.Participant;
import ui.UI;
import participant.ParticipantManager;

public class FilterCommandFactory implements CommandFactory{
    private final ParticipantManager participantManager;
    private final UI ui;

    public FilterCommandFactory(ParticipantManager participantManager, UI ui) {
        this.participantManager = participantManager;
        this.ui = ui;
    }

    public Command createCommand() throws SyncException {
        Participant participant = participantManager.getCurrentUser();

        if (participant == null) {
            throw new SyncException("You are not logged in. Enter 'login' to log in first.");
        }

        //logger.info("Creating filter command.");
        String input = ui.readFilterInput();
        //logger.fine("Input for filter event: " + input);

        if (input == null) {
            throw new SyncException("Input string should not be null");
        }

        input = input.trim();

        if (input.trim().isEmpty()) {
            throw new SyncException("Input string should not be empty");
        }

        String[] stringParts = input.split(" ");
        if (stringParts.length == 0) {
            throw new SyncException("Input string should not be empty");
        }

        if ((stringParts.length != 1) && (stringParts.length != 2)) {
            //logger.warning("Invalid number of parts in input: " + stringParts.length);
            throw new SyncException("Please provide one or two priority levels (e.g.,'LOW', 'LOW MEDIUM')");
        }

        try {
            String lowerPriority = stringParts[0].toUpperCase();
            String upperPriority;
            if ((stringParts.length == 1)) {
                upperPriority = lowerPriority;
            } else {
                upperPriority = stringParts[1].toUpperCase();
            }

            if (!Priority.isValid(lowerPriority) || !Priority.isValid(upperPriority)) {
                throw new SyncException(SyncException.invalidPriorityFilterErrorMessage());
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
