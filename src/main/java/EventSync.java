import java.util.Scanner;
public class EventSync {
    private final EventManager eventManager;
    private final Parser parser;
    private final UI ui;

    public EventSync() {
        ui = new UI();
        eventManager = new EventManager();
        parser = new Parser(eventManager, ui);
    }

    public void run() {
        ui.showMessage("Welcome to EventSync!");
        if (!eventManager.getEvents().isEmpty()) {
            ui.showEventList(eventManager);
        }

        Scanner scanner = new Scanner(System.in);

        while (scanner.hasNextLine()) {
            String input = scanner.nextLine();

            try {
                parser.parse(input);
                if (input.equalsIgnoreCase("bye")) {
                    break;
                }
            } catch (SyncException e) {
                ui.showMessage(e.getMessage());
            }
        }

        scanner.close();
        ui.showMessage("Goodbye!");
    }

    public static void main(String[] args) {
        (new EventSync()).run();
    }
}