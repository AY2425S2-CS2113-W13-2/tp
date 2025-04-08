package sort;

import command.LoginCommand;
import event.Event;
import label.Priority;
import java.util.List;
import java.util.logging.Logger;

/**
 * The SortByPriority class implements the sorting algorithm for a list of events,
 * sorting them based on their priority in descending order.
 * In case two events have the same priority, they are sorted based on their end time,
 * with earlier events appearing first.
 */
public class SortByPriority extends Sort {
    private static final Logger LOGGER = Logger.getLogger(LoginCommand.class.getName());


    /**
     * Sorts a list of events based on their priority and end time.
     * The events are sorted in descending order of their priority. If two events have
     * the same priority, they are further sorted in ascending order of their end time.
     *
     * @param events The list of events to be sorted.
     * @param priorities The list of priorities corresponding to the events, used for sorting.
     *                   A higher priority value indicates a higher priority.
     */
    @Override
    public void sort(List<Event> events, List<String> priorities) {
        assert events != null;
        LOGGER.info("Attempting sorting by priorities");
        int n = events.size();
        for (int i = 0; i < n - 1; i++) {
            int bestIdx = i;
            for (int j = i + 1; j < n; j++) {
                int prioBest = Priority.getValue(priorities.get(bestIdx));
                int prioJ = Priority.getValue(priorities.get(j));

                boolean isHigherPriority = prioJ > prioBest;
                boolean isSamePriorityEarlierEnd = prioJ == prioBest &&
                        events.get(j).getEndTime().isBefore(events.get(bestIdx).getEndTime());

                if (isHigherPriority || isSamePriorityEarlierEnd) {
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
        Event tempEvent = events.get(i);
        events.set(i, events.get(j));
        events.set(j, tempEvent);

        String tempPrio = priorities.get(i);
        priorities.set(i, priorities.get(j));
        priorities.set(j, tempPrio);
    }
}
