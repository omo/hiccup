package es.flakiness.hiccup.talk;

import java.util.List;

public class RemoveTalksEvent {
    private final List<Long> removedItems;

    public RemoveTalksEvent(List<Long> removedItems) {
        this.removedItems = removedItems;
    }

    public List<Long> getRemovedItems() {
        return removedItems;
    }
}
