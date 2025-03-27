package sort;

import event.Event;
import label.Priority;

import java.util.ArrayList;

public class SortByStartTime extends Sort {

    @Override
    public void sort(ArrayList<Event> events, ArrayList<String> priorities) {
        int n = events.size();
        for (int i = 0; i < n - 1; i++) {
            int minIdx = i;
            for (int j = i + 1; j < n; j++) {
                boolean isEarlier = events.get(j).getStartTime().isBefore(events.get(minIdx).getStartTime());
                boolean isSameTime = events.get(j).getStartTime().equals(events.get(minIdx).getStartTime());

                if (isEarlier || (isSameTime &&
                        Priority.getValue(priorities.get(j)) < Priority.getValue(priorities.get(minIdx)))) {
                    minIdx = j;
                }
            }

            swap(events, priorities, i, minIdx);
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
