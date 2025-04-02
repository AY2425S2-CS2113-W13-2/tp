import event.Event;
import sort.SortByEndTime;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SortByEndTimeTest {

    @Test
    void testSortByEndTimeThenPriority() {
        List<Event> events = new ArrayList<>();
        List<String> priorities = new ArrayList<>();

        events.add(new Event("Event A",
                LocalDateTime.of(2025, 4, 1, 10, 0),
                LocalDateTime.of(2025, 4, 2, 12, 0),
                "Room A", "Description A"));
        priorities.add("MEDIUM");

        events.add(new Event("Event B",
                LocalDateTime.of(2025, 4, 1, 9, 0),
                LocalDateTime.of(2025, 4, 1, 11, 0),
                "Room B", "Description B"));
        priorities.add("MEDIUM");

        events.add(new Event("Event C",
                LocalDateTime.of(2025, 4, 1, 8, 0),
                LocalDateTime.of(2025, 4, 1, 11, 0),
                "Room C", "Description C"));
        priorities.add("HIGH");

        new SortByEndTime().sort(events, priorities);

        assertEquals("Event C", events.get(0).getName());
        assertEquals("Event B", events.get(1).getName());
        assertEquals("Event A", events.get(2).getName());
    }
}
