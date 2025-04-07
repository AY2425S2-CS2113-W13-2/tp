package command;

import static org.junit.jupiter.api.Assertions.assertFalse;

import event.EventManager;
import org.junit.jupiter.api.Test;
import participant.ParticipantManager;
import ui.UI;
import exception.SyncException;

class CommandTest {

    @Test
    void testIsExitDefault() {
        Command command = new Command() {
            @Override
            public void execute(EventManager events, UI ui, ParticipantManager participantManager)
                    throws SyncException {
            }
        };

        assertFalse(command.isExit(), "Default isExit() should return false");
    }
}
