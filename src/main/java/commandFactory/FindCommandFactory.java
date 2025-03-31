package commandFactory;

import command.Command;
import command.FindCommand;
import exception.SyncException;

public class FindCommandFactory implements CommandFactory{
    private final String keyword;

    public FindCommandFactory(String keyword) {
        this.keyword = keyword;
    }

    public Command createCommand() throws SyncException {
        assert keyword != null : "Keyword should not be null";
        assert !keyword.isEmpty() : "Keyword should not be empty";

        return new FindCommand(keyword);
    }
}
