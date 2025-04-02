# Event Manager Developer Guide

## Acknowledgements

{list here sources of all reused/adapted ideas, code, documentation, and third-party libraries -- include links to the original source as well}

## Introduction

This project is a CLI-based Event Manager designed to help users organize their schedules effectively. In addition to 
creating, editing, deleting, and sorting events, the application supports participant management with access levels and 
availability. This Developer Guide documents the architecture, features, design considerations, and implementation 
details to help contributors understand the codebase and make meaningful contributions.

## Setting Up the Development Environment

### Prerequisites

- `JDK 17`
- `Gradle 7.6.2` or higher
- `IntelliJ IDEA` (recommended IDE for Java development)

### Getting Started

#### 1. Clone the Repository

Clone the repository to your local machine:
```
git clone https://github.com/AY2425S2-CS2113-W13-2/tp.git
```
#### 2. Import the Project into IntelliJ IDEA

To import the project:

- Open `IntelliJ IDEA`
- Click on `File → Open`
- Select the `tp` directory
- Choose the `build.gradle` file and open it
- Select `Open as Project` when prompted
- Ensure the JDK is set to `JDK 17`
- Wait for Gradle to finish syncing

#### 3. Verify the Setup

To ensure everything is working correctly, you can:

- Run all tests:
```
  ./gradlew test
```
- Launch the application:
```
./gradlew run
```
---

## Component Overview

[Click here to view the UML class diagram](UMLClassDiagram.pdf)

The architecture of Event Manager is structured into clearly defined components to ensure modularity and 
maintainability. The core components are outlined below:

### 1. Main Component (EventSync.java)
- Serves as the entry point of the application.
- Initializes and connects all components (UI, Parser, Managers).
- Manages the main execution loop and system shutdown.
---

### 2. UI Component (UI.java)
- Handles all user interaction through the command line.
- Displays menus, messages, prompts, and results.
- Reads user input and forwards it to the Parser.
---

### 3. Logic Component
- **Parser Component**:
  - Parser.java: Main parsing interface
  - CommandParser.java: Specialized parser for command inputs
- **Command Component**: Implements Command pattern with classes like:
  - Command.java: Abstract base class for all commands
  - AddEventCommand.java: Handles event creation
  - EditEventCommand.java: Allows event modification
  - DeleteCommand.java: Removes events
  - DuplicateCommand.java: Creates copies of events
  - ListCommand.java: Shows events with sort options
  - ListAllCommand.java: Displays all events
  - ListParticipantsCommand.java: Shows event participants
  - FindCommand.java: Searches for specific events
  - FilterCommand.java: Filters events by criteria
  - LoginCommand.java: Handles user authentication
  - LogOutCommand.java: Manages user logout
  - ByeCommand.java: Exits the application
  - CreateUserCommand.java: Creates new user profiles
  - AddParticipantCommand.java: Adds users to events
- **CommandFactory Component**: Factory pattern implementation for command creation:
  - CommandFactory.java: Base factory interface
  - Specialized factories for each command type (e.g., AddEventCommandFactory, DeleteCommandFactory)

---

### 4. Model Component
- **Event Component**:
  - Event.java: Core data structure for scheduled activities
  - EventManager.java: Manages events collection and operations
- **Participant Component**:
  - Participant.java: Represents users in the system
  - ParticipantManager.java: Handles user management
  - AvailabilitySlot.java: Tracks user availability periods
- **Label Component**:
  - Priority.java: Enum for event priority levels
- **Sort Component**: Strategy pattern for event sorting:
  - Sort.java: Abstract sorting strategy
  - SortByStartTime.java: Sorts by event start time
  - SortByEndTime.java: Sorts by event end time
  - SortByPriority.java: Sorts by priority level
- **Exception Component**:
  - SyncException.java: Custom exception for synchronization issues
---

### 5. Storage Component
- Storage.java: Base class for data persistence
- UserStorage.java: Handles user data storage
- Uses file I/O to save and load events and user data

![Storage Class Diagram](images/class_diagram.png)

#### API: UserStorage.java

The `UserStorage` component,

- is responsible for saving and loading user-related data, including participant details.
- stores data in a structured format, ensuring persistence across sessions. (sample: Alice | MEMBER | 123 | 2025-03-31 12:00,2025-05-31 12:00)
- depends on `Participant` as it manages participant-related storage operations.
- throws `SyncException` in case of synchronization failures.

#### API: Storage.java

The `Storage` component,

- handles the storage and retrieval of both event and priority data.
- integrates `UserStorage` to manage participant-related information.
- can save and load events, including participant details and scheduling information.
- depends on `UserStorage`, `Event`, and `Priority` as it manages multiple data types.
- throws `SyncException` to handle potential storage errors.
---

