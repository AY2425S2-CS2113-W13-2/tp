# User Guide

## Table of Contents
1. [Introduction](#introduction)
2. [Quick Start](#quick-start)
3. [Features](#featurescommand-you-need-to-enter---description)
    - 3.1 [`create` - Create user](#-create--create-a-new-participant)
    - 3.2 [`login` - Log in](#-login--log-in-as-an-existing-participant)
    - 3.3 [`logout` - Log out](#-logout--log-out-of-your-session)
    - 3.4 [`add` - Add event](#-add--add-a-new-event)
    - 3.5 [`edit` - Edit event](#-edit--edit-an-existing-event-admin-only)
    - 3.6 [`delete` - Delete event](#-delete--delete-an-event)
    - 3.7 [`list` - List events](#-list--list-events-assigned-to-you)
    - 3.8 [`listall` - List all events](#-listall--list-all-events-admin-only)
    - 3.9 [`find` - Search events](#-find-keyword--search-for-events)
    - 3.10 [`filter` - Filter events](#-filter--show-events-by-priority)
    - 3.11 [`duplicate` - Duplicate event](#-duplicate--duplicate-an-event)
    - 3.12 [`addparticipant` - Add participant](#-addparticipant--assign-participant-to-event)
    - 3.13 [`listparticipants` - List participants](#-listparticipants--show-event-participants)
    - 3.14 [`bye` - Exit](#-bye--exit-the-application)
4. [FAQ](#faq)
5. [Command Summary](#command-summary)
6. [Notes](#notes)

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

## Features(command you need to enter - description)

### 👤 `create` — Create a new participant

#### Command: `create`

#### Example

```plaintext
User not found. Please create user first!
>create

Enter participant's name: 
>Alice

Enter participant's password: 
>123

Enter participant's access level (1 for Admin, 2 for Member): 
>2

Enter number of availability slots (maximum 10): 
>1

Enter start time for availability slot 1 (in format yyyy-MM-dd HH:mm): 
>2025-03-31 12:00

Enter end time for availability slot 1 (in format yyyy-MM-dd HH:mm): 
>2025-05-31 12:00

Successfully created: Alice
```

#### Notes: `create`
Creates a user (admin or member) and sets availability.

**Input format:**  
You will be asked to provide:
- Name
- Password
- Access level (`ADMIN` or `MEMBER`)
- Number of available slots
- Availability slots (`yyyy-MM-dd HH:mm - yyyy-MM-dd HH:mm`)

---

### ✅ `login` — Log in as an existing participant

#### Command: `login`

#### Example

```plaintext
Welcome to EventSync!
Press 'login' to log in or 'create' to create a new user.
>login

Please enter your Username
>alice

User not found. Please create user first!
>login

Please enter your Username
>Terry

Please enter your password
>123

Wrong password. Do you want to login again? (yes/no)
>yes

Please enter your Username
>hii

Please enter your password
>123

Successfully logged in.
╔═════════════════════════════════════════╗
║          EVENT SYNC COMMAND MENU        ║
╠═════════════════════════════════════════╣
║  === Event Management Commands ===      ║
║  add            - Add new event         ║
║  list           - List your events      ║
║  listall        - List all events       ║
║  delete [INDEX] - Delete an event       ║
║  edit [INDEX]   - Edit an event         ║
║  duplicate [ID] - Duplicate an event    ║
║  find [KEYWORD] - Search events         ║
║  filter         - Filter events         ║
║                                         ║
║  === Participant Commands ===           ║
║  addparticipant - Add to event          ║
║  listparticipants- List participants    ║
║  create         - Create new user       ║
║                                         ║
║  === Session Commands ===               ║
║  login          - Login to system       ║
║  logout         - Logout                ║
║                                         ║
║  === System Commands ===                ║
║  bye            - Exit program          ║
║  help           - Show this menu        ║
╚════════════════════════════════════════╝
```

#### Notes: `login`
- You must log in to manage or view events.
- Prompts for your name and password.
- You will be asked to provide:
- Username
- Password
- Required before accessing most features.
---

### 🔒 `logout` — Log out of your session

#### Command: `logout`

#### Example

```plaintext
>logout
Terry has logged out.
Bye! Press 'login' to log in or 'create' to create a new user.
```

#### Notes: `logout`
Need to log in first.
Ends the current user session.  
Another participant can log in afterward.

---

### 📅 `add` — Add a new event

#### Command: `add`

#### Example

```plaintext
>add

Enter event details (format: Event Name | Start Date | End Date | Location | Description): 
>Team Meeting | 2020-05-10 14:00 | 2020-05-10 16:00 | Conference Room | A team meeting to discuss project updates

Enter event priority (LOW, MEDIUM, HIGH): 
>Low

The event
+----------------------+--------------------------------+
| Name                 | Team Meeting
| Start Time           | 2020-05-10 14:00
| End Time             | 2020-05-10 16:00
| Location             | Conference Room
| Description          | A team meeting to discuss project updates
| Participants         | [Participant: Terry (Available: 2 slots)]
+----------------------+--------------------------------+
has been added to the list.
```

#### Notes: `add`
You need to log in to an ADMIN account first.
Adds an event to the system. Only ADMIN can add an event. The creator will be automatically added to the event.

**Input format:**
```
Event Name | Start Date | End Date | Location | Description
```
---
*Date/time must be in `yyyy-MM-dd HH:mm` format.*  
You will then be prompted to set the event’s priority (`HIGH`, `MEDIUM`, `LOW`).

---

### ✏️ `edit` — Edit an existing event (admin only)

Allows an admin to change event fields. Only ADMIN can edit an event.
You will be shown options to modify:
1. Name
2. Start time
3. End time
4. Location
5. Description

#### Example:

```plaintext
>edit

Enter event index to edit: 
>7

Editing Event: Chill
1. Edit Name
2. Edit Start Time
3. Edit End Time
4. Edit Location
5. Edit Description
6. Done Editing
Select an option: 
>1

Enter New Event Name: 
>Chillingg

Updated Event Details:
+----------------------+--------------------------------+
| Name                 | Chillingg
| Start Time           | 2020-01-01 10:00
| End Time             | 2020-01-02 10:00
| Location             | Home
| Description          | chill
| Participants         | [Participant: Jacky (Available: 1 slots), Participant: jack (Available: 3 slots)]
+----------------------+--------------------------------+

Editing Event: Chillingg
1. Edit Name
2. Edit Start Time
3. Edit End Time
4. Edit Location
5. Edit Description
6. Done Editing
Select an option: 
````
---

### 🗑️ `delete` — Delete an event

Removes an event from the system. Only ADMIN can delete an event.

- Prompts you to search by name.
- If multiple matches are found, you can select by index.
- Confirmation is required before deletion.

---

### 📋 `list` — List events assigned to you

#### Command: `list`

#### Example

```plaintext
>list

Enter your sort type: Now we have a list of available sort types: priority, start, end 
>end

The event 1 is: 
+----------------------+--------------------------------+
| Name                 | Team Meeting
| Start Time           | 2020-05-10 14:00
| End Time             | 2020-05-10 16:00
| Location             | Conference Room
| Description          | A team meeting to discuss project updates
| Participants         | [Participant: Terry (Available: 2 slots)]
+----------------------+--------------------------------+
Priority: LOW

The event 2 is: 
+----------------------+--------------------------------+
| Name                 | event a
| Start Time           | 2023-05-23 16:00
| End Time             | 2023-05-24 17:00
| Location             | sdfsf
| Description          | sdfsf
| Participants         | [Participant: Terry (Available: 2 slots)]
+----------------------+--------------------------------+
Priority: LOW
```

#### Notes: `list`
Displays all events for the logged-in participant.

You’ll be prompted to choose a sort order:
- `priority` — by priority, then end time
- `start` — by start time, then priority
- `end` — by end time, then priority

---

### 📋 `listall` — List all events in the storage to you

#### Command: `listall`

#### Example

```plaintext
>listall

Enter your sort type: Now we have a list of available sort types: priority, start, end 
>end

The event 1 is: 
+----------------------+--------------------------------+
| Name                 | Team Meeting
| Start Time           | 2020-05-10 14:00
| End Time             | 2020-05-10 16:00
| Location             | Conference Room
| Description          | A team meeting to discuss project updates
+----------------------+--------------------------------+
Priority: LOW

#### Notes: `listall`
Displays all events in the storage only for admin level users.

You’ll be prompted to choose a sort order:
- `priority` — by priority, then end time
- `start` — by start time, then priority
- `end` — by end time, then priority
```
---

### 🧠 `find KEYWORD` — Search for events

Finds events that contain the keyword in either:
- Event name
- Event description

#### Command : `find <KEYWORD>`

#### Example : 

```plaintext
> find team
Searching for 'team' in list:
Found 1 matching event.
Here are the matching events in your list.

1. +----------------------+--------------------------------+
| Name                 | Team Meeting
| Start Time           | 2020-05-10 14:00
| End Time             | 2020-05-10 16:00
| Location             | Conference Room
| Description          | A team meeting to discuss project updates
+----------------------+--------------------------------+
Priority: LOW
```
---

### 🎯 `filter` — Show events by priority

Filters and displays events by priority range.

**Input format:**: filter {lower-priority} {higher-priority}
#### Example
```
LOW MEDIUM
```
Valid values: `HIGH`, `MEDIUM`, `LOW`.

---

### 📎 `duplicate` — Duplicate an event

Clones an existing event and gives it a new name. Only ADMIN can duplicate an event.

You will be prompted to enter:
- Event index
- New name

The copy retains all original details and priority.

#### Command : 'duplicate'

#### Example : 

```plaintext
> duplicate

Enter duplicate command (format: <index> <New Event Name>):
`3 new team`

Event duplicated : 
+----------------------+--------------------------------+
| Name                 | new team
| Start Time           | 2020-05-10 14:00
| End Time             | 2020-05-10 16:00
| Location             | Conference Room
| Description          | A team meeting to discuss project updates
+----------------------+--------------------------------+
Priority: LOW
```
---

### 🙋 `addparticipant` — Assign participant to event

Adds a participant to an event if available. Only ADMIN can add a participant. 
The list of events and users will be shown.


#### Example 1: ✅ Successful Addition

```plaintext
>addparticipant

Available Events:
1. event a
2. Meeting
3. Team Meeting
4. Team Meeting
5. Meeting
6. MEETUP
7. Chill

Available Participants:
- hii
- Terry
- jack
- Alice
- Jacky

Use: <EventIndex> | <Participant Name>
>7|jack

Event Index : 6
Event Start Time : 2020-01-01T10:00
Event End Time : 2020-01-02T10:00
Checking participant availability
  -2000-12-12T12:00 to 2023-03-03T15:59
Participant jack has been added.


#### Example 2 : Fail Addition
>addparticipant
7|ye

Event Index : 6
Event Start Time : 2020-01-01T10:00
Event End Time : 2020-01-02T10:00
Participant 'ye' does not exist. Create a new one? (Y/N)
>N
Operation cancelled.

````
### 🧑‍🤝‍🧑 `listparticipants` — Show event participants

Lists participants of a selected event.

You will be prompted to enter the index of the event.

#### Example 1 : Successful list
```plaintext
>listparticipants

Enter event index to list participants:
>7

Participants for event "Chill":
- Participant: Jacky (Available: 1 slots)
- Participant: jack (Available: 3 slots)
````
---

### 👋 `bye` — Exit the application

Saves all data and exits the program safely.

#### Command : 'bye'

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

## Notes

- All changes are saved automatically after every command.
- Participant availability is validated before adding to events.
- Commands must be entered exactly as shown (case-insensitive).
- Events are visible only to assigned participants.
