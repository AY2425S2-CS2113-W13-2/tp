package sort;

import event.Event;
import label.Priority;
import java.util.List;

/**
 * The SortByStartTime class implements the sorting algorithm for a list of events,
 * sorting them based on their start time in ascending order.
 * If two events have the same start time, they are sorted based on their priority,
 * with higher priority events appearing first.
 */
public class SortByStartTime extends Sort {

    /**
     * Sorts a list of events based on their start time and priority.
     * The events are sorted in ascending order of their start time. If two events have
     * the same start time, they are further sorted in descending order of their priority.
     *
     * @param events The list of events to be sorted.
     * @param priorities The list of priorities corresponding to the events, used for sorting.
     *                   A higher priority value indicates a higher priority.
     */
    @Override
    public void sort(List<Event> events, List<String> priorities) {
        int n = events.size();
        for (int i = 0; i < n - 1; i++) {
            int bestIdx = i;
            for (int j = i + 1; j < n; j++) {
                boolean isEarlier = events.get(j).getStartTime().isBefore(events.get(bestIdx).getStartTime());
                boolean sameTimeHigherPriority = events.get(j).getStartTime().equals(events.get(bestIdx).getStartTime())
                        && Priority.getValue(priorities.get(j)) > Priority.getValue(priorities.get(bestIdx));

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
