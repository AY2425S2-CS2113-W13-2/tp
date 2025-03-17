import event.EventManager;
import parser.Parser;
import ui.UI;
import exception.SyncException;
import command.Command;

import java.util.Scanner;

public class EventSync {
    private final UI ui;
    private final EventManager eventManager;
    private final Parser parser;

    public EventSync() throws SyncException {
        ui = new UI();
        eventManager = new EventManager();
        parser = new Parser(eventManager, ui);
    }

    public void run() {
        ui.showMessage("Welcome to EventSync!");
        boolean isExit = false;
        Scanner scanner = new Scanner(System.in);

        while (!isExit) {
            String input = scanner.nextLine();

            try {
                Command c = parser.parse(input);
                c.execute(eventManager, ui);
                isExit = c.isExit();
            } catch (SyncException e) {
                ui.showMessage(e.getMessage());
            } finally {
            }
        }

        scanner.close();
        ui.showMessage("Goodbye!");
    }

    public static void main(String[] args) throws SyncException {
        new EventSync().run();
    }
}
