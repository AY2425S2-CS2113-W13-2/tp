package participant;

import event.Event;
import exception.SyncException;
import ui.UI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import storage.UserStorage;

/**
 * Manages the participants in the system, including user authentication,
 * availability checking, and assignment to events.
 * The ParticipantManager handles user login, logout, adding new users, and checking availability for events.
 */
public class ParticipantManager {
    private ArrayList<Participant> participants;
    private Participant currentUser;
    private final UI ui;
    private final UserStorage storage;

    /**
     * Constructs a ParticipantManager with a list of participants,
     * UI instance, and storage for saving participant data.
     *
     * @param participants The list of participants.
     * @param ui The UI instance for interacting with the user.
     * @param storage The UserStorage instance for saving participant data.
     */
    public ParticipantManager(ArrayList<Participant> participants, UI ui, UserStorage storage) {
        this.participants = participants;
        this.currentUser = null;
        this.ui = ui;
        this.storage = storage;
    }

    /**
     * Gets the list of all participants.
     *
     * @return The list of participants.
     */
    public ArrayList<Participant> getParticipants() {
        return participants;
    }

    /**
     * Gets the currently logged-in participant.
     *
     * @return The current user.
     */
    public Participant getCurrentUser() {
        return currentUser;
    }

    /**
     * Adds a new participant to the system. Throws an exception if the user already exists.
     *
     * @param participant The participant to add.
     * @throws SyncException If the participant already exists.
     */
    public void addNewUser(Participant participant) throws SyncException {
        if (participants.stream().anyMatch(p -> p.getName().equals(participant.getName()))) {
            throw new SyncException("User already exists.");
        }
        participants.add(participant);
        storage.saveUsers(participants);
    }

    /**
     * Deletes a participant from the system.
     *
     * @param participant The participant to delete.
     * @throws SyncException If an error occurs while deleting.
     */
    public void deleteUser(Participant participant) throws SyncException {
        participants.remove(participant);
        storage.saveUsers(participants);
    }

    /**
     * Retrieves a participant by their username.
     *
     * @param username The username of the participant to retrieve.
     * @return The participant, or null if not found.
     * @throws SyncException If an error occurs while retrieving the participant.
     */
    public Participant getParticipant(String username) throws SyncException {
        try {
            for (Participant participant : participants) {
                if (participant.getName().equals(username)) {
                    return participant;
                }
            }
        } catch (NullPointerException e) {
            throw new SyncException(e.getMessage());
        }
        return null;
    }

    /**
     * Logs in a participant by verifying their username and password.
     * Prompts the user for their credentials and handles login success or failure.
     *
     * @return The current ParticipantManager instance after login.
     * @throws SyncException If an error occurs during login.
     */
    public ParticipantManager login() throws SyncException {
        ui.showMessage("Please enter your Username (or type 'exit' to leave)");
        String username = ui.readLine();
        ui.checkForExit(username);
        if(this.getParticipant(username) != null) {
            ui.showMessage("Please enter your password (or type 'exit' to leave)");
            String password = ui.readLine();
            ui.checkForExit(password);
            if(this.getParticipant(username).checkPassword(password)) {
                this.currentUser = this.getParticipant(username);
                ui.showSuccessLoginMessage();
                ui.showMenu();
                return this;
            } else {
                ui.showMessage("Wrong password. Do you want to login again? (yes/no)");
                if(ui.readLine().equalsIgnoreCase("yes")) {
                    return this.login();
                } else {
                    ui.showMessage("Wrong password! Session ends");
                    ui.showMessage("Please enter 'create' to create user first or 'login' to log in.");
                    return this;
                }
            }
        } else {
            ui.showMessage("User not found. Please enter 'create' to create user first!");
            return this;
        }
    }

    /**
     * Logs out the current participant, if any.
     */
    public void logout() {
        if (this.currentUser != null) {
            ui.showMessage(this.currentUser.getName() + " has logged out.");
            this.currentUser = null;
        } else {
            ui.showMessage("No user is currently logged in.");
        }
    }

    /**
     * Checks if a participant is available during the given event's time slot.
     *
     * @param event The event to check for availability.
     * @param participant The participant to check.
     * @return True if the participant is available during the event time, false otherwise.
     */
    public boolean checkParticipantAvailability(Event event, Participant participant) {
        ui.showMessage("Checking participant availability");
        for (AvailabilitySlot slot : participant.getAvailableTimes()) {
            ui.showMessage("  -" + slot.getStartTime() + " to " + slot.getEndTime());
            LocalDateTime slotStart = slot.getStartTime();
            LocalDateTime slotEnd = slot.getEndTime();

            if (event.getStartTime().isAfter(slotEnd) || event.getEndTime().isBefore(slotStart)) {
                continue;
            }
            return true; // Participant is available
        }
        ui.showParticipantSlotCollisionWarning(event, new ArrayList<>());
        return false;
    }

    /**
     * Assigns a participant to an event, ensuring the participant is not already assigned.
     *
     * @param event The event to assign the participant to.
     * @param participant The participant to assign.
     * @return True if the participant was successfully assigned, false otherwise.
     * @throws SyncException If the participant is already assigned to the event.
     */
    public boolean assignParticipant(Event event, Participant participant) throws SyncException {
        if (event.getParticipants().contains(participant)) {
            throw new SyncException("User has already been assigned to this event. Try another user/event.");
        } else {
            boolean assigned = participant.assignEventTime(event.getStartTime(), event.getEndTime());
            storage.saveUsers(participants);
            return assigned;
        }
    }

    /**
     * Sets the current logged-in participant.
     *
     * @param user The participant to set as the current user.
     */
    public void setCurrentUser(Participant user) {
        this.currentUser = user;
    }

    /**
     * Checks if the current logged-in participant is an admin.
     *
     * @return True if the current user is an admin, false otherwise.
     */
    public boolean isCurrentUserAdmin() {
        return this.currentUser.getAccessLevel() == Participant.AccessLevel.ADMIN;
    }

    /**
     * Checks if the current logged-in participant is available during the given event's time slot.
     *
     * @param event The event to check for availability.
     * @return True if the current user is available during the event time, false otherwise.
     */
    public boolean checkCurrentParticipantAvailability(Event event) {
        return this.currentUser.isAvailableDuring(event.getStartTime(), event.getEndTime());
    }

    public void updateParticipant(Participant updated) throws SyncException {
        for (int i = 0; i < participants.size(); i++) {
            if (participants.get(i).getName().equalsIgnoreCase(updated.getName())) {
                participants.set(i, updated);
                storage.saveUsers(participants);
                return;
            }
        }
        participants.add(updated);
        storage.saveUsers(participants);
    }

    public void save() throws SyncException {
        storage.saveUsers(participants);
    }

    public void save(Participant participant) throws SyncException {
        boolean found = false;

        for (int i = 0; i < participants.size(); i++) {
            if (participants.get(i).getName().equals(participant.getName())) {
                participants.set(i, participant);
                found = true;
                break;
            }
        }

        if (!found) {
            participants.add(participant);
        }

        storage.saveUsers(participants);
    }
}
