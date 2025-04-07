package commandfactory;

import command.Command;
import command.FilterCommand;
import exception.SyncException;
import label.Priority;
import participant.Participant;
import ui.UI;
import participant.ParticipantManager;

/**
 * Factory class responsible for creating a FilterCommand.
 * This factory ensures the user is logged in and creates a command to filter events
 * based on priority levels.
 */
public class FilterCommandFactory implements CommandFactory {
    private final ParticipantManager participantManager;
    private final UI ui;

    /**
     * Constructor to initialize the factory with participant manager and UI.
     *
     * @param participantManager The participant manager to handle participant data
     * @param ui The UI used to interact with the user
     */
    public FilterCommandFactory(ParticipantManager participantManager, UI ui) {
        this.participantManager = participantManager;
        this.ui = ui;
    }

    /**
     * Creates a FilterCommand based on the user's input.
     * The command filters events according to the specified priority levels.
     * It validates the input to ensure the user is logged in and the provided priority levels are valid.
     *
     * @return A new FilterCommand to filter events by priority
     * @throws SyncException If an error occurs during command creation, such as invalid input or the user not being logged in
     */
    public Command createCommand() throws SyncException {
        Participant participant = participantManager.getCurrentUser();

        if (participant == null) {
            throw new SyncException("You are not logged in. Enter 'login' to log in first.");
        }

        // Read and validate the input for filter criteria
        String input = ui.readFilterInput();

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
            throw new SyncException("Please provide one or two priority levels (e.g., 'LOW', 'LOW MEDIUM')");
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
            throw new SyncException(SyncException.invalidBoundErrorMessage());
        }
    }
}
