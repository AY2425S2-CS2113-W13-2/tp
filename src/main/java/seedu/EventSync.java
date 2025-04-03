package seedu;

import commandfactory.CommandFactory;
import event.EventManager;
import parser.Parser;
import participant.ParticipantManager;
import storage.Storage;
import storage.UserStorage;
import ui.UI;
import exception.SyncException;
import command.Command;

import java.io.InputStream;
import java.util.Scanner;

public class EventSync {
    private final UI ui;
    private final EventManager eventManager;
    private final ParticipantManager participantManager;
    private final Parser parser;
    private final Scanner scanner;
    private final Storage storage;
    private final UserStorage userStorage;

    public EventSync(String filePathEvent, String filePathUser) throws SyncException {
        scanner = new Scanner(System.in);
        ui = new UI();
        userStorage = new UserStorage(filePathUser);
        storage = new Storage(filePathEvent, userStorage);
        participantManager = new ParticipantManager(userStorage.loadUsers(), ui, userStorage);
        eventManager = new EventManager(storage.loadEvents(), ui, storage, userStorage);
        parser = new Parser(eventManager, participantManager, ui, scanner);
    }

    public EventSync(InputStream inputStream,
                     String filePathEvent, String filePathUser) throws SyncException {
        userStorage = new UserStorage(filePathUser);
        ui = new UI();
        storage = new Storage(filePathEvent, userStorage);
        eventManager = new EventManager(storage.loadEvents(), ui, storage, userStorage);
        participantManager = new ParticipantManager(userStorage.loadUsers(), ui, userStorage);
        scanner = new Scanner(inputStream);
        parser = new Parser(eventManager, participantManager, ui, this.scanner);
    }

    public void run() {
        ui.showWelcomeMessage();
        boolean isExit = false;
        while (!isExit && scanner.hasNextLine()) {
            String input = scanner.nextLine();
            try {
                CommandFactory factory = parser.parse(input);
                Command c = factory.createCommand();
                c.execute(eventManager, ui, participantManager);
                isExit = c.isExit();
            } catch (SyncException e) {
                ui.showMessage(e.getMessage());
            }
        }
        scanner.close();
    }

    public static void main(String[] args) throws SyncException {
        new EventSync("./data/seedu.EventSync.txt", "./data/seedu.UserSync.txt" ).run();
    }
}
