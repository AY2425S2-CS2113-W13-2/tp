package commandfactory;

import static org.junit.jupiter.api.Assertions.assertTrue;

import command.Command;
import command.HelpCommand;
import org.junit.jupiter.api.Test;


public class HelpCommandFactoryTest {

    @Test
    public void testCreateCommandReturnsHelpCommand() {
        HelpCommandFactory factory = new HelpCommandFactory();
        Command command = factory.createCommand();
        assertTrue(command instanceof HelpCommand, "Expected a HelpCommand instance");
    }
}
