# Event Manager Developer Guide

## Acknowledgements

{list here sources of all reused/adapted ideas, code, documentation, and third-party libraries -- include links to the original source as well}

## Introduction

This project is a CLI-based Event Manager designed to help users organize their schedules effectively. In addition to creating, editing, deleting, and sorting events, the application supports participant management. This Developer Guide documents the architecture, features, design considerations, and implementation details to help contributors understand the codebase and make meaningful contributions.

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

## User Stories

| Version | As a ...       | I want to ...                          | So that I can ...                                |
|---------|----------------|----------------------------------------|--------------------------------------------------|
| v1.0    | user           | add an event with a description and date/time | keep track of important events and deadlines |
| v1.0    | user           | list all my events                    | see all my events organized by date/time |
| v2.0    | user           | store events persistently             | ensure my events remain even after restarting the application |

## Non-Functional Requirements

- **Performance**: The application should be able to handle up to 1000 events efficiently, with quick load and save times.
- **Usability**: The application should be easy to use with minimal training. User commands should be clear and simple.
- **Reliability**: The application should be stable, with no crashes or errors during typical use cases. Data should not be lost in case of unexpected shutdowns.
- **Portability**: The application should work on different platforms, such as Windows, macOS, and Linux, as long as Java is installed.
- **Maintainability**: The codebase should be easy to maintain and extend, with well-documented functions and modular code.
- **Scalability**: The application should be able to scale to handle more complex user cases, such as handling recurring events or multiple event categories.

## Glossary

* **Event** - A scheduled activity with a description and a specific date/time.
* **EventList** - A collection or list of all events, used to store and manage events.
* **Command Pattern** - A design pattern that encapsulates a request as an object, allowing for parameterization of clients with different requests.
* **Serialization** - The process of converting an object into a format (such as text) that can be stored or transmitted, and later reconstructed.

## Instructions for manual testing

{Give instructions on how to do a manual product testing e.g., how to load sample data to be used for testing}
