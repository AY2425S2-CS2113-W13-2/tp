package commandfactory;

import command.Command;
import command.HelpCommand;

public class HelpCommandFactory implements CommandFactory {
    public Command createCommand() {
        return new HelpCommand();
    }
}
