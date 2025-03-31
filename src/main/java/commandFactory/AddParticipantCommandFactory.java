package commandFactory;

import command.AddParticipantCommand;
import participant.AvailabilitySlot;
import participant.Participant;
import participant.ParticipantManager;
import exception.SyncException;
import java.util.ArrayList;
import parser.CommandParser;
import ui.UI;

public class AddParticipantCommandFactory implements CommandFactory {
    private final ParticipantManager participantManager;
    private final UI ui;

    public AddParticipantCommandFactory(ParticipantManager participantManager, UI ui) {
        this.participantManager = participantManager;
        this.ui = ui;
    }

    public AddParticipantCommand createCommand() throws SyncException {
        checkAdminPrivileges();
        String[] input = CommandParser.splitAddParticipantCommandInput();

        return new AddParticipantCommand(
                Integer.parseInt(input[0].trim()) - 1,
                input[1].trim()
        );
    }

    private void checkAdminPrivileges() throws SyncException {
        if (!participantManager.isCurrentUserAdmin()) {
            throw new SyncException("Only ADMIN users can add participants.");
        }
    }
}