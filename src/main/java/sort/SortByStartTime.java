package sort;

import event.Event;
import label.Priority;
import java.util.List;



public class SortByStartTime extends Sort {

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


    private void swap(List<Event> events, List<String> priorities, int i, int j) {
        Event temp = events.get(i);
        events.set(i, events.get(j));
        events.set(j, temp);

        String tempPrio = priorities.get(i);
        priorities.set(i, priorities.get(j));
        priorities.set(j, tempPrio);
    }
}
