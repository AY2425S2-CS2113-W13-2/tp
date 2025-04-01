# Yan Xinyao - Project Portfolio Page

## Overview
Our team developed **EventSync**, a smart event scheduling and management application designed for seamless coordination. It enables users to create, update, and manage events efficiently while ensuring minimal conflicts and optimal scheduling.

I focused on implementing key functionalities for event management, optimizing performance, and ensuring robust testing through JUnit test cases.

## Code Contributed
[View my code on tP Code Dashboard](https://nus-cs2113-ay2425s2.github.io/tp-dashboard/?search=YanXinyaoo&sort=groupTitle%20dsc&sortWithin=title&since=2025-02-21&timeframe=commit&mergegroup=&groupSelect=groupByRepos&breakdown=true&checkedFileTypes=docs~functional-code~test-code~other)

## Enhancements Implemented

### Core Event Management Features (v1.0)
- **Implemented the following commands:**
    - `addEventCommand`: Allows users to create new events with customizable details.
    - `listCommand`: Displays all upcoming events in an organized format.

- **Helped with overall code structure:**
    - Refactored event-related classes to follow the **Single Responsibility Principle (SRP)**.
    - Improved modularity by separating **event command**, **paser**, and **UI interaction**.
    - Optimized event handling with **efficient data structures**.

### New Features and Enhancements (v2.0)
- **Added new commands:**
    - `addParticipantCommand`: Allows users to add participants to existing events with availability checks.
    - `createUserCommand`: Allows users to create new accounts and register with the system.
    - `LoginCommand`: Enables user authentication and session management.
    - `LogoutCommand`: Allows users to log out and generates a downloadable event schedule in CSV format.

- **Added new components:**
    - **`Storage`**:
        - A centralized storage system for all event-related data. This component ensures that events are stored persistently and can be retrieved across application sessions.
        - Stores event details such as event name, time, participants, and other metadata.
        - Uses a file-based or in-memory database (depending on your implementation) to persist event data.

    - **`UserStorage`**:
        - Manages the storage of user information, including user profiles, registration details, and availability.
        - Ensures that user data is stored securely and can be retrieved to check availability when scheduling new events.

- **Modification to `AddEventCommand`**:
        - The event creator is automatically added to the event.
        - The creator’s availability is checked to ensure there are no scheduling conflicts.
        - The event details are saved in `Storage` for future retrieval.

- **Added new command factories:**

### Command Factories

| Factory Class                         | Description                                                                 |
|---------------------------------------|-----------------------------------------------------------------------------|
| `AddEventCommandFactory`              | Creates a command to add new events with time conflict detection and automatic time slot suggestions |
| `AddParticipantCommandFactory`       | Creates a command to add participants to events with availability checks     |
| `ByeCommandFactory`                  | Creates a command to exit the application and save data                     |
| `CommandFactory`                     | Base factory class for all command factories                                |
| `CreateUserCommandFactory`           | Creates a command to register new users (stores in `UserStorage`)           |
| `DeleteCommandFactory`               | Creates a command to remove events with user confirmation                   |
| `DuplicateCommandFactory`            | Creates a command to duplicate events with modification options             |
| `EditCommandFactory`                 | Creates a command to modify event details with conflict detection           |
| `FilterCommandFactory`               | Creates a command to filter events by user-defined criteria                 |
| `FindCommandFactory`                 | Creates a command to search for events by keywords or date                 |
| `ListCommandFactory`                 | Creates a command to list events with optional filters                      |
| `ListParticipantsCommandFactory`     | Creates a command to list participants of a particular event                |
| `LoginCommandFactory`                | Creates a command for user authentication and login                        |
| `LogOutCommandFactory`               | Creates a command for logging out the user and saving session data         |


### Implementation of JUnit Tests
- **Added comprehensive JUnit tests for:**
    - `EventSyncTest`
    - Updated **text-ui-test**

## Contributions to the User Guide
- **Documented:**:
    - Interactive usage of `addEvent`, `listEvent`, `login`, `logout` and `addParticipant`
    - Provided detailed examples and common user scenarios

## Contributions to the Developer Guide
- **Added:**
    - **Sequence Diagrams** for `addEvent` and `listEvent` commands.
    - **Class Diagrams** for `EventManager` and its dependencies.
    - **Detailed explanations** of conflict resolution logic and event handling.

## Contributions to Team-Based Tasks
- Assisted in debugging and refactoring shared components.
- Participated in milestone discussions and design decisions.
- Contributed to project documentation and version control maintenance.

## Review/Mentoring Contributions
- Reviewed PRs related to event management and UI improvements.
- Provided feedback on optimizing scheduling algorithms and improving edge case handling.
- Helped team members troubleshoot complex bugs and refine error messaging.

---
