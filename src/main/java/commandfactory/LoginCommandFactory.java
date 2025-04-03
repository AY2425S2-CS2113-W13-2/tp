package commandfactory;

import command.Command;
import command.LoginCommand;

public class LoginCommandFactory implements CommandFactory{
    public Command createCommand() {
        return new LoginCommand();
    }
}
