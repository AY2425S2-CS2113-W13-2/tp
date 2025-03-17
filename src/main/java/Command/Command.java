package Command;

import Event.EventManager;
import UI.UI;
import Exception.SyncException;

abstract public class Command {
        public abstract void execute(EventManager events, UI ui) throws SyncException;

        public boolean isExit() {
            return false;
        }
}