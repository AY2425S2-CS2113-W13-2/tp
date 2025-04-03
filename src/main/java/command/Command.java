    package command;

    import event.EventManager;
    import participant.ParticipantManager;
    import ui.UI;
    import exception.SyncException;

    public abstract class Command {
        public abstract void execute(EventManager events, UI ui, ParticipantManager participantManager) throws SyncException;

        public boolean isExit() {
            return false;
        }
    }
