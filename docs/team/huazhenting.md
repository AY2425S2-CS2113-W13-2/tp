## **Overview**

Our team developed **EventSync**, a command-line event scheduling and management application aimed at optimizing
collaboration and time management. Users can create, view, edit, and organize events with conflict detection,
user management, and sorting capabilities.

I am primarily responsible for creating the conflict detector feature, and an admin-only feature that display all stored events with different sorting strategies.

## Code Contributed
[View my code on tP Code Dashboard](https://nus-cs2113-ay2425s2.github.io/tp-dashboard/?search=HuaZhenting&sort=groupTitle%20dsc&sortWithin=title&since=2025-02-21&timeframe=commit&mergegroup=&groupSelect=groupByRepos&breakdown=true&checkedFileTypes=docs~functional-code~test-code~other)

## **Enhancements Implemented**

### **Core Event Management Features (v1.0)**
- **Conflict Detection Feature:**
  - Called automatically with adding or editing an event. 
  - Detects and warns user of potential scheduling conflicts.
  - Outputs conflicts on venue occupied at the scheduled time.
  - **Smart Design**: One collision detection module handles both adding and editing events by using an excludeIndex (set to -1 for adding) to exclude the edited event during checks.


### **New Features and Enhancements (v2.0)**
- **Implemented Priority Filter:**
    - Developed the 'FilterCommand' to allow users to filter events assigned to him by priority.
    - Used a single class to handle different input types:
      - 1. Single priority
      - 2. A range of priorities
    - Displayed matching events in a clean, readable format through the UI.
    - Integrated debug output to facilitate troubleshooting.

- **Implemented List All Feature:**
  - Developed the 'ListAllCommand' and 'ListAllCommandFactory' to allow users to view all events in the storage with different sorting strategies.
  - Ensured this feature is only accessible by Admin users.
  - Prompted user to select sort type after entering list, enhancing interactivity.
  - Supported sorting types:
    - By *priority*** (primary), sub-sorted by end time.
    - By **start*`* or **end time** (primary), sub-sorted by priority.

### **Implementation of JUnit Tests**
- **Added comprehensive JUnit tests for:**
    - `PriorityFilterTest`
    - `ListAllCommandTest`
    - Conflict detection logic for adding/editing events in `EventSyncTest`
- Ensured coverage of edge cases.

## **Contributions to the User Guide**
- **Documented:**
    - Interactive usage of `listAll`, `filter`, and conflict resolution features.
    - Provided example inputs/outputs to assist user understanding.

## **Contributions to the Developer Guide**
- **Added:**
    - **Architecture Diagram** giving an overview of the main components and their interaction.
    - **Sequence Diagram** for:
      - `Priority Filter` feature (`FilterEvent.puml`).
      - `List All Events` feature (`ListAllEvents.puml`).
    - **Implementation Details** for conflict detection, priority filter and list all feature.
    - **Documentation Guide** providing guidance on setting up, maintaining, and styling the project's documentation.

## **Contributions to Team-Based Tasks**
- Assisted in debugging and integration.
- Reviewed and corrected issues from PE Dry Run.
- Contributed to project documentation and testing strategies.
- Participated in milestone planning and feature discussions.

## **Review/Mentoring Contributions**
- Reviewed PRs related to event listing and filtering.
- Added JavaDoc Comments for the code.
- Edited coding style.

---