package command;

import commandfactory.CommandFactory;
import commandfactory.CreateUserCommandFactory;
import event.Event;
import event.EventManager;
import participant.Participant;
import participant.ParticipantManager;
import exception.SyncException;
import ui.UI;
import label.Priority;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Represents a command to add a participant to an existing event.
 * This command handles adding participants to events, including creating new participants
 * if they don't exist in the system.
 */
public class AddParticipantCommand extends Command {
    private final int eventIndex;
    private final String participantName;
    private final UI ui;
    private final ParticipantManager participantManager;
    private static final Logger LOGGER = Logger.getLogger(AddParticipantCommand.class.getName());

    /**
     * Constructs a new AddParticipantCommand with the specified parameters.
     *
     * @param eventIndex The index of the event to add the participant to
     * @param participantName The name of the participant to add
     * @param ui The user interface for interaction
     * @param participantManager The participant manager for participant operations
     * @throws AssertionError if participantName is null or empty, or if ui or participantManager are null
     */
    public AddParticipantCommand(int eventIndex, String participantName, UI ui, ParticipantManager participantManager) {
        this.eventIndex = eventIndex;
        this.participantName = participantName;
        this.ui = ui;
        this.participantManager = participantManager;

        assert participantName != null && !participantName.isEmpty() : "Participant name cannot be null or empty";
        assert ui != null : "UI cannot be null";
        assert participantManager != null : "ParticipantManager cannot be null";
        assert eventIndex >= 0 : "Event index must be non-negative";

        LOGGER.info("AddParticipantCommand created for event index: " + eventIndex +
                " and participant: " + participantName);
    }

    /**
     * Executes the command to add a participant to an event.
     * If the participant doesn't exist, prompts the user to create a new one.
     * Checks participant availability before adding them to the event.
     *
     * @param eventManager The event manager containing the events
     * @param ui The user interface for displaying messages
     * @param participantManager The participant manager for managing participants
     * @throws SyncException if there is a synchronization error during execution
     * @throws AssertionError if any of the parameters are null
     */
    public void execute(EventManager eventManager, UI ui, ParticipantManager participantManager) throws SyncException {
        LOGGER.info("Executing AddParticipantCommand for event index: " + eventIndex);

        assert eventManager != null : "EventManager cannot be null";
        assert ui != null : "UI cannot be null";
        assert participantManager != null : "ParticipantManager cannot be null";

        try {
            Event event = eventManager.getEvent(eventIndex);
            assert event != null : "Event not found at index: " + eventIndex;

            LOGGER.info("Retrieved event: " + event);
            ui.showMessage("Event Index: " + eventIndex);
            ui.showMessage("Event Start Time: " + event.getStartTime());
            ui.showMessage("Event End Time: " + event.getEndTime());

            Participant participant = participantManager.getParticipant(participantName);
            LOGGER.info("Participant lookup result: " + (participant != null ? "found" : "not found"));

            // 2. If not, ask if the user wants to create a new one
            if (participant == null) {
                boolean shouldCreate = ui.askConfirmation(
                        "Participant '" + participantName + "' does not exist. Create a new one? (Y/N)"
                );

                if (!shouldCreate) {
                    LOGGER.info("User cancelled participant creation");
                    ui.showMessage("Operation cancelled.");
                    return;
                }

                LOGGER.info("Creating new participant: " + participantName);
                CommandFactory factory = new CreateUserCommandFactory(this.ui, this.participantManager);
                Command cmd = factory.createCommand();
                cmd.execute(eventManager, ui, participantManager);

                // RELOAD the participant object from the manager
                participant = participantManager.getParticipant(participantName);
                assert participant != null : "Participant still null after creation attempt";
                LOGGER.info("New participant created: " + participant);
            }

            boolean isAvailable = participantManager.checkParticipantAvailability(event, participant);
            LOGGER.info("Participant availability check result: " + isAvailable);

            if (isAvailable) {
                boolean assigned = participantManager.assignParticipant(event, participant);
                if (assigned) {
                    event.addParticipant(participant);
                    LOGGER.info("Successfully added participant to event");
                    ui.showMessage("Participant " + participant.getName() + " has been added.");
                } else {
                    LOGGER.warning("Failed to assign time slot for participant");
                    ui.showMessage("Failed to assign time slot. Enter 'addparticipant' to try again.");
                }
            } else {
                LOGGER.warning("Participant unavailable during event time");
                ui.showMessage("Participant " + participant.getName() + " is unavailable during the event." +
                        "Enter 'addparticipant' to try again or try other features.");
            }

            // Persist updated event list to storage
            LOGGER.info("Saving updated events to storage");
            eventManager.getStorage().saveEvents(eventManager.getEvents(), Priority.getAllPriorities());

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error executing AddParticipantCommand", e);
            throw e;
        }
    }

    /**
     * Returns the name of the participant associated with this command.
     *
     * @return The participant name
     */
    public String getParticipantName() {
        return participantName;
    }

    /**
     * Returns the index of the event associated with this command.
     *
     * @return The event index
     */
    public int getEventIndex() {
        return eventIndex;
    }
}
