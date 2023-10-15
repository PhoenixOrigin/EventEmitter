package net.phoenix;

import java.util.*;
import java.util.stream.Collectors;

public class EventEmitter<T extends Event> {

    private final HashMap<String, LinkedList<Pair<String, Callback<T>>>> callbacks = new HashMap<>();

    public enum Priority {
        TOP(4),
        HIGH(3),
        MEDIUM(2),
        LOW(1);

        private final int value;

        Priority(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
    public void emit(String channel, Event object) {
        if (!isMatchingEventType(object)) {
            throw new IllegalArgumentException("Object type does not match the EventEmitter type parameter.");
        }
        List<Pair<String, Callback<T>> > sortedCallbacks = getSortedCallbacks(channel);
        for (Pair<String, Callback<T>> callbackPair : sortedCallbacks) {
            if (object.cancelled) return;
            callbackPair.getSecond().call(channel, (T) object);
        }
    }

    public String on(String channel, Callback<T> callback) {
        return on(channel, callback, Priority.LOW);
    }

    public String on(String channel, Callback<T> callback, Priority priority) {
        String uuid = UUID.randomUUID().toString();
        LinkedList<Pair<String, Callback<T>>> callbackPairList = callbacks.get(channel);
        if (callbackPairList == null) {
            callbackPairList = new LinkedList<>();
            callbackPairList.add(new Pair<>(uuid, callback, priority));
        } else {
            callbackPairList.add(new Pair<>(uuid, callback, priority));
        }
        callbacks.put(channel, callbackPairList);
        return uuid;
    }

    public void off(String uuid) {
        for (String channel : callbacks.keySet()) {
            LinkedList<Pair<String, Callback<T>> > callbackList = callbacks.get(channel);
            if (callbackList != null) {
                callbackList.removeIf(pair -> pair.getFirst().equals(uuid));
            }
        }
    }

    private List<Pair<String, Callback<T>>> getSortedCallbacks(String channel) {
        List<Pair<String, Callback<T>>> callbackPairList = callbacks.get(channel);
        if (callbackPairList != null) {
            return callbackPairList.stream()
                    .sorted((pair1, pair2) -> Integer.compare(pair2.getPriority().getValue(), pair1.getPriority().getValue()))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private boolean isMatchingEventType(Event object) {
        return object.getClass().isAssignableFrom(Event.class);
    }

    static class Pair<T1, T2> {

        private T1 first;
        private T2 second;
        private Priority priority;

        public Pair(T1 first, T2 second, Priority priority) {
            this.first = first;
            this.second = second;
            this.priority = priority;
        }

        public T1 getFirst() {
            return first;
        }

        public T2 getSecond() {
            return second;
        }

        public Priority getPriority() {
            return priority;
        }
    }
}