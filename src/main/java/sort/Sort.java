package sort;

import event.Event;
import label.Priority;

import java.util.ArrayList;

public abstract class Sort {
    public abstract void sort(ArrayList<Event> events, ArrayList<String> priorities);
}