package sort;

import event.Event;
import label.Priority;
import java.util.List;

/**
 * The SortByEndTime class implements the sorting algorithm for a list of events,
 * sorting them based on their end time in ascending order.
 * In case two events have the same end time, they are sorted based on their priority,
 * with higher priority events appearing first.
 */
public class SortByEndTime extends Sort {

    /**
     * Sorts a list of events based on their end time and priority.
     * The events are sorted in ascending order of their end time, and if two events
     * have the same end time, they are sorted by their priority in descending order.
     *
     * @param events The list of events to be sorted.
     * @param priorities The list of priorities corresponding to the events, used to break ties when two events
     *                   have the same end time. A higher priority value indicates a higher priority.
     */
    @Override
    public void sort(List<Event> events, List<String> priorities) {
        int n = events.size();
        for (int i = 0; i < n - 1; i++) {
            int bestIdx = i;
            for (int j = i + 1; j < n; j++) {
                boolean isEarlier = events.get(j).getEndTime().isBefore(events.get(bestIdx).getEndTime());
                boolean sameTimeHigherPriority = events.get(j).getEndTime().equals(events.get(bestIdx).getEndTime()) &&
                        Priority.getValue(priorities.get(j)) > Priority.getValue(priorities.get(bestIdx));

                if (isEarlier || sameTimeHigherPriority) {
                    bestIdx = j;
                }
            }
            swap(events, priorities, i, bestIdx);
        }
    }

    /**
     * Swaps the elements at indices i and j in both the events and priorities lists.
     *
     * @param events The list of events.
     * @param priorities The list of priorities.
     * @param i The index of the first element to swap.
     * @param j The index of the second element to swap.
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
