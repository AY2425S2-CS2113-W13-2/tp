package seedu;

import event.EventManager;
import parser.Parser;
import storage.Storage;
import ui.UI;
import exception.SyncException;
import command.Command;

import java.io.InputStream;
import java.util.Scanner;

public class EventSync {
    private final UI ui;
    private final EventManager eventManager;
    private final Parser parser;
    private final Scanner scanner;
    private final Storage storage;

    public EventSync(String filePath) throws SyncException {
        this.scanner = new Scanner(System.in);
        ui = new UI();
        storage = new Storage(filePath);
        eventManager = new EventManager(storage.loadEvents(), ui, storage);
        parser = new Parser(eventManager, ui, scanner);
    }

    public EventSync(InputStream inputStream, String filePath) throws SyncException {
        ui = new UI();
        storage = new Storage(filePath);
        eventManager = new EventManager(storage.loadEvents(), ui, storage);
        scanner = new Scanner(inputStream);
        parser = new Parser(eventManager, ui, this.scanner);
    }

    public void run() {
        ui.showMessage("Welcome to seedu.EventSync!");
        boolean isExit = false;
        while (!isExit && scanner.hasNextLine()) {
            String input = scanner.nextLine();
            try {
                Command c = parser.parse(input);
                c.execute(eventManager, ui);
                isExit = c.isExit();
            } catch (SyncException e) {
                ui.showMessage(e.getMessage());
            }
        }
        scanner.close();
    }

    public static void main(String[] args) throws SyncException {
        new EventSync("./data/seedu.EventSync.txt").run();
    }
}
