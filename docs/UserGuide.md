# User Guide

## Introduction

This is a CLI-based Event Manager for organizing events, participants, and schedules. It supports priority levels, 
conflict detection, event filtering, user roles (admin/member), and participant availability. Each participant can log 
in and see or manage only their assigned events. Data is saved automatically between sessions.

---

## Quick Start

1. Ensure you have `Java 17` or above installed on your computer.
2. Download the latest `EventManager.jar`.
3. Copy the `.jar` file into any folder you want to use as the home directory for Event Manager.
4. Open a command prompt or terminal and navigate to the folder where you placed the `.jar` file.
5. Run the application using the following command:
```
java -jar EventManager.jar
```
6. Type a command and press `Enter` to execute it.

---

## Features

### ✅ `login` — Log in as an existing participant

You must log in to manage or view events.

- Prompts for your name and password.
- Required before accessing most features.

---

### 👤 `create` — Create a new participant

Creates a user (admin or member) and sets availability.

**Input format:**  
You will be asked to provide:
- Name
- Password
- Access level (`ADMIN` or `MEMBER`)
- Availability slots (`yyyy-MM-dd HH:mm - yyyy-MM-dd HH:mm`)

---

### 🔒 `logout` — Log out of your session

Ends the current user session.  
Another participant can log in afterward.

---

### 📅 `add` — Add a new event

Adds an event to the system.

**Input format:**
```
Event Name | Start Date | End Date | Location | Description
```
---
*Date/time must be in `yyyy-MM-dd HH:mm` format.*  
You will then be prompted to set the event’s priority (`HIGH`, `MEDIUM`, `LOW`).

---

### ✏️ `edit` — Edit an existing event (admin only)

Allows an admin to change event fields.  
You will be shown options to modify:
1. Name
2. Start time
3. End time
4. Location
5. Description

---

### 🗑️ `delete` — Delete an event

Removes an event from the system.

- Prompts you to search by name.
- If multiple matches are found, you can select by index.
- Confirmation is required before deletion.

---

### 📋 `list` — List events assigned to you

Displays all events for the logged-in participant.

You’ll be prompted to choose a sort order:
- `priority` — by priority, then end time
- `start` — by start time, then priority
- `end` — by end time, then priority

---

### 🧠 `find KEYWORD` — Search for events

Finds events that contain the keyword in either:
- Event name
- Event description

---

### 🎯 `filter` — Show events by priority

Filters and displays events by priority range.

**Input format:**
```
LOW MEDIUM
```
Valid values: `HIGH`, `MEDIUM`, `LOW`.

---

### 📎 `duplicate` — Duplicate an event

Clones an existing event and gives it a new name.

You will be prompted to enter:
- Event index
- New name

The copy retains all original details and priority.

---

### 🙋 `addparticipant` — Assign participant to event

Adds a participant to an event if available.

**Input format:**
```
EventIndex | ParticipantName
```
- Availability is checked before assigning.
- If the participant does not exist, you’ll be asked to create them.

---

### 🧑‍🤝‍🧑 `listparticipants` — Show event participants

Lists participants of a selected event.

You will be prompted to enter the index of the event.

---

### 👋 `bye` — Exit the application

Saves all data and exits the program safely.

---

### ❓ `help` — Display the command menu

Shows a command summary with descriptions for each.

---

## FAQ

**Q:** What format should dates follow?  
**A:** `yyyy-MM-dd HH:mm` — e.g., `2025-04-10 18:00`

**Q:** Why can't I see any events after logging in?  
**A:** You can only view events assigned to you. Ask an admin to add you as a participant.

**Q:** Can a member user edit events?  
**A:** No, only participants with the `ADMIN` role can edit event details.

---

## Command Summary

| Command                  | Description                                 |
|--------------------------|---------------------------------------------|
| `login`                 | Log in as an existing user                  |
| `create`                | Create a new participant                    |
| `logout`                | Log out of the session                      |
| `add`                   | Add a new event                             |
| `edit`                  | Edit an event (admin only)                  |
| `delete`                | Delete an event                             |
| `duplicate`             | Duplicate an event                          |
| `list`                  | List your assigned events                   |
| `find KEYWORD`          | Search events by name or description        |
| `filter`                | Filter events by priority                   |
| `addparticipant`        | Add a participant to an event               |
| `listparticipants`      | List all participants for an event          |
| `bye`                   | Exit the program                            |
| `help`                  | Show command summary                        |

---

## Notes

- All changes are saved automatically after every command.
- Participant availability is validated before adding to events.
- Commands must be entered exactly as shown (case-insensitive).
- Events are visible only to assigned participants.