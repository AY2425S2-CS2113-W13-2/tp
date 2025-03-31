package participant;

import event.Event;
import ui.UI;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class ParticipantManager {
    private final UI ui;

    public ParticipantManager(UI ui) {
        this.ui = ui;
    }

    public boolean checkParticipantAvailability(Event event, Participant participant) {

        System.out.println("Checking participant availability");
        for (AvailabilitySlot slot : participant.getAvailableTimes()) {
            System.out.println("  -" + slot.getStartTime() + " to " + slot.getEndTime());
            LocalDateTime slotStart = slot.getStartTime();
            LocalDateTime slotEnd = slot.getEndTime();

            if (!(event.getStartTime().isAfter(slotEnd) && event.getEndTime().isBefore(slotStart))) {
                ui.showParticipantSlotCollisionWarning(event, new ArrayList<>());
                return false; //Participant is unavailable
            }
            //ui.showParticipantSlotCollisionWarning(event, new ArrayList<>());
            return true; // Participant is available
        }
        return true;
    }
}
