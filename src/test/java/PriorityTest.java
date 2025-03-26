import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import label.Priority;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class PriorityTest {

    @BeforeEach
    public void setUp() {
        Priority.clearPriorities();
    }

    @AfterEach
    public void tearDown() {
        Priority.clearPriorities();
    }

    @Test
    public void testIsValid() {
        assertTrue(Priority.isValid("HIGH"));
        assertTrue(Priority.isValid("medium"));
        assertTrue(Priority.isValid("low"));
        assertFalse(Priority.isValid("urgent"));
        assertFalse(Priority.isValid(""));
        assertFalse(Priority.isValid(null));
    }

    @Test
    public void testNormalize() {
        assertEquals("HIGH", Priority.normalize("high"));
        assertEquals("LOW", Priority.normalize("Low"));
        assertEquals("MEDIUM", Priority.normalize(" medium "));
    }

    @Test
    public void testGetValue() {
        assertEquals(1, Priority.getValue("HIGH"));
        assertEquals(2, Priority.getValue("MEDIUM"));
        assertEquals(3, Priority.getValue("LOW"));
        assertEquals(4, Priority.getValue("UNKNOWN"));
    }

    @Test
    public void testAddAndGetPriority() {
        Priority.addPriority("HIGH");
        Priority.addPriority("LOW");

        assertEquals("HIGH", Priority.getPriority(0));
        assertEquals("LOW", Priority.getPriority(1));
    }

    @Test
    public void testUpdatePriority() {
        Priority.addPriority("LOW");
        Priority.updatePriority(0, "MEDIUM");

        assertEquals("MEDIUM", Priority.getPriority(0));
    }

    @Test
    public void testRemovePriority() {
        Priority.addPriority("HIGH");
        Priority.addPriority("LOW");
        Priority.removePriority(0);

        assertEquals("LOW", Priority.getPriority(0));  // LOW should now be at index 0
    }

    @Test
    public void testClearPriorities() {
        Priority.addPriority("HIGH");
        Priority.addPriority("LOW");
        Priority.clearPriorities();

        assertEquals(0, Priority.getAllPriorities().size());
    }

    @Test
    public void testLoadPriorities() {
        List<String> saved = new ArrayList<>();
        saved.add("HIGH");
        saved.add("MEDIUM");

        Priority.loadFromStorage(saved);

        assertEquals("HIGH", Priority.getPriority(0));
        assertEquals("MEDIUM", Priority.getPriority(1));
    }

    @Test
    public void testGetPriorityOutOfBounds() {
        assertEquals("LOW", Priority.getPriority(0));  // default fallback
    }
}
