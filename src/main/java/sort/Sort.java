package sort;

import event.Event;
import java.util.List;

/**
 * The abstract Sort class defines a blueprint for sorting algorithms that can be applied to a list of events.
 * Subclasses are required to implement the sorting logic based on the provided priorities.
 *
 * This class allows different sorting strategies to be used for sorting events, based on various criteria such as date, time, or other event attributes.
 */
public abstract class Sort {

    /**
     * Sorts a list of events based on the specified priorities.
     *
     * @param events The list of events to be sorted.
     * @param priorities The list of priorities that determine how the events should be sorted.
     *                   These could represent sorting criteria such as event time, name, etc.
     */
    public abstract void sort(List<Event> events, List<String> priorities);
}
