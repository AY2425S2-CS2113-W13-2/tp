package sort;

import event.Event;
import label.Priority;

import java.util.ArrayList;

public class SortByPriority extends Sort {

    @Override
    public void sort(ArrayList<Event> events, ArrayList<String> priorities) {
        int n = events.size();
        for (int i = 0; i < n - 1; i++) {
            int maxIdx = i;
            for (int j = i + 1; j < n; j++) {
                int prioJ = Priority.getValue(priorities.get(j));
                int prioMax = Priority.getValue(priorities.get(maxIdx));

                if (prioJ > prioMax || (prioJ == prioMax &&
                        events.get(j).getEndTime().isBefore(events.get(maxIdx).getEndTime()))) {
                    maxIdx = j;
                }
            }

            swap(events, priorities, i, maxIdx);
        }
    }

    public void swap(ArrayList<Event> events, ArrayList<String> priorities, int i, int j) {
        Event tempEvent = events.get(i);
        events.set(i, events.get(j));
        events.set(j, tempEvent);

        String tempPriority = priorities.get(i);
        priorities.set(i, priorities.get(j));
        priorities.set(j, tempPriority);
    }
}