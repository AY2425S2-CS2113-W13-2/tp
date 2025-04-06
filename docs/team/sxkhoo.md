# Khoo Shi Xian - Project Portfolio Page

## Project Overview
Our team developed EventSync, a command-line event scheduling and management application aimed at optimizing collaboration and time management. Users can create, view, edit, and organize events with conflict detection, user management, and sorting capabilities.

I am responsible for creating the edit event feature, add participant feature, and to list participants of a specific event.

## Code Contribution

[View my code contribution](https://nus-cs2113-ay2425s2.github.io/tp-dashboard/?search=W13&sort=groupTitle&sortWithin=title&timeframe=commit&mergegroup=&groupSelect=groupByRepos&breakdown=true&checkedFileTypes=docs~functional-code~test-code~other&since=2025-02-21&tabOpen=true&tabType=authorship&tabAuthor=sxkhoo&tabRepo=AY2425S2-CS2113-W13-2%2Ftp%5Bmaster%5D&authorshipIsMergeGroup=false&authorshipFileTypes=docs~functional-code~test-code&authorshipIsBinaryFileTypeChecked=false&authorshipIsIgnoredFilesChecked=false)
## Enhanced Implementation

### Core Event Management Feature (v1.0)

- **Edit Event Feature**:
    - Implemented the `EditEventCommand` to allow users to edit existing events.
    - Provided the ability to update event details such as name, description, and timing.
    - Validated input and ensured events were correctly updated in the event manager.
    - Provided feedback to the user via the UI when an event was successfully edited.

### New Event Management Feature (v2.0)

- **Add Participant Feature**:
    - Implemented the `AddParticipantCommand` to allow users to add participants to events.
    - Ensured proper validation that only available participants are added to the event.
    - Provided clear feedback to the user via the UI when a participant is successfully added.

- **List Participant Feature**:
    - Implemented the `ListParticipantCommand` to display all participants of a specific event.
    - Ensured that the participant list is displayed in a clear, readable format for users.

- **Participant Class**:
    - Created the `Participant` class to manage participant data such as username, password, availability, and access levels.
    - Ensured the class included functionality for managing participant information and availability.
    - Integrated the `Participant` class with the add and list participant features.

### Implementation of JUnit Test Case
- **Added JUnit test case for**:
    - `EditEvent.java`
    - `ParticipantTest.java`
  
    - Ensured coverage of edge cases.

## Contribution to User Guide
- Contributed sections for the **Add Participant** and **Edit Event** features.
- Provided detailed steps and examples for how users can add participants to events and edit event details.

## Contribution to Developer Guide

### Logic Diagram for Parser

- Contributed to the **Parser** section of the Developer Guide by creating both **class** and **sequence** diagrams to describe how the **Parser** interacts with the **CommandParser**, **Command**, and **CommandFactory** classes.
- The **class diagram** illustrates the structure and relationships between these components, showcasing the flow and dependencies among them.
- The **sequence diagram** visualizes the interactions between the classes during runtime, demonstrating how commands are parsed and executed in the system.

## Contribution to Team-Based Tasks

## Review / Mentoring Contributions
