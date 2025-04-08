# Khoo Shi Xian - Project Portfolio Page

## Overview
Our team developed **EventSync**, a command-line smart scheduling application that supports event creation, editing, participant management, and availability conflict resolution. It aims to streamline event coordination with robust participant-role handling and persistent storage integration.

As one of the key contributors, I was responsible for building major interaction features such as event editing, participant management, and implementing the global **exit feature** for all interactive commands (v2.1). I also maintained data integrity between participant availability and event timing logic.

## Code Contributed
[View my code on tP Code Dashboard](https://nus-cs2113-ay2425s2.github.io/tp-dashboard/?search=sxkhoo&sort=groupTitle&sortWithin=title&timeframe=commit&mergegroup=&groupSelect=groupByRepos&breakdown=true&checkedFileTypes=docs~functional-code~test-code~other&since=2025-02-21&tabOpen=true&tabType=authorship&tabAuthor=sxkhoo&tabRepo=AY2425S2-CS2113-W13-2%2Ftp%5Bmaster%5D&authorshipIsMergeGroup=false&authorshipFileTypes=docs~functional-code~test-code&authorshipIsBinaryFileTypeChecked=false&authorshipIsIgnoredFilesChecked=false)

## Enhancements Implemented

### Core Event Management Features (v1.0)
- **`EditEventCommand`**:
  - Enables administrators to modify name, time, location, and description of events.
  - Ensures start and end times are logically valid, preventing conflicts like a start time occurring after the end time or overlapping schedules.
  - Supports UI-driven step-by-step editing flow with dynamic feedback.

### New Features (v2.0)
- **`AddParticipantCommand`**:
  - Allows admin to assign participants to events.
  - Validates that the participant exists or allows on-the-fly user creation.
  - Checks participant availability and updates their time slots upon successful assignment.
  - Syncs changes to storage.

- **`ListParticipantsCommand`**:
  - Lists all participants assigned to a specific event with roles (ADMIN/MEMBER).
  - Integrated robust UI prompts to assist user in selecting the target event index.

- **`Participant` class enhancements**:
  - Developed availability slot logic including methods for assigning and unassigning time.
  - Designed consistent `equals()` and `hashCode()` methods to avoid duplication.
  - Added access level tracking, slot sorting, and time-based conflict resolution.

### Exit Feature for All Commands (v2.1)
- Implemented a **universal exit feature** across all multi-step commands (e.g., `add`, `edit`, `create`, `login`).
  - Users can type `'exit'` at any stage to cancel the current command.
  - Improved UX by eliminating user lock-in during prompts.
  - Added `UI.checkForExit()` across critical input points and validated cancelation logic gracefully.
  - Significantly enhanced system responsiveness and user control.

---

## Implementation of JUnit Tests
- Developed and maintained tests for:
  - `EditEventCommand`
  - `ParticipantTest`
  - `ListParticipantsTest`
- Validated correct update of availability during edits.
- Checked corner cases such as duplicate assignments and invalid time formats.
- Helped with the checking and debugging of other JUnit tests.

---

## Contributions to the User Guide
- **Documented:**:
  - Interactive usage of EditEvent,AddParticipants and ListParticipants.
  - Provided detailed examples and common user scenarios

---

## Contributions to the Developer Guide
- **Parser and Command System**:
  - Documented the architecture of the Logic layer, including the role of the `Parser`, `Command`, and `CommandFactory` classes.
  - Provided detailed explanations of how user input is parsed and converted into executable commands.

- Helped with the detailed explanations for:
  - **Edit Event** feature flow, validations, and participant synchronization.  
  - **Add Participant** command with participant creation fallback.
  - **List Participant** interaction and input validation.

- Contributed:
  - **Sequence and class Diagrams** for Logic
  - **PlantUML** diagrams (`.puml`) under `docs/graph/` directory.

---

## Contributions to Team-Based Tasks
- Helped with writing the skeleton of the code for first draft. 
- Actively reviewed pull requests and coordinated the merging of code.
- Helped with documentations and milestone deliverables.
- Helped refactor command structure for better testability and consistency across command factories.
- Actively coordinated feature integration with team members working on storage and parser components.

---

## Review / Mentoring Contributions
- Helped with debugging teammates' code.
- Reviewed and debugged several code in classes such as EventManager.
- Reviewed and debugged command input flow to ensure consistent `'exit'` handling.
- Provided guidance to teammates on applying assert statements and improving storage robustness.
- Conducted code walkthroughs for participant scheduling bugs and helped resolve storage syncing issues.

---

## Summary
My work covered both feature implementation and architectural support across multiple components. From editing and assigning participants to introducing a global exit flow, my contributions ensured that EventSync was interactive, robust, and user-friendly.
