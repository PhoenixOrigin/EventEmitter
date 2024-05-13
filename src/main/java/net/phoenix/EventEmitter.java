package net.phoenix;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * Standard event emitter class
 * @param <T> Event type. Either should be a class that extends Event OR a generic type (EventEmitter&lt;? extends Event&gt;)
 */
@SuppressWarnings("unused")
public class EventEmitter<T extends Event> {

    private final HashMap<String, LinkedList<Pair<String, Callback<T>>>> callbacks = new HashMap<>();

    /**
     * Priorities
     */
    public enum Priority {
        /**
         * ONLY USE FOR RARE CASES where this event needs to be called at the top. For normal cases, use HIGH
         */
        TOP(4),
        /**
         * Use for high level event handlers
         */
        HIGH(3),
        /**
         * Event handlers that should be called before the end
         */
        MEDIUM(2),
        /**
         * Default priority, called last
         */
        LOW(1);

        private final int value;

        Priority(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    /**
     * Calls all the events under a channel
     *
     * @param channel Channel to send event
     * @param object Instance of event to send
     * @throws IllegalArgumentException Throws exception if the event passed is different to the EventHandler type
     */
    public void emit(String channel, T object) {
        if (!isMatchingEventType(object)) {
            throw new IllegalArgumentException("Object type does not match the EventEmitter type parameter.");
        }
        List<Pair<String, Callback<T>> > sortedCallbacks = getSortedCallbacks(channel);
        for (Pair<String, Callback<T>> callbackPair : sortedCallbacks) {
            if (object.cancelled) return;
            callbackPair.second().call(channel, object);
        }
    }

    /**
     * Awaits for a method under the global channel
     *
     * @throws IllegalArgumentException Throws exception if the event passed is different to the EventHandler type
     */
    public String on(Callback<T> callback) {
        return on("global", callback);
    }

    /**
     * Awaits for a method under the global channel
     *
     * @throws IllegalArgumentException Throws exception if the event passed is different to the EventHandler type
     */
    public String on(Callback<T> callback, Priority priority) {
        return on("global", callback, priority);
    }

    /**
     * Awaits for a method under the global channel
     *
     * @throws IllegalArgumentException Throws exception if the event passed is different to the EventHandler type
     */
    public void emit(T object) {
        emit("global", object);
    }


    /**
     * Register an event handler with default priority of LOW
     *
     * @param channel The channel you want to listen for
     * @param callback Event handler. Can be used as a lambda; (channel, event) -> {}
     * @return Event handler UUID used in off()
     * @see EventEmitter#off(String) 
     * @see EventEmitter#on(String, Callback, Priority)  
     */
    public String on(String channel, Callback<T> callback) {
        return on(channel, callback, Priority.LOW);
    }


    /**
     * Register an event handler with a custom priority
     *
     * @param channel The channel you want to listen for
     * @param callback Event handler. Can be used as a lambda; (channel, event) -> {}
     * @param priority Priority level
     * @return Event handler UUID used in off()
     * @see EventEmitter#off(String)
     * @see EventEmitter#on(String, Callback, Priority)
     */
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

    /**
     * Awaits for a method under the global channel
     *
     * @throws InterruptedException Throws exception if the thread is interrupted
     */
    public T await(String channel) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<T> e = new AtomicReference<>();
        this.on(channel, (ch, event) -> {
            latch.countDown();
            e.set(event);
        }, Priority.TOP);
        latch.await();
        return e.get();
    }


    /**
     * Disable a eventHandler
     *
     * @param uuid UUID of event handler to remove. UUID is the value returned by EventEmitter#on
     */
    public void off(String uuid) {
        for (String channel : callbacks.keySet()) {
            LinkedList<Pair<String, Callback<T>> > callbackList = callbacks.get(channel);
            if (callbackList != null) {
                callbackList.removeIf(pair -> pair.first().equals(uuid));
            }
        }
    }

    /**
     * Private function to get a list of callbacks for a channel sorted by priority
     * @param channel Channel to sort callbacks for
     * @return Sorted list of callbacks by priority
     */
    private List<Pair<String, Callback<T>>> getSortedCallbacks(String channel) {
        List<Pair<String, Callback<T>>> callbackPairList = callbacks.get(channel);
        if (callbackPairList != null) {
            return callbackPairList.stream()
                    .sorted((pair1, pair2) -> Integer.compare(pair2.priority().getValue(), pair1.priority().getValue()))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    /**
     * Checks for illegal argument
     *
     * @param object Event to check
     * @return True / False
     */
    private boolean isMatchingEventType(Event object) {
        return object.getClass().isAssignableFrom(Event.class);
    }

    /**
     * Stores pairs of code
     *
     * @param <T1> UUID of callback
     * @param <T2> Callback
     */
    record Pair<T1, T2>(T1 first, T2 second, Priority priority) { }
}