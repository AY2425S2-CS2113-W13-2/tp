package commandfactory;

import command.AddParticipantCommand;
import event.Event;
import event.EventManager;
import participant.Participant;
import participant.ParticipantManager;
import exception.SyncException;
import java.util.ArrayList;
import ui.UI;

/**
 * Factory class for creating AddParticipantCommand instances.
 * Ensures that the current user is logged in, has admin privileges, and shows available events and participants.
 */
public class AddParticipantCommandFactory implements CommandFactory {
    private final ParticipantManager participantManager;
    private final UI ui;
    private final EventManager eventManager;

    /**
     * Constructs an AddParticipantCommandFactory with the specified EventManager, ParticipantManager, and UI.
     *
     * @param eventManager the EventManager instance used to manage events
     * @param participantManager the ParticipantManager instance used to manage participants
     * @param ui the UI instance used to interact with the user
     */
    public AddParticipantCommandFactory(EventManager eventManager, ParticipantManager participantManager, UI ui) {
        this.participantManager = participantManager;
        this.ui = ui;
        this.eventManager = eventManager;
    }

    /**
     * Creates an AddParticipantCommand based on user input.
     * Validates the user's status (must be logged in and an admin) and allows selecting an event and participant to add.
     *
     * @return an AddParticipantCommand that can be executed to add a participant to an event
     * @throws SyncException if the user is not logged in, is not an admin, or if the input is invalid
     */
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

        return new AddParticipantCommand(
                eventIndex,
                input[1].trim(),
                ui,
                participantManager
        );
    }

    /**
     * Displays the list of all participants.
     * If no participants are available, a message is displayed to inform the user.
     */
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

    /**
     * Checks if the current user has admin privileges.
     * Throws a SyncException if the user is not an admin.
     *
     * @throws SyncException if the current user is not an admin
     */
    private void checkAdminPrivileges() throws SyncException {
        if (!participantManager.isCurrentUserAdmin()) {
            throw new SyncException("Only ADMIN users can add participants. Please 'logout' " +
                    "and 'login' to an ADMIN user");
        }
    }

    /**
     * Displays the list of all events.
     * If no events are available, a message is displayed to inform the user.
     */
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