### 6. Logger Component
- EventSyncLogger.java: Handles logging for debugging and tracking

---

### 7. Common classes
-

---

## Design & Implementation

### Add Event Feature

#### Implementation
The `AddEventCommand` feature allows users to add an event to their schedule. It is implemented using the `Command` pattern, where `AddEventCommand` extends `Command`.

1. **User Input Parsing**: The `Parser` class processes user input and creates an `AddEventCommand` instance.
2. **Event Storage**: The event details are stored in an `Event` object, which is added to an `EventList`.
3. **Feedback to User**: A confirmation message is displayed to inform the user of successful event creation.

#### Design Considerations

- **Why this design?**
    - The `Command` pattern allows easy extension for future commands.
    - Using an `EventList` simplifies storage and retrieval.

### Edit Event Feature

#### Implementation

The `EditEventCommand` feature allows users to modify an existing event.

1. **User Input Parsing**: The `Parser` prompts the user for the index of the event they want to edit.
2. **Modification Flow**: The user is guided through inputs to edit event fields (e.g., name, time, location, etc.).
3. **Update Storage**: The original event is updated in the `EventList`.

#### Design Considerations

- **Why this design?**
  - Enables users to fix or update event details without needing to delete and recreate them.
  - Reuses existing input and validation logic, keeping the system modular.


### Conflict Detector Feature
#### Implementation

The `ConflictDetector` feature checks for overlapping events in a user's schedule.
1. **Conflict Check**: Compares the start and end times of all events in EventList.
2. **Feedback to User**: If conflicts are detected, a warning message is displayed to the user.

#### Design Considerations
- **Why this design?**
  - Ensures users are aware of scheduling conflicts, preventing double-booking.
  - Improves event management by highlighting overlapping schedules.

### List Events Feature

#### Implementation
The `ListCommand` allows users to list events with custom sorting options.

1. **User Prompt**: When the user types `list`, they are asked how they want to sort the events.
2. **Sort Strategies**: 
- Implemented using the Strategy Pattern:
  - `SortByPriority`: sorts from HIGH to LOW, with a subsort by end time (earlier first).
  - `SortByStartTime`: sorts by earliest start time, then by priority (HIGH first).
  - `SortByEndTime`: sorts by earliest end time, then by priority (HIGH first).
3. **Modularity**: The `Sort` abstract class defines the contract for sorting. Each strategy class overrides it with specific logic.
4. **Non-destructive Sorting**: Events and priorities are copied into temporary lists, sorted, and displayed without altering the original list.

#### Design Considerations

- **Why this design?**
  - It enables flexibility and clean extension of new sort types.
  - Keeps the original data intact.
  - Allows consistent behavior across commands.

### Delete Event Feature

#### Implementation
The `DeleteCommand` feature users to remove an event from their schedule by keyword. If multiple events share the same name, users are prompted to select the correct one based on the list shown.
1. **User Input Parsing**: 
The `Parser` reads the event keyword from user input using `readDeleteName()`. `findMatchingEvents(name)` is used to gather all matching events. 
If more than one match is found, the UI shows them with indices for disambiguation.
2. **Event Deletion Flow**: The selected event is passed to `DeleteCommand` with the correct index. In `execute()`, the user is asked to confirm the deletion via `ui.confirmDeletion()`.
3. **Data Synchronization**: If confirmed, the event and its corresponding priority are removed.

#### Design Considerations
- **Why this design?**
  - Maintains consistency between the events list and the priority list.
  - Ensures safe deletion by confirming with the user.

### Priority Filter Feature

#### Implementation
The `PriorityFilter` feature allows users to filter events by priority level (LOW, MEDIUM, HIGH). 

1. **User Input Parsing**: The Parser class processes input in format {PRIORITY PRIORITY}.
2. **Event Filtering**: Events with priority within the range are filtered.
3. **Display**: Events are printed to the user.

#### Design Considerations

- **Why this design?**
  - Allows users to quickly find events with certain priorities.
  - Enhances usability by enabling customized event views.

### Duplicate Event Feature

#### Implementation
The `DuplicateEventCommand` feature allows users to duplicate an existing event to their schedule. It is implemented using the `Command` pattern, where `DuplicateEventCommand` extends `Command`.

1. **User Input Parsing**: The `Parser` class processes user input and creates an `DuplicateEventCommand` instance.
2. **Event Storage**: The duplicated event details are stored in an `Event` object, which is added to an `EventList`.
4. **Feedback to User**: The user is asked to input the index that they wish to duplicate and the new name of the event. Duplicated event is displayed to inform the user of successful event duplication.

#### Design Considerations

