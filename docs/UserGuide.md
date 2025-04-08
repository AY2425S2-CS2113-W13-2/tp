# User Guide

## Table of Contents
1. [Introduction](#introduction)
2. [Quick Start](#quick-start)
3. [Features](#features)
    - 3.1 [`create` - Create user](#-create--creates-a-user-and-sets-availability)
    - 3.2 [`login` - Log in](#-login--log-in-as-an-existing-participant)
    - 3.3 [`logout` - Log out](#-logout--ends-the-current-user-session)
    - 3.4 [`add` - Add event](#-add--add-a-new-event)
    - 3.5 [`edit` - Edit event](#-edit--edit-details-of-an-existing-event) 
    - 3.6 [`delete` - Delete event](#-delete--delete-an-event-from-system) 
    - 3.7 [`list` - List events](#-list--list-events-assigned-to-you)
    - 3.8 [`listall` - List all events](#-listall--list-all-events-in-the-system) 
    - 3.9 [`find` - Search events](#-find-keyword--search-for-events)
    - 3.10 [`filter` - Filter events](#-filter--show-events-by-priority)
    - 3.11 [`duplicate` - Duplicate event](#-duplicate--duplicate-an-existing-event)
    - 3.12 [`addparticipant` - Add participant](#-addparticipant--assign-participant-to-event)
    - 3.13 [`listparticipants` - List participants](#-listparticipants--show-event-participants)
    - 3.14 [`bye` - Exit](#-bye--exit-the-application)
4. [FAQ](#faq)
5. [Command Summary](#command-summary)
6. [Notes](#final-notes)

## Introduction

This is a CLI-based Event Manager for organizing events, participants, and schedules. It supports priority levels, 
conflict detection, event filtering, user roles (admin/member), and participant availability. Each participant can log 
in and see or manage only their assigned events. Data is saved automatically between sessions.

---

## Quick Start

1. Ensure you have `Java 17` or above installed on your computer.
2. Download the latest `EventSync.jar`.
3. Copy the `.jar` file into any folder you want to use as the home directory for Event Manager.
4. Open a command prompt or terminal and navigate to the folder where you placed the `.jar` file.
5. Run the application using the following command:
```
java -jar EventSync.jar
```
6. Type a command and press `Enter` to execute it.

---

## Features

### 👤 `create` — Creates a user and sets availability

#### Command: `create`

**Input format:**  
You will be asked to provide:
- Name
- Password
- Access level (`ADMIN` or `MEMBER`)
- Number of available slots
- Availability slots (`yyyy-MM-dd HH:mm - yyyy-MM-dd HH:mm`)

#### Example

```plaintext
Press 'login' to log in or 'create' to create a new user.
>create

Enter participant's name (or type 'exit' to cancel): 
>Alice

Enter participant's password (or type 'exit' to cancel):
>123

Enter participant's access level (1 for Admin, 2 for Member) (or type 'exit' to cancel): 
>2

Enter number of availability slots (maximum 10) (or type 'exit' to cancel): 
>1

Enter start time for availability slot 1 (in format yyyy-MM-dd HH:mm) (or type 'exit' to cancel): 
>2025-03-31 12:00

Enter end time for availability slot 1 (in format yyyy-MM-dd HH:mm) (or type 'exit' to cancel): 
>2025-05-31 12:00

Successfully created: Alice
```

---

### ✅ `login` — Log in as an existing participant

#### Command: `login`

#### Notes: 
- You must log in to manage or view events.
- Prompts for your name and password.
- You will be asked to provide:
- Username
- Password
- Required before accessing most features.
  
#### Example for Successful Login

```plaintext
Please enter 'login' to log in or continue with your previous command.
>login

Please enter your Username (or type 'exit' to leave)
>Alice

Please enter your password (or type 'exit' to leave)
>123

Successfully logged in.
╔═════════════════════════════════════════╗
║          EVENT SYNC COMMAND MENU        ║
╠═════════════════════════════════════════╣
║  === Event Management Commands ===      ║
║  add            - Add new event         ║
║  listall        - List all events       ║
║  delete         - Delete an event       ║
║  edit           - Edit an event         ║
║  duplicate [ID] - Duplicate an event    ║
║  addparticipant - Add to event          ║
║                                         ║
║  === Participant Commands ===           ║
║  list           - List your events      ║
║  find [KEYWORD] - Search events         ║
║  filter         - Filter events         ║
║  listparticipants- List participants    ║
║                                         ║
║  === Session Commands ===               ║
║  create         - Create new user       ║
║  login          - Login to system       ║
║  logout         - Logout                ║
║                                         ║
║  === System Commands ===                ║
║  bye            - Exit program          ║
║  help           - Show help menu        ║
╚═════════════════════════════════════════╝
```
#### Example for Unsuccessful Login 

```plaintext
Please enter your Username (or type 'exit' to leave)
>Terry 

User not found. Please enter 'create' to create user first!
>login
```

```plaintext
Please enter your Username (or type 'exit' to leave)
>Alice

Please enter your password (or type 'exit' to leave)
>330

Wrong password. Do you want to login again? (yes/no)
>no

Wrong password! Session ends
```
---

### 🔒 `logout` — Ends the current user session

#### Command: `logout`

#### Notes:
- You must login first
- Another participant can log in afterward.
  
#### Example

```plaintext
>logout
Terry has logged out.
Bye! Press 'login' to log in or 'create' to create a new user.
```
---

### 📅 `add` — Add a new event

#### Command: `add`

#### Notes:
- You need to log in to an ADMIN account. 
- Adds an event to the system. Only ADMIN can add an event. The creator will be automatically added to the event.

**Input format:**
```
Event Name | Start Date | End Date | Location | Description
```
*Date/time must be in `yyyy-MM-dd HH:mm` format.*  
You will then be prompted to set the event’s priority (`HIGH`, `MEDIUM`, `LOW`).

#### Example

```plaintext
>add

Enter event details (format: Event Name | Start Date | End Date | Location | Description) (or type 'exit' to cancel): 
>Team Meeting | 2025-10-10 20:00 | 2025-10-10 21:00 | Conference Room | A team meeting to discuss project updates

Enter event priority (LOW, MEDIUM, HIGH): 
>Low

The event
+----------------------+--------------------------------+
| Name                 | Team Meeting
| Start Time           | 2025-10-10 20:00
| End Time             | 2025-10-10 21:00
| Location             | Conference Room
| Description          | A team meeting to discuss project updates
| Participants         | [Participant: Terry]
+----------------------+--------------------------------+
has been added to the list.
```
---

### ✏️ `edit` — Edit details of an existing event 

#### Command: `edit`

#### Notes:
- Only ADMIN can edit an event.
- You will be shown options to modify:
- 1. Name
- 2. Start time
- 3. End time
- 4. Location
- 5. Description

#### Example:

```plaintext
>edit

Enter event index to edit (or type 'exit' to cancel): 
>1

--- Editing Event ---
+----------------------+--------------------------------+
| Name                 | Team Meeting
| Start Time           | 2025-10-10 20:00
| End Time             | 2025-10-10 21:00
| Location             | Conference Room
| Description          | A team meeting to discuss project updates
| Participants         | [Participant: Terry]
+----------------------+--------------------------------+
What would you like to edit?
1. Name
2. Start Time (format: yyyy-MM-dd HH:mm)
3. End Time (format: yyyy-MM-dd HH:mm)
4. Location
5. Description
6. Done

Enter your choice (1-6):
>1

Enter New Event Name  (or type 'exit' to cancel): meet
✅ Name updated:

--- Editing Event ---
+----------------------+--------------------------------+
| Name                 | meet
| Start Time           | 2025-10-10 20:00
| End Time             | 2025-10-10 21:00
| Location             | Conference Room
| Description          | A team meeting to discuss project updates
| Participants         | [Participant: Terry]
+----------------------+--------------------------------+
What would you like to edit?
1. Name
2. Start Time (format: yyyy-MM-dd HH:mm)
3. End Time (format: yyyy-MM-dd HH:mm)
4. Location
5. Description
6. Done

Enter your choice (1-6):
>6

✅ Event editing completed.
````
---

### 🗑️ `delete` — Delete an event from system

#### Command: `delete`

#### Notes:
- Only ADMIN can delete an event.
- You will be prompted to search by name.
- If multiple matches are found, you can select by index.
- Confirmation is required before deletion.

```plaintext
>delete

Enter name to search for events to delete (or type 'exit' to cancel):
>meet

Matching Events:
1. meet
2. Team Meeting
Enter the index of the event you want to delete: 
>2

Confirm deletion of "Team Meeting"? (yes/no):
>yes

"Team Meeting" has been deleted.
```
---

### 📋 `list` — List events assigned to you

#### Command: `list`

#### Notes: 
- Displays all events for the logged-in participant.
- You’ll be prompted to choose a sort order:
    - `priority` — by priority, then end time
    - `start` — by start time, then priority
    - `end` — by end time, then priority
  
#### Example

```plaintext
>list

Enter your sort type (priority, start, end) or type 'exit' to cancel:
>end

The event 1 is: 
+----------------------+--------------------------------+
| Name                 | Team Meeting
| Start Time           | 2025-03-12 20:00
| End Time             | 2025-03-12 21:00
| Location             | Conference Room
| Description          | A team meeting to discuss project updates
| Participants         | [Participant: a]
+----------------------+--------------------------------+
Priority: LOW

The event 2 is: 
+----------------------+--------------------------------+
| Name                 | chill
| Start Time           | 2025-03-31 19:00
| End Time             | 2025-03-31 20:00
| Location             | Room
| Description          | A team meeting to relax
| Participants         | [Participant: a]
+----------------------+--------------------------------+
Priority: LOW
```
---

### 📋 `listall` — List all events in the system

#### Command: `listall`

#### Notes:
- You need to log in to an ADMIN account first.
- You’ll be prompted to choose a sort order:
    - `priority` — by priority, then end time
    - `start` — by start time, then priority
    - `end` — by end time, then priority
 
#### Example

```plaintext
>listall

Enter your sort type: Now we have a list of available sort types: priority, start, end 
>priority

The event 1 is: 
+----------------------+--------------------------------+
| Name                 | Team Meeting
| Start Time           | 2025-03-12 20:00
| End Time             | 2025-03-12 21:00
| Location             | Conference Room
| Description          | A team meeting to discuss project updates
| Participants         | [Participant: a]
+----------------------+--------------------------------+
Priority: LOW

The event 2 is: 
+----------------------+--------------------------------+
| Name                 | chill
| Start Time           | 2025-03-31 19:00
| End Time             | 2025-03-31 20:00
| Location             | Room
| Description          | A team meeting to relax
| Participants         | [Participant: a]
+----------------------+--------------------------------+
Priority: LOW

The event 3 is: 
+----------------------+--------------------------------+
| Name                 | meet
| Start Time           | 2025-10-10 20:00
| End Time             | 2025-10-10 21:00
| Location             | Conference Room
| Description          | A team meeting to discuss project updates
| Participants         | [Participant: Terry]
+----------------------+--------------------------------+
Priority: LOW
```
---

### 🧠 `find KEYWORD` — Search for events

#### Command : `find <KEYWORD>`

#### Notes:
- Finds events that contain the keyword in either:
    - Event name
    - Event description

#### Example : 

```plaintext
> find chill

Found 1 matching events.
Here are the matching events in your list: 
 1. +----------------------+--------------------------------+
| Name                 | chill
| Start Time           | 2025-03-31 19:00
| End Time             | 2025-03-31 20:00
| Location             | Room
| Description          | A team meeting to relax
| Participants         | [Participant: a]
+----------------------+--------------------------------+
```
---

### 🎯 `filter` — Show events by priority

#### Command : `filter`

#### Notes:
- Filters and displays events by either:
    - A certain priority
    - A priority range

**Input format:**: 
- filter {priority}
- filter {lower-priority} {higher-priority}

Valid values: `HIGH`, `MEDIUM`, `LOW`.

#### Example for certain priority 
```plaintext 
>filter

Enter a priority or a range (or type 'exit' to cancel):
>HIGH

Found 1 matching events.
Here are the matching events in your list: 
 1. +----------------------+--------------------------------+
| Name                 | coffee
| Start Time           | 2025-03-20 15:00
| End Time             | 2025-03-20 16:00
| Location             | Conference Room
| Description          | Coffee chat
| Participants         | [Participant: a]
+----------------------+--------------------------------+
```
#### Example for Range

```plaintext 
>filter

Enter a priority or a range (or type 'exit' to cancel):
>LOW HIGH

Found 2 matching events.
Here are the matching events in your list: 
 1. +----------------------+--------------------------------+
| Name                 | Team Meeting
| Start Time           | 2025-03-12 20:00
| End Time             | 2025-03-12 21:00
| Location             | Conference Room
| Description          | A team meeting to discuss project updates
| Participants         | [Participant: a]
+----------------------+--------------------------------+
2. +----------------------+--------------------------------+
| Name                 | coffee
| Start Time           | 2025-03-20 15:00
| End Time             | 2025-03-20 16:00
| Location             | Conference Room
| Description          | Coffee chat
| Participants         | [Participant: a]
+----------------------+--------------------------------+
```
---

### 📎 `duplicate` — Duplicate an existing event

#### Command : `duplicate`

#### Notes:
- Clones an existing event and gives it a new name. Only ADMIN can duplicate an event.
- You will be prompted to enter:
    - Event index
    - New name
- The copy retains all original details and priority.

#### Example : 

```plaintext
> duplicate

Enter duplicate command (format: <index> <New Event Name>)(or type 'exit' to cancel):
>1 new

Event duplicated: +----------------------+--------------------------------+
| Name                 | new
| Start Time           | 2025-10-10 20:00
| End Time             | 2025-10-10 21:00
| Location             | Conference Room
| Description          | A team meeting to discuss project updates
| Participants         | [Participant: Terry]
+----------------------+--------------------------------+
```
---

### 🙋 `addparticipant` — Assign participant to event

#### Command : `addparticipant`

#### Notes:
- Adds a participant to an event if available.
- Only ADMIN can add a participant. 

#### Example : ✅ Successful Addition

```plaintext
>addparticipant

Available Events:
1. meet
2. Team Meeting
3. chill
4. coffee
5. new
6. ret
Available Participants:
- Alice
- Terry
- a
- gg
Use: <EventIndex> | <Participant Name>
Type 'exit' to cancel.
> 6 | gg

Event Index: 5
Event Start Time: 2025-03-20T16:30
Event End Time: 2025-03-20T17:00
Checking participant availability
  -2025-03-20T14:00 to 2025-03-20T19:00
Participant gg has been added.
```

#### Example : Fail Addition
```plaintext
>addparticipant
5|Alice

Event Index: 4
Event Start Time: 2025-10-10T20:00
Event End Time: 2025-10-10T21:00
Checking participant availability
  -2025-03-31T12:00 to 2025-05-31T12:00
Warning: Scheduling Conflict
Participants are not able to attend
Please find another participant
Participant Alice is unavailable during the event.Enter 'addparticipant' to try again or try other features.
````
### 🧑‍🤝‍🧑 `listparticipants` — Show event participants

#### Command : `listparticipants`

#### Notes:
- Lists participants of a selected event.
- You will be prompted to enter the index of the event.


#### Example:
```plaintext
>listparticipants

Available Events:
1. meet
2. Team Meeting
3. chill
4. coffee
5. new
6. ret
Enter event index to list participants (or type 'exit' to cancel):
>6

Participants for event "ret":
- Participant: a
- Participant: gg
````
---

### 👋 `bye` — Exit the application

#### Command : 'bye'

#### Notes
Saves all data and exits the program safely.

#### Example : 

```plaintext
> bye

Bye!
```
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

| Command(Then follow the guidance) | Description                          |
|-----------------------------------|--------------------------------------|
| `login`                           | Log in as an existing user           |
| `create`                          | Create a new participant             |
| `logout`                          | Log out of the session               |
| `add`                             | Add a new event                      |
| `edit`                            | Edit an event (admin only)           |
| `delete`                          | Delete an event                      |
| `duplicate`                       | Duplicate an event                   |
| `list`                            | List your assigned events            |
| `listall`                         | List all events (admin only)         |
| `find KEYWORD`                    | Search events by name or description |
| `filter`                          | Filter events by priority            |
| `addparticipant`                  | Add a participant to an event        |
| `listparticipants`                | List all participants for an event   |
| `bye`                             | Exit the program                     |

---

## Final Notes

- All changes are saved automatically after every command.
- Participant availability is validated before adding to events.
- Commands must be entered exactly as shown (case-insensitive).
- Events are visible only to assigned participants.
