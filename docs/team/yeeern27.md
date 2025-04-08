# Goh Yee Ern - Project Portfolio Page

## Project Overview

Our team developed EventSync, a command-line event scheduling and management application aimed at optimizing collaboration and time management. Users can create, view, edit, and organize events with conflict detection, user management, and sorting capabilities.

I am responsible for creating the find feature, duplicate event feature, and to allocate participants based on their available schedule. 


## Code Contribution

[View my code contribution](https://nus-cs2113-ay2425s2.github.io/tp-dashboard/?search=W13&sort=groupTitle&sortWithin=title&timeframe=commit&mergegroup=&groupSelect=groupByRepos&breakdown=true&checkedFileTypes=docs~functional-code~test-code~other&since=2025-02-21&tabOpen=true&tabType=authorship&tabAuthor=yeeern27&tabRepo=AY2425S2-CS2113-W13-2%2Ftp%5Bmaster%5D&authorshipIsMergeGroup=false&authorshipFileTypes=docs~functional-code~test-code&authorshipIsBinaryFileTypeChecked=false&authorshipIsIgnoredFilesChecked=false)

## Enhanced Implementation

### Core Event Management Feature (v1.0) 

- **Find Feature** : 
   - Implemented the `FindCommand` to allow users to find event with keywords.
   - Performed case-insenstive mathcing against both event names and description.
   - Displayed all matching events to the user in a clearn, readable format via UI.
   - Handled whitespace and formatting inconsistencies by trimming input and event data.
   - Included debug output for easier troubleshooting.
 
- **Duplicate Event Feature** :
   - Implemented the `DuplicateCommand` to allow users to create a copy of an existing event with a new name.
   - Ensured that the duplicated event is properly added to the event manager.
   - Provided user feedback via the UI to confirm successful duplication.
 
 ### New Event Management Feature (v2.0) 
 
 - **Availability Conflict Detection** :

    - Implemented availability checking logic to ensure particpants can only be added to the events if their schedule perimits.
    - Determined overlap by checking for non-collision between the participant's available time and the event duration.
    - Intergrated debug print statements to assist with runtime verification of availability.
    - Displayed a warning via the UI when the participant is not free for the event.
    - Integrated with the `addparticipant` command for execution.
  

### Implementation of JUnit Test Case 
- **Added JUnit test case for** :
    - DuplicateEventTest.java
    - FilterCommandFactoryTest.java
    - PrintMatchingEventsTest.java
    - UITest.java
 
- Ensure coverage of edge case.

## Contribution to User Guide 
- Added features for `find` and `duplicate` command. 
- Contributed to the Features section by including the examples for each command based on the latest version of code.
- Checked and finalised the User Guide.

## Contribution to Developer Guide 
- Contributed to the UI section by listing the different methods in the UI and their functions. 
- Contributed to generating the UI diagram.
  
## Contribution to Team-based task  
  - Helped to write the skeleton of the code for first draft.
  - Reviewed and coordinated the merging of code.
  - Helped with documentations and milestone deliverables.
  - Edited the coding style to ensure it matches with the standard coding style.

## Review / Mentoring Contribution  
  - Assisted with debugging of teammates' code.
  - Reviewed and debugged `listparticipants` code.
    


  
