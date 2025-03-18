package parser;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Logger;

import command.*;
import command.AddEventCommand;
import command.ByeCommand;
import command.Command;
import command.DeleteCommand;
import command.DuplicateCommand;
import command.EditEventCommand;
import command.ListCommand;
import command.FindCommand;
import logger.EventSyncLogger;


public class Parser {
    private static final Logger logger = EventSyncLogger.getLogger();
    private final EventManager eventManager;
    private final UI ui;
    private final Scanner scanner;

    public Parser(EventManager eventManager, UI ui) {
        this.eventManager = eventManager;
        this.ui = ui;
        this.scanner = new Scanner(System.in);
        logger.info("Parser initialized with default scanner.");
    }

    public Parser(EventManager eventManager, UI ui, Scanner scanner) {
        this.eventManager = eventManager;
        this.ui = ui;
        this.scanner = scanner;
        logger.info("Parser initialized with custom scanner.");
    }

    public Command parse(String input) throws SyncException {

            
        logger.info("Parsing command: " + input);
        
        String[] parts = input.trim().toLowerCase().split(" ", 2); // Split input

        if (parts.length > 0) {
            String commandWord = parts[0];

        switch (commandWord.toLowerCase()) {
        case "bye":
            logger.info("Bye command received.");
            return new ByeCommand();
        case "list":
            logger.info("List command received.");
            return new ListCommand();
        case "add":
            logger.info("Add command received.");
            return createAddEventCommand();
        case "delete":
            logger.info("Delete command received.");
            return createDeleteCommand();
        case "duplicate":
            logger.info("Duplicate command received.");
            return createDuplicateCommand();
        case "edit":
            logger.info("Edit command received.");
            return createEditCommand();
        case "find":
            if (parts.length > 1) {
                logger.info("Find command received with keyword: " + parts[1]);
                return createFindCommand(parts[1]);
            } else {
                logger.warning("Find command received without keyword.");
                throw new SyncException("Please provide a keyword");
        }
        default:
            logger.warning("Invalid command received: " + input);
            throw new SyncException(SyncException.invalidCommandErrorMessage(input));

        }
    }

    private Command createFindCommand(String keyword) throws SyncException {
        assert keyword != null : "Keyword should not be null";
        assert !keyword.isEmpty() : "Keyword should not be empty";

        return new FindCommand(keyword);
    }


    private Command createAddEventCommand() throws SyncException {
        logger.info("Creating add event command.");
        String input = readAddEventInput();
        logger.fine("Input for add event: " + input);

        assert input != null : "Input string should not be null";
        assert !input.trim().isEmpty() : "Input string should not be empty";

        String[] parts = input.split("\\|");

        assert parts.length > 0 : "Split result should not be empty";

        if (parts.length != 5) {
            logger.warning("Invalid number of parts in input: " + parts.length);
            throw new SyncException(SyncException.invalidEventDetailsErrorMessage());
        }

        try {
            String name = parts[0].trim();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");

            LocalDateTime startTime = LocalDateTime.parse(parts[1].trim(), formatter);
            LocalDateTime endTime = LocalDateTime.parse(parts[2].trim(), formatter);

            assert startTime != null : "Start time should not be null";
            assert endTime != null : "End time should not be null";
            assert !endTime.isBefore(startTime) : "End time should not be before start time";

            String location = parts[3].trim();
            String description = parts[4].trim();

            assert !name.isEmpty() : "Event name should not be empty";
            assert !location.isEmpty() : "Event location should not be empty";
            assert !description.isEmpty() : "Event description should not be empty";

            Event newEvent = new Event(name, startTime, endTime, location, description);
            logger.info("New event created: " + newEvent);
            return new AddEventCommand(newEvent);
        } catch (DateTimeException e) {
            logger.severe("DateTimeException occurred: " + e.getMessage());
            throw new SyncException("Invalid date-time format. Please use yyyy/MM/dd HH:mm");
        } catch (Exception e) {
            logger.severe("Unexpected exception occurred: " + e.getMessage());
            throw new SyncException(SyncException.invalidEventDetailsErrorMessage());
        }
    }

    private String readAddEventInput() {
        ui.showAddFormat();
        return scanner.nextLine();
    }

    private Command createDeleteCommand() throws SyncException {
        String name = readDeleteName();
        ArrayList<Event> matchingEvents = findMatchingEvents(name);

        if (matchingEvents.isEmpty()) {
            throw new SyncException("No events found with the name: " + name);
        } else if (matchingEvents.size() == 1) {
            Event eventToDelete = matchingEvents.get(0);
            if (ui.confirmDeletion(eventToDelete.getName())) {
                int eventIndex = eventManager.getEvents().indexOf(eventToDelete);
                return new DeleteCommand(eventIndex);
            } else {
                ui.showMessage("Deletion cancelled.");
                return null;
            }
        } else {
            ui.showMatchingEventsWithIndices(matchingEvents, eventManager);
            int eventIndex = readDeleteEventIndex(matchingEvents);
            return new DeleteCommand(eventManager.getEvents().indexOf(matchingEvents.get(eventIndex)));
        }
    }

    private String readDeleteName() {
        System.out.print("Enter name to search for events to delete: ");
        return scanner.nextLine().trim();
    }

    private ArrayList<Event> findMatchingEvents(String name) {
        ArrayList<Event> matchingEvents = new ArrayList<>();
        for (Event event : eventManager.getEvents()) {
            if (event.getName().toLowerCase().contains(name.toLowerCase())) {
                matchingEvents.add(event);
            }
        }
        return matchingEvents;
    }

    private int readDeleteEventIndex(ArrayList<Event> matchingEvents) throws SyncException {  // 🔹 Ask for event index
        System.out.print("Enter the index of the event you want to delete: ");
        try {
            int index = Integer.parseInt(scanner.nextLine()) - 1;
            if (index < 0 || index >= matchingEvents.size()) {
                throw new SyncException("Invalid event index. Please enter a valid index.");
            }
            return eventManager.getEvents().indexOf(matchingEvents.get(index));
            // Convert matching event index to actual event index
        } catch (NumberFormatException e) {
            throw new SyncException("Invalid index format. Please enter a number.");
        }
    }

    private Command createDuplicateCommand() throws SyncException {
        String input = ui.readDuplicateEventInput();
        String[] parts = input.split(" ", 2);

        if (parts.length < 2) {
            throw new SyncException("Invalid duplicate command format. Use: duplicate index New Event Name");
        }

        try {
            int index = Integer.parseInt(parts[0]) - 1;
            if (index >= 0 && index < eventManager.getEvents().size()) {
                Event eventToDuplicate = eventManager.getEvents().get(index);
                String newName = parts[1];
                return new DuplicateCommand(eventToDuplicate, newName);
            } else {
                throw new SyncException("Invalid event index.");
            }
        } catch (NumberFormatException e) {
            throw new SyncException("Invalid index format. Use a number.");
        }
    }

    private Command createEditCommand() throws SyncException {
        int index = readEditEventIndex();
        return new EditEventCommand(index);
    }

    private int readEditEventIndex() throws SyncException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter event index to edit: ");
        try {
            int index = Integer.parseInt(scanner.nextLine()) - 1;
            return index;
        } catch (Exception e) {
            throw new SyncException(SyncException.invalidEventDetailsErrorMessage());
        }
    }
}
