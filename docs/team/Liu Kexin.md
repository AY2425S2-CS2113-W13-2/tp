# Liu Kexin - Project Portfolio Page

## Overview
Our team developed `EventSync`, a command-line event scheduling and management application aimed at optimizing 
collaboration and time management. Users can create, view, edit, and organize events with conflict detection, 
user management, and sorting capabilities.

I was primarily responsible for implementing the priority system, the list command with sorting strategies, and the delete feature.


## Code Contributed
[View my code on tP Code Dashboard](https://nus-cs2113-ay2425s2.github.io/tp-dashboard/?search=Lydialkx&sort=groupTitle%20dsc&sortWithin=title&since=2025-02-21&timeframe=commit&mergegroup=&groupSelect=groupByRepos&breakdown=true&checkedFileTypes=docs~functional-code~test-code~other)

## Enhancements Implemented
### Core Event Management Features (v1.0)
- **Delete Feature**:
    - Implemented the `DeleteCommand` that allows users to remove events by keyword.
    - Supported disambiguation when multiple events match the keyword — shows list with index selection.
    - Confirmed deletion with the user before removal.
    - Synchronized deletion between the event list and the priority list to ensure consistency.

### New Features and Enhancements (v2.0)
- **Priority System**:
    - Designed and implemented a `Priority` class under the `label` package to manage event priorities.
    - Enabled setting and displaying priority levels (`HIGH`, `MEDIUM`, `LOW`) during event creation and display.

- **Enhanced List Command with Sorting**:
    - Refactored `list` command to support dynamic sorting strategies.
    - Supported sorting types:
      - By **priority** (primary), sub-sorted by end time.
      - By **start** or **end time** (primary), sub-sorted by priority.
    - Synced sorting logic for both events and their corresponding priority levels.
    - Prompted user to select sort type after entering list, enhancing interactivity.


### Implementation of JUnit Tests
- **Added comprehensive JUnit tests for:**
    - `PriorityTest.java`
    - `ListCommandTest.java`
    - `DeleteEventTest.java`
    - `SortByPriorityTest.java`
    - `SortByStartTimeTest.java`
    - `SortByEndTimeTest.java`
    - `ParticipantManagerTest.java`
    - `PriorityFilterTest.java`
- Ensured coverage of edge cases, sorting correctness, and priority-event syncing.

## Contributions to the User Guide
- Drafted the **basic command syntax and structure** for major features, ensuring consistency and clarity across command formats.
- Documented detailed usage for `delete`, `list` (with sort), and `priority` features.
- Added example inputs/outputs to guide users on expected behavior and edge cases.

## Contributions to the Developer Guide
- **Added:**
   - **Model Component Diagram** in `modelComponent.puml`, which captures the relationships between all core classes such 
  as `EventManager`, `ParticipantManager`, `Priority`, `Sort`, and `SyncException`.
   - **Sequence Diagram** for the `deleteEvent` feature (`DeleteEvent.puml`) that includes branching logic for 
  handling multiple matching events and user confirmation.
   - **Implementation Details** and design rationale for:
     - `deleteEvent`: including parsing flow, user disambiguation logic, confirmation prompts, and data synchronization.
     - `listEvent`: including enhancement to sorting strategy (by start time, end time, priority).
   - **Logging Guide** under the Documentation, logging, testing, configuration, dev-ops section, describing 
  the usage of EventSyncLogger with java.util.logging, setup, and code snippets.

## Contributions to Team-Based Tasks
- Helped debug integration bugs in list and delete features.
- Reviewed and coordinated merging of team contributions related to events and commands.
- Supported documentation polishing and milestone deliverables (DG, UG formatting).

## Review/Mentoring Contributions
- Reviewed PRs related to sorting, filtering, and command structure.
- Assisted teammates with testing and implementation of user input parsing and validation.

---