- **Why this design?**
    - The `Command` pattern allows easy extension for future commands.
    - Using an `EventList` simplifies storage and retrieval.

### Add Participant Feature

#### Implementation

The `AddParticipantCommand` feature enables users to add a participant to a specific event.

1. **User Input Parsing**: The `Parser` reads the event index, participant name, and access level (ADMIN or MEMBER).
2. **Validation**: Ensures valid access level and event index.
3. **Participant Assignment**: Adds the participant to the event.

#### Design Considerations

- **Why this design?**
  - Facilitates collaboration by assigning roles to users in events.
  - Enhances event detail and accountability.


### List Participants Feature

#### Implementation

The `ListParticipantsCommand` feature lists all participants assigned to a specific event.

1. **User Input Parsing**: The user inputs the index of the event.
2. **Retrieval**: Fetches and displays the participants for that event.

#### Design Considerations

- **Why this design?**
  - Allows users to view who's involved in an event.
  - Useful for managing group tasks or meetings.

### Storage

#### Implementation
The storage component is responsible for persisting event data across sessions.

1. **File Handling**: Data is stored in a text file (`events.txt`).
2. **Serialization**: Events are written and read in a structured format.
3. **Error Handling**: Ensures corrupted files do not crash the program by adding fallback mechanisms.

#### Design Considerations

- **Why this design?**
    - Using a text file ensures simplicity, portability, and easy debugging.
    - It is lightweight and does not require additional libraries or systems for data management.

## Product Scope
### Target User Profile

The target user profile for this application includes individuals who need to manage their personal or professional schedules. These users may be:

- Students who need to track assignments and exam dates.
- Professionals who need to manage meetings, deadlines, and other appointments.
- Anyone looking for a simple, easy-to-use event management tool.

### Value Proposition

This application addresses the need for a lightweight, easy-to-use solution for managing and organizing events. It provides the following value:

- **Simple Event Management**: Users can add, list, and organize their events with minimal effort.
- **Persistence**: Events are saved between application sessions, ensuring that users' data is not lost.
- **Flexibility**: It allows users to customize events and sort them in the order they choose (chronologically).

By providing these features, the application allows users to focus on their tasks and not on manually managing their schedule.

## User Stories

| Version | As a ...       | I want to ...                          | So that I can ...                                |
|---------|----------------|----------------------------------------|--------------------------------------------------|
| v1.0    | user           | add an event with a description and date/time | keep track of important events and deadlines |
| v1.0    | user           | list all my events                    | see all my events organized by date/time |
| v2.0    | user           | store events persistently             | ensure my events remain even after restarting the application |
| v2.0    | admin          | edit event details                    | fix errors or update times                        |
| v2.0    | user           | filter events by priority             | focus on high-priority tasks when needed          |
| v2.0    | team leader    | add participants to events            | track who is involved in each activity            |
| v2.0    | user           | detect scheduling conflicts           | avoid double-booking myself                       |
| v2.0    | user           | duplicate existing events             | quickly create similar events without re-entering all details |
| v2.0    | user           | sort events in different ways         | view my schedule according to my current needs    |
| v2.0    | user           | create a user profile                 | have personalized access to the system            |
| v2.0    | user           | log in and out of the system          | keep my schedule information secure               |

## Non-Functional Requirements

- **Performance**: The application should be able to handle up to 1000 events efficiently, with quick load and save times.
- **Usability**: The application should be easy to use with minimal training. User commands should be clear and simple.
- **Reliability**: The application should be stable, with no crashes or errors during typical use cases. Data should not be lost in case of unexpected shutdowns.
- **Portability**: The application should work on different platforms, such as Windows, macOS, and Linux, as long as Java is installed.
- **Maintainability**: The codebase should be easy to maintain and extend, with well-documented functions and modular code.
- **Scalability**: The application should be able to scale to handle more complex user cases, such as handling recurring events or multiple event categories.

## Glossary

* **Event** - A scheduled activity with a description and a specific date/time.
* **EventManager** - Component that manages the collection of events and operations on them.
* **Command Pattern** - A design pattern that encapsulates a request as an object, allowing for parameterization of clients with different requests.
* **Factory Pattern** - A creational pattern that uses factory methods to create objects without specifying the exact class.
* **Strategy Pattern** - A behavioral design pattern that enables selecting an algorithm at runtime.
* **Serialization** - The process of converting an object into a format (such as text) that can be stored or transmitted, and later reconstructed.
* **Participant** - A user who is assigned to an event with a specific access level.
* **Priority** - A label indicating the importance level of an event (LOW, MEDIUM, HIGH).

## Instructions for manual testing

{Give instructions on how to do a manual product testing e.g., how to load sample data to be used for testing}
