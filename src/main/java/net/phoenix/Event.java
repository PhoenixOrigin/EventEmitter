package net.phoenix;

/**
 * Event class. All custom events should extend this.
 */
@SuppressWarnings("unused")
public abstract class Event {
    void cancel() {cancelled = true;}
    boolean cancelled = false;
}

