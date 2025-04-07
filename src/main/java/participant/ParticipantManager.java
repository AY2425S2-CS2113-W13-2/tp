package participant;

import event.Event;
import exception.SyncException;
import ui.UI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Scanner;
import storage.UserStorage;

public class ParticipantManager {
    private ArrayList<Participant> participants;
    private Participant currentUser;
    private final UI ui;
    private final UserStorage storage;

    public ParticipantManager(ArrayList<Participant> participants, UI ui, UserStorage storage) {
        this.participants = participants;
        this.currentUser = null;
        this.ui = ui;
        this.storage = storage;
    }

    public ArrayList<Participant> getParticipants() {
        return participants;
    }

    public Participant getCurrentUser() {
        return currentUser;
    }

    public void addNewUser(Participant participant) throws SyncException {
        if (participants.stream().anyMatch(p -> p.getName().equals(participant.getName()))) {
            throw new SyncException("User already exists.");
        }
        participants.add(participant);
        storage.saveUsers(participants);
    }

    public void deleteUser(Participant participant) throws SyncException {
        participants.remove(participant);
        storage.saveUsers(participants);
    }

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

    public ParticipantManager login() throws SyncException {
        ui.showMessage("Please enter your Username");
        String username = ui.readLine();
        if(this.getParticipant(username) != null) {
            ui.showMessage("Please enter your password");
            String password = ui.readLine();
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

    public void logout() {
        if (this.currentUser != null) {
            ui.showMessage(this.currentUser.getName() + " has logged out.");
            this.currentUser = null;
        } else {
            ui.showMessage("No user is currently logged in.");
        }
    }

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

    public boolean assignParticipant(Event event, Participant participant) throws SyncException {
        if (event.getParticipants().contains(participant)) {
            throw new SyncException("User has already been assigned to this event. Try another user/event.");
        } else {
            boolean assigned = participant.assignEventTime(event.getStartTime(), event.getEndTime());
            storage.saveUsers(participants);
            return assigned;
        }
    }

    public void setCurrentUser(Participant user) {
        this.currentUser = user;
    }

    public boolean isCurrentUserAdmin() {
        return this.currentUser.getAccessLevel() == Participant.AccessLevel.ADMIN;
    }

    public boolean checkCurrentParticipantAvailability(Event event) {
        return this.currentUser.isAvailableDuring(event.getStartTime(), event.getEndTime());
    }
}
