import org.junit.jupiter.api.Test;
import sort.SortByPriority;
import event.Event;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SortByPriorityTest {

    @Test
    void testSortByPriorityThenEndTime() {
        List<Event> events = new ArrayList<>();
        List<String> priorities = new ArrayList<>();

        events.add(new Event("Event A", LocalDateTime.of(2025, 5, 10, 12, 0),
                LocalDateTime.of(2025, 5, 10, 13, 0), "Loc", "Desc"));
        priorities.add("LOW");

        events.add(new Event("Event B", LocalDateTime.of(2025, 5, 10, 12, 0),
                LocalDateTime.of(2025, 5, 10, 14, 0), "Loc", "Desc"));
        priorities.add("HIGH");

        events.add(new Event("Event C", LocalDateTime.of(2025, 5, 10, 11, 0),
                LocalDateTime.of(2025, 5, 10, 13, 0), "Loc", "Desc"));
        priorities.add("MEDIUM");

        new SortByPriority().sort(events, priorities);

        assertEquals("Event B", events.get(0).getName()); // HIGH
        assertEquals("Event C", events.get(1).getName()); // MEDIUM
        assertEquals("Event A", events.get(2).getName()); // LOW
    }
}
