package sort;

import event.Event;

import java.util.List;

public abstract class Sort {
    public abstract void sort(List<Event> events, List<String> priorities);
}
