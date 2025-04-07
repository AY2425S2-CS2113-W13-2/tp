package commandfactory;

import command.AddParticipantCommand;
import event.Event;
import event.EventManager;
import participant.Participant;
import participant.ParticipantManager;
import exception.SyncException;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.logging.Level;
import ui.UI;

/**
 * Factory class for creating AddParticipantCommand objects.
 * This factory handles the creation of commands that add participants to events in the system.
 * It ensures proper user authorization and provides necessary information to guide the selection process.
 */
public class AddParticipantCommandFactory implements CommandFactory {
    private static final Logger LOGGER = Logger.getLogger(AddParticipantCommandFactory.class.getName());
    private final ParticipantManager participantManager;
    private final UI ui;
    private final EventManager eventManager;

    /**
     * Constructs a new AddParticipantCommandFactory with the specified dependencies.
     *
     * @param eventManager the manager handling event data
     * @param participantManager the manager handling participant data and authentication
     * @param ui the user interface for displaying information and collecting input
     */
    public AddParticipantCommandFactory(EventManager eventManager, ParticipantManager participantManager, UI ui) {
        this.participantManager = participantManager;
        this.ui = ui;
        this.eventManager = eventManager;
        LOGGER.info("AddParticipantCommandFactory initialized");
    }

    /**
     * Creates an AddParticipantCommand after validating user authorization and collecting
     * the necessary information about which participant to add to which event.
     *
     * @return a new AddParticipantCommand containing the information needed to add a participant to an event
     * @throws SyncException if the user is not logged in, doesn't have admin privileges,
     *                       or if there are issues with the selection input
     */
    @Override
    public AddParticipantCommand createCommand() throws SyncException {
        LOGGER.info("Creating AddParticipantCommand");

        Participant participant = participantManager.getCurrentUser();
        assert participantManager != null : "ParticipantManager should not be null";

        if (participant == null) {
            LOGGER.warning("Command creation failed: User not logged in");
            throw new SyncException("You are not logged in. Enter 'login' to log in first.");
        }

        checkAdminPrivileges();
        showAllEvents();
        showAllParticipants();

        String[] input = ui.splitAddParticipantCommandInput();
        assert input != null : "Command input should not be null";
        assert input.length >= 2 : "Command input should have at least 2 parts";

        int eventIndex;
        try {
            eventIndex = Integer.parseInt(input[0].trim()) - 1;
            assert eventIndex >= 0 : "Event index should not be negative";
            assert eventIndex < eventManager.getEvents().size() : "Event index should be within range";
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid event number format: {0}", input[0]);
            throw new SyncException("❌ Invalid event number. Please enter 'addparticipant' and try again.");
        }

        String participantName = input[1].trim();
        assert !participantName.isEmpty() : "Participant name should not be empty";

        LOGGER.log(Level.INFO, "Creating command to add participant {0} to event index {1}",
                new Object[]{participantName, eventIndex});

        return new AddParticipantCommand(
                eventIndex,
                participantName,
                ui,
                participantManager
        );
    }

    /**
     * Displays all available participants in the system.
     * If no participants are available, displays an appropriate message.
     */
    private void showAllParticipants() {
        LOGGER.info("Displaying all available participants");
        ArrayList<Participant> participants = participantManager.getParticipants();
        assert participants != null : "Participants list should not be null";

        if (participants.isEmpty()) {
            ui.showMessage("No participants available.");
            return;
        }

        ui.showMessage("Available Participants:");
        for (Participant participant : participants) {
            ui.showMessage("- " + participant.getName());
        }
    }

    /**
     * Verifies that the current user has admin privileges.
     *
     * @throws SyncException if the current user does not have admin privileges
     */
    private void checkAdminPrivileges() throws SyncException {
        LOGGER.info("Checking admin privileges");
        if (!participantManager.isCurrentUserAdmin()) {
            LOGGER.warning("Command creation failed: User is not an admin");
            throw new SyncException("Only ADMIN users can add participants. Please 'logout' " +
                    "and 'login' to an ADMIN user");
        }
    }

    /**
     * Displays all available events in the system.
     * If no events are available, displays an appropriate message.
     */
    private void showAllEvents() {
        LOGGER.info("Displaying all available events");
        ArrayList<Event> events = eventManager.getEvents();
        assert events != null : "Events list should not be null";

        if (events.isEmpty()) {
            ui.showMessage("No events available.");
            return;
        }

        ui.showMessage("Available Events:");
        for (int i = 0; i < events.size(); i++) {
            ui.showMessage((i + 1) + ". " + events.get(i).getName());
        }
    }
}
