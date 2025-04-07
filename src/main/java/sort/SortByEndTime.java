package sort;

import event.Event;
import label.Priority;
import java.util.List;
import java.util.logging.Logger;

/**
 * This class extends the Sort class and sorts events based on their end time.
 * If two events have the same end time, the event with the higher priority is placed earlier.
 */
public class SortByEndTime extends Sort {

    // Logger for logging assertions and other actions in the class
    private static final Logger logger = Logger.getLogger(SortByEndTime.class.getName());

    /**
     * Sorts the list of events based on their end time.
     * If two events have the same end time, the one with the higher priority is considered earlier.
     *
     * @param events a list of events to be sorted
     * @param priorities a list of priorities corresponding to each event
     * @throws IllegalArgumentException if the events or priorities lists are null or empty
     */
    @Override
    public void sort(List<Event> events, List<String> priorities) {
        // Assert that the events list and priorities list are not null or empty
        if (events == null || priorities == null || events.isEmpty() || priorities.isEmpty()) {
            logger.severe("Null or empty list provided");
            throw new IllegalArgumentException("Events and priorities lists cannot be null or empty.");
        }

        int n = events.size();
        logger.info("Sorting " + n + " events based on end time and priority.");

        for (int i = 0; i < n - 1; i++) {
            int bestIdx = i;
            for (int j = i + 1; j < n; j++) {
                boolean isEarlier = events.get(j).getEndTime().isBefore(events.get(bestIdx).getEndTime());
                boolean sameTimeHigherPriority = events.get(j).getEndTime().equals(events.get(bestIdx).getEndTime()) &&
                        Priority.getValue(priorities.get(j)) > Priority.getValue(priorities.get(bestIdx));

                // Log the comparison results
                if (isEarlier) {
                    logger.fine("Event " + events.get(j) + " is earlier than " + events.get(bestIdx));
                }
                if (sameTimeHigherPriority) {
                    logger.fine("Event " + events.get(j) + " has the same end time but higher priority than " + events.get(bestIdx));
                }

                if (isEarlier || sameTimeHigherPriority) {
                    bestIdx = j;
                }
            }

            // Log the swapping action
            if (bestIdx != i) {
                logger.info("Swapping events at index " + i + " and " + bestIdx);
            }

            swap(events, priorities, i, bestIdx);
        }
    }

    /**
     * Swaps two elements in both the events list and priorities list.
     *
     * @param events the list of events
     * @param priorities the list of priorities
     * @param i the index of the first element
     * @param j the index of the second element
     */
    private void swap(List<Event> events, List<String> priorities, int i, int j) {
        Event temp = events.get(i);
        events.set(i, events.get(j));
        events.set(j, temp);

        String tempPrio = priorities.get(i);
        priorities.set(i, priorities.get(j));
        priorities.set(j, tempPrio);
    }
}
