package commandfactory;

import command.Command;
import command.LogOutCommand;

public class LogOutCommandFactory implements CommandFactory {
    public Command createCommand() {
        return new LogOutCommand();
    }
}
