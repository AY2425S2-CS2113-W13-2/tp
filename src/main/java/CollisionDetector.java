import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class CollisionDetector {
    public ArrayList<Event> findTask(String startTime, String endTime, ArrayList<Event> events) throws SyncException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime start = LocalDateTime.parse(startTime, formatter);
        LocalDateTime end = LocalDateTime.parse(endTime, formatter);
        ArrayList<Event> collisions = new ArrayList<>();

        for (Event event : events) {
            if (!(event.getEndTime().isBefore(start) || event.getStartTime().isAfter(end))) {
                collisions.add(event);
            }
        }
        return collisions;
    }
}
