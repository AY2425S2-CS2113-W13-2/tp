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

/**
 * Represents a command to add a participant to an event.
 * An {@code AddParticipantCommand} allows assigning a participant
 * to a specific event by index, creating the participant if needed.
 */
public class AddParticipantCommand extends Command {
    private final int eventIndex;
    private final String participantName;
    private final UI ui;
    private final ParticipantManager participantManager;

    /**
     * Constructs an {@code AddParticipantCommand} with the given event index,
     * participant name, user interface, and participant manager.
     *
     * @param eventIndex the index of the event to add the participant to.
     * @param participantName the name of the participant to be added.
     * @param ui the user interface for interaction.
     * @param participantManager the manager handling participants.
     */
    public AddParticipantCommand(int eventIndex, String participantName, UI ui, ParticipantManager participantManager) {
        this.eventIndex = eventIndex;
        this.participantName = participantName;
        this.ui = ui;
        this.participantManager = participantManager;
    }

    /**
     * Executes the command to add a participant to the specified event.
     * If the participant does not exist, prompts the user to create one.
     * Assigns the participant to the event if they are available.
     *
     * @param eventManager the manager handling the list of events.
     * @param ui the user interface used for interaction.
     * @param participantManager the manager handling participant data.
     * @throws SyncException if there is an error during execution or saving data.
     */
    public void execute(EventManager eventManager, UI ui, ParticipantManager participantManager) throws SyncException {
        Event event = eventManager.getEvent(eventIndex);
        ui.showMessage("Event Index: " + eventIndex);
        ui.showMessage("Event Start Time: " + event.getStartTime());
        ui.showMessage("Event End Time: " + event.getEndTime());

        Participant participant = participantManager.getParticipant(participantName);

        if (participant == null) {
            boolean shouldCreate = ui.askConfirmation(
                    "Participant '" + participantName + "' does not exist. Create a new one? (Y/N)"
            );

            if (!shouldCreate) {
                ui.showMessage("Operation cancelled.");
                return;
            }

            CommandFactory factory = new CreateUserCommandFactory(this.ui, this.participantManager);
            Command cmd = factory.createCommand();
            cmd.execute(eventManager, ui, participantManager);

            participant = participantManager.getParticipant(participantName);
        }

        boolean isAvailable = participantManager.checkParticipantAvailability(event, participant);
        if (isAvailable) {
            boolean assigned = participantManager.assignParticipant(event, participant);
            if (assigned) {
                event.addParticipant(participant);
                ui.showMessage("Participant " + participant.getName() + " has been added.");
            } else {
                ui.showMessage("Failed to assign time slot. Enter 'addparticipant' to try again.");
            }
        } else {
            ui.showMessage("Participant " + participant.getName() + " is unavailable during the event." +
                    "Enter 'addparticipant' to try again or try other features.");
        }

        eventManager.getStorage().saveEvents(eventManager.getEvents(), Priority.getAllPriorities());
    }

    /**
     * Returns the name of the participant to be added.
     *
     * @return the participant name.
     */
    public String getParticipantName() {
        return participantName;
    }

    /**
     * Returns the index of the event to which the participant should be added.
     *
     * @return the event index.
     */
    public int getEventIndex() {
        return eventIndex;
    }
}
