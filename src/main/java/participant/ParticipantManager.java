package participant;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import event.Event;
import exception.SyncException;
import ui.UI;
import storage.UserStorage;

/**
 * Manages participant-related operations including user authentication,
 * event assignment, and user management.
 */
public class ParticipantManager {
    private static final Logger LOGGER = Logger.getLogger(ParticipantManager.class.getName());

    private ArrayList<Participant> participants;
    private Participant currentUser;
    private final UI ui;
    private final UserStorage storage;

    /**
     * Constructs a ParticipantManager with initial participants, UI, and storage.
     *
     * @param participants Initial list of participants
     * @param ui User interface for interactions
     * @param storage Storage mechanism for user data
     */
    public ParticipantManager(ArrayList<Participant> participants, UI ui, UserStorage storage) {
        this.participants = participants;
        this.currentUser = null;
        this.ui = ui;
        this.storage = storage;

        LOGGER.log(Level.INFO, "ParticipantManager initialized with {0} participants", participants.size());
    }

    /**
     * Retrieves the list of all participants.
     *
     * @return ArrayList of all participants
     */
    public ArrayList<Participant> getParticipants() {
        return new ArrayList<>(participants);
    }

    /**
     * Gets the currently logged-in user.
     *
     * @return Current user, or null if no user is logged in
     */
    public Participant getCurrentUser() {
        return currentUser;
    }

    /**
     * Adds a new user to the system.
     *
     * @param participant New participant to add
     * @throws SyncException if user already exists
     */
    public void addNewUser(Participant participant) throws SyncException {
        assert participant != null : "Participant cannot be null";

        if (participants.stream().anyMatch(p -> p.getName().equals(participant.getName()))) {
            LOGGER.log(Level.WARNING, "Attempt to add duplicate user: {0}", participant.getName());
            throw new SyncException("User already exists.");
        }

        participants.add(participant);
        storage.saveUsers(participants);
        LOGGER.log(Level.INFO, "New user added: {0}", participant.getName());
    }

    /**
     * Deletes a user from the system.
     *
     * @param participant Participant to delete
     * @throws SyncException if deletion fails
     */
    public void deleteUser(Participant participant) throws SyncException {
        assert participant != null : "Participant cannot be null";

        participants.remove(participant);
        storage.saveUsers(participants);
        LOGGER.log(Level.INFO, "User deleted: {0}", participant.getName());
    }

    /**
     * Retrieves a participant by username.
     *
     * @param username Username to search for
     * @return Matching participant, or null if not found
     * @throws SyncException if an error occurs during retrieval
     */
    public Participant getParticipant(String username) throws SyncException {
        assert username != null : "Username cannot be null";

        try {
            for (Participant participant : participants) {
                if (participant.getName().equals(username)) {
                    return participant;
                }
            }
        } catch (NullPointerException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving participant", e);
            throw new SyncException(e.getMessage());
        }

        LOGGER.log(Level.INFO, "Participant not found: {0}", username);
        return null;
    }

    /**
     * Handles user login process.
     *
     * @return Updated ParticipantManager after login attempt
     * @throws SyncException if login fails
     */
    public ParticipantManager login() throws SyncException {
        ui.showMessage("Please enter your Username (or type 'exit' to leave)");
        String username = ui.readLine();
        ui.checkForExit(username);

        Participant participant = this.getParticipant(username);
        if (participant != null) {
            ui.showMessage("Please enter your password (or type 'exit' to leave)");
            String password = ui.readLine();
            ui.checkForExit(password);

            if (participant.checkPassword(password)) {
                this.currentUser = participant;
                LOGGER.log(Level.INFO, "User logged in: {0}", username);
                ui.showSuccessLoginMessage();
                ui.showMenu();
                return this;
            } else {
                LOGGER.log(Level.WARNING, "Failed login attempt for user: {0}", username);
                ui.showMessage("Wrong password. Do you want to login again? (yes/no)");
                if (ui.readLine().equalsIgnoreCase("yes")) {
                    return this.login();
                } else {
                    ui.showMessage("Wrong password! Session ends");
                    ui.showMessage("Please enter 'create' to create user first or 'login' to log in.");
                    return this;
                }
            }
        } else {
            LOGGER.log(Level.INFO, "Login attempt for non-existent user: {0}", username);
            ui.showMessage("User not found. Please enter 'create' to create user first!");
            return this;
        }
    }

