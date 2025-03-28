package command;

import event.Event;
import event.EventManager;
import participant.Participant;
import participant.AvailabilitySlot;
import exception.SyncException;
import ui.UI;
import label.Priority;

import java.util.ArrayList;


public class AddParticipantCommand extends Command {
    private final int eventIndex;
    private final String participantName;
    private final Participant.AccessLevel accessLevel;
    private final ArrayList<AvailabilitySlot> availabilitySlots;

    public AddParticipantCommand(int eventIndex, String participantName, Participant.AccessLevel accessLevel,
                                 ArrayList<AvailabilitySlot> availabilitySlots) {
        this.eventIndex = eventIndex;
        this.participantName = participantName;
        this.accessLevel = accessLevel;
        this.availabilitySlots = availabilitySlots;
    }

    @Override
    public void execute(EventManager eventManager, UI ui) throws SyncException {
        Event event = eventManager.getEvent(eventIndex);
        System.out.println("Event Index : " + eventIndex);
        System.out.println("Event Start Time : " + event.getStartTime());
        System.out.println("Event End Time : " + event.getEndTime());

        Participant newParticipant = new Participant(participantName, accessLevel);
        newParticipant.getAvailableTimes().addAll(availabilitySlots);

        if(event != null){
            boolean isAvailable = eventManager.checkParticipantAvailability(event, newParticipant);
            if(isAvailable == true){
                event.addParticipant(newParticipant);
                ui.showMessage("Participant " + newParticipant.getName() + "[" + accessLevel + "]" +
                        " has been added" );
            } else {
                ui.showMessage("Participant " + newParticipant.getName() + " is unavailable during the event");
            }
        } else {
            throw new SyncException("Could not find the event with the provided index");
        }

        // Persist updated event list to storage
        eventManager.getStorage().saveEvents(eventManager.getEvents(), Priority.getAllPriorities());
    }
}
