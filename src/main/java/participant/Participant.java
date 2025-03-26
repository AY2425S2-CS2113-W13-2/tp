package participant;

public class Participant {
    private final String name;
    private AccessLevel accessLevel;

    public enum AccessLevel { ADMIN, MEMBER }

    public Participant(String name, AccessLevel accessLevel) {
        this.name = name;
        this.accessLevel = accessLevel;
    }

    public String getName() { return name; }
    public AccessLevel getAccessLevel() { return accessLevel; }

    @Override
    public String toString() {
        return name +" : "+ accessLevel;
    }
}