    /**
     * Logs out the current user.
     */
    public void logout() {
        if (this.currentUser != null) {
            LOGGER.log(Level.INFO, "User logged out: {0}", this.currentUser.getName());
            ui.showMessage(this.currentUser.getName() + " has logged out.");
            this.currentUser = null;
        } else {
            LOGGER.log(Level.INFO, "Logout attempt when no user is logged in");
            ui.showMessage("No user is currently logged in.");
        }
    }

    /**
     * Checks participant availability for a specific event.
     *
     * @param event Event to check availability against
     * @param participant Participant to check
     * @return true if participant is available, false otherwise
     */
    public boolean checkParticipantAvailability(Event event, Participant participant) {
        assert event != null : "Event cannot be null";
        assert participant != null : "Participant cannot be null";

        LOGGER.log(Level.INFO, "Checking availability for participant: {0}", participant.getName());
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

        LOGGER.log(Level.INFO, "Participant not available: {0}", participant.getName());
        ui.showParticipantSlotCollisionWarning(event, new ArrayList<>());
        return false;
    }

    /**
     * Assigns a participant to an event.
     *
     * @param event Event to assign participant to
     * @param participant Participant to assign
     * @return true if assignment is successful, false otherwise
     * @throws SyncException if participant is already assigned to the event
     */
    public boolean assignParticipant(Event event, Participant participant) throws SyncException {
        assert event != null : "Event cannot be null";
        assert participant != null : "Participant cannot be null";

        if (event.getParticipants().contains(participant)) {
            LOGGER.log(Level.WARNING, "Duplicate participant assignment attempt");
            throw new SyncException("User has already been assigned to this event. Try another user/event.");
        } else {
            boolean assigned = participant.assignEventTime(event.getStartTime(), event.getEndTime());
            storage.saveUsers(participants);

            LOGGER.log(Level.INFO, "Participant {0} assigned to event", participant.getName());
            return assigned;
        }
    }

    /**
     * Sets the current user directly.
     *
     * @param user User to set as current
     */
    public void setCurrentUser(Participant user) {
        this.currentUser = user;
    }

    /**
     * Checks if the current user is an admin.
     *
     * @return true if current user is an admin, false otherwise
     */
    public boolean isCurrentUserAdmin() {
        return this.currentUser != null &&
                this.currentUser.getAccessLevel() == Participant.AccessLevel.ADMIN;
    }

    /**
     * Checks if the current participant is available during an event.
     *
     * @param event Event to check availability against
     * @return true if current participant is available, false otherwise
     */
    public boolean checkCurrentParticipantAvailability(Event event) {
        assert event != null : "Event cannot be null";

        return this.currentUser.isAvailableDuring(event.getStartTime(), event.getEndTime());
    }

    /**
     * Updates an existing participant or adds a new one if not found.
     *
     * @param updated Participant to update or add
     * @throws SyncException if saving fails
     */
    public void updateParticipant(Participant updated) throws SyncException {
        assert updated != null : "Updated participant cannot be null";

        for (int i = 0; i < participants.size(); i++) {
            if (participants.get(i).getName().equalsIgnoreCase(updated.getName())) {
                participants.set(i, updated);
                storage.saveUsers(participants);
                LOGGER.log(Level.INFO, "Participant updated: {0}", updated.getName());
                return;
            }
        }

        participants.add(updated);
        storage.saveUsers(participants);
        LOGGER.log(Level.INFO, "New participant added during update: {0}", updated.getName());
    }

    /**
     * Saves all participants to storage.
     *
     * @throws SyncException if saving fails
     */
    public void save() throws SyncException {
        storage.saveUsers(participants);
        LOGGER.log(Level.INFO, "All participants saved, total count: {0}", participants.size());
    }

    /**
     * Saves a specific participant, updating or adding as necessary.
     *
     * @param participant Participant to save
     * @throws SyncException if saving fails
     */
    public void save(Participant participant) throws SyncException {
        assert participant != null : "Participant cannot be null";

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
        LOGGER.log(Level.INFO, "Participant saved: {0}, Found in existing list: {1}",
                new Object[]{participant.getName(), found});
    }
}
