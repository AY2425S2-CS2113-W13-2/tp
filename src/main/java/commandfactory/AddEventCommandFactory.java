package commandfactory;

import java.time.LocalDateTime;
import java.util.logging.Logger;
import java.util.logging.Level;

import command.AddEventCommand;
import command.Command;
import exception.SyncException;
import parser.CommandParser;
import participant.ParticipantManager;
import ui.UI;
import event.Event;

/**
 * Factory class for creating AddEventCommand objects.
 * This factory handles the creation of commands that add new events to the system.
 * It ensures the current user has proper authorization and collects all necessary event information.
 */
public class AddEventCommandFactory implements CommandFactory {
    private static final Logger LOGGER = Logger.getLogger(AddEventCommandFactory.class.getName());
    private final ParticipantManager participantManager;
    private final UI ui;

    /**
     * Constructs a new AddEventCommandFactory with the specified participant manager and UI.
     *
     * @param participantManager the manager handling participant data and authentication
     * @param ui the user interface for collecting input
     */
    public AddEventCommandFactory(ParticipantManager participantManager, UI ui) {
        this.participantManager = participantManager;
        this.ui = ui;
        LOGGER.info("AddEventCommandFactory initialized");
    }

    /**
     * Creates an AddEventCommand after collecting and validating all necessary event information.
     * Ensures the current user is logged in and has admin privileges before proceeding.
     *
     * @return a new AddEventCommand containing the event to be added
     * @throws SyncException if the user is not logged in or doesn't have admin privileges,
     *                       or if there are issues with the event data
     */
    @Override
    public Command createCommand() throws SyncException {
        LOGGER.info("Creating AddEventCommand");

        if (participantManager.getCurrentUser() == null) {
            LOGGER.warning("Command creation failed: User not logged in");
            throw new SyncException("You are not logged in. Please enter 'login' to login.");
        } else if (!participantManager.isCurrentUserAdmin()) {
            LOGGER.warning("Command creation failed: User is not an admin");
            throw new SyncException("Only admin can create events!");
        } else {
            LOGGER.info("User authorized to create events, collecting event details");
            String input = ui.readAddCommandInput();
            String[] parts = CommandParser.splitAddCommandInput(input);

            String name = parts[0].trim();
            LocalDateTime startTime = CommandParser.parseDateTime(parts[1]);
            LocalDateTime endTime = CommandParser.parseDateTime(parts[2]);

            // Validate event time data
            assert startTime != null : "Start time should not be null";
            assert endTime != null : "End time should not be null";
            assert !endTime.isBefore(startTime) : "End time should not be before start time";

            String location = parts[3].trim();
            String description = parts[4].trim();

            // Validate event text data
            assert !name.isEmpty() : "Event name should not be empty";
            assert !location.isEmpty() : "Event location should not be empty";
            assert !description.isEmpty() : "Event description should not be empty";

            LOGGER.log(Level.INFO, "Creating new event: {0} at {1}", new Object[]{name, location});
            Event newEvent = new Event(name, startTime, endTime, location, description);
            LOGGER.info("AddEventCommand created successfully");
            return new AddEventCommand(newEvent);
        }
    }
}
