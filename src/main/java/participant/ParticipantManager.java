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

    public Participant getCurrentUser() {
        return currentUser;
    }

    public void addNewUser(Participant participant) {
        participants.add(participant);
        storage.saveUsers(participants);
    }

    public void deleteUser(Participant participant) {
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
        System.out.println("Please enter your Username");
        Scanner scanner = new Scanner(System.in);
        String username = scanner.nextLine();
        if(this.getParticipant(username) != null) {
            System.out.println("Please enter your password");
            String password = scanner.nextLine();
            if(this.getParticipant(username).checkPassword(password)) {
                this.currentUser = this.getParticipant(username);
                ui.showSuccessLoginMessage();
                ui.showMenu();
                return this;
            } else {
                System.out.println("Wrong password. Do you want to login again?");
                if(scanner.nextLine().equalsIgnoreCase("yes")) {
                    return this.login();
                } else {
                    return this;
                }
            }
        } else {
            System.out.println("User not found. Please create user first!");
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
        System.out.println("Checking participant availability");
        for (AvailabilitySlot slot : participant.getAvailableTimes()) {
            System.out.println("  -" + slot.getStartTime() + " to " + slot.getEndTime());
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

    public boolean assignParticipant(Event event, Participant participant) {
        boolean assigned = participant.assignEventTime(event.getStartTime(), event.getEndTime());
        storage.saveUsers(participants);
        return assigned;
    }

    public boolean isCurrentUserAdmin() {
        return this.currentUser.getAccessLevel() == Participant.AccessLevel.ADMIN;
    }
}
