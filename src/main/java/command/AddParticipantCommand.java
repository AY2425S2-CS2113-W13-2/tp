package command;

import commandFactory.CommandFactory;
import commandFactory.CreateUserCommandFactory;
import event.Event;
import event.EventManager;
import participant.Participant;
import participant.AvailabilitySlot;
import participant.ParticipantManager;
import exception.SyncException;
import ui.UI;
import label.Priority;
import java.util.ArrayList;


public class AddParticipantCommand extends Command {
    private final int eventIndex;
    private final String participantName;

    public AddParticipantCommand(int eventIndex, String participantName) {
        this.eventIndex = eventIndex;
        this.participantName = participantName;
    }

    public void execute(EventManager eventManager, UI ui, ParticipantManager participantManager) throws SyncException {
        Event event = eventManager.getEvent(eventIndex);
        System.out.println("Event Index : " + eventIndex);
        System.out.println("Event Start Time : " + event.getStartTime());
        System.out.println("Event End Time : " + event.getEndTime());

        Participant participant = participantManager.getParticipant(participantName);

        // 2. If not, ask if the user wants to create a new one
        if (participant == null) {
            boolean shouldCreate = ui.askConfirmation(
                    "Participant '" + participantName + "' does not exist. Create a new one? (Y/N)"
            );

            if (!shouldCreate) {
                ui.showMessage("Operation cancelled.");
                return;
            }

            CommandFactory factory = new CreateUserCommandFactory();
            Command cmd = factory.createCommand();
            cmd.execute(eventManager, ui, participantManager);
        }

        // 3. Check availability and add to event
        boolean isAvailable = participantManager.checkParticipantAvailability(event, participant);
        if (isAvailable) {
            event.addParticipant(participant);
            participant.assignEventTime(event.getStartTime(), event.getEndTime());
            ui.showMessage("Participant " + participant.getName() + "has been added.");
        } else {
            ui.showMessage("Participant " + participant.getName() + " is unavailable during the event.");
        }

        // Persist updated event list to storage
        eventManager.getStorage().saveEvents(eventManager.getEvents(), Priority.getAllPriorities());
    }
}
