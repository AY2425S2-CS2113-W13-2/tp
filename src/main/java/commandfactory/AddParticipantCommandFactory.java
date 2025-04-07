package commandfactory;

import command.AddParticipantCommand;
import event.Event;
import event.EventManager;
import participant.Participant;
import participant.ParticipantManager;
import exception.SyncException;
import java.util.ArrayList;
import ui.UI;

public class AddParticipantCommandFactory implements CommandFactory {
    private final ParticipantManager participantManager;
    private final UI ui;
    private final EventManager eventManager;

    public AddParticipantCommandFactory(EventManager eventManager, ParticipantManager participantManager, UI ui) {
        this.participantManager = participantManager;
        this.ui = ui;
        this.eventManager = eventManager;
    }

    public AddParticipantCommand createCommand() throws SyncException {
        Participant participant = participantManager.getCurrentUser();

        if (participant == null) {
            throw new SyncException("You are not logged in. Enter 'login' to log in first.");
        }

        checkAdminPrivileges();
        showAllEvents();
        showAllParticipants();
        String[] input = ui.splitAddParticipantCommandInput();

        int eventIndex;
        try {
            eventIndex = Integer.parseInt(input[0].trim()) - 1;
        } catch (NumberFormatException e) {
            throw new SyncException("❌ Invalid event number. Please enter 'addparticipant' and try again.");
        }

        if (eventIndex < 0) {
            throw new SyncException("Event index cannot be negative. Please enter a valid index.");
        }

        if (eventIndex >= eventManager.getEvents().size()) {
            throw new SyncException("Event index is out of bounds. Please enter a valid index.");
        }

        if (input[1].trim().isEmpty()) {
            throw new SyncException("Participant name cannot be empty. Please enter a valid name.");
        }

        return new AddParticipantCommand(
                eventIndex,
                input[1].trim(),
                ui,
                participantManager
        );
    }

    private void showAllParticipants() {
        ArrayList<Participant> participants = participantManager.getParticipants();
        if (participants.isEmpty()) {
            ui.showMessage("No participants available.");
            return;
        }

        ui.showMessage("Available Participants:");
        for (Participant participant : participants) {
            ui.showMessage("- " + participant.getName());
        }
    }

    private void checkAdminPrivileges() throws SyncException {
        if (!participantManager.isCurrentUserAdmin()) {
            throw new SyncException("Only ADMIN users can add participants. Please 'logout' " +
                    "and 'login' to an ADMIN user");
        }
    }

    private void showAllEvents() {
        ArrayList<Event> events = eventManager.getEvents();
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
