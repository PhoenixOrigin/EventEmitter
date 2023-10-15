package net.phoenix;

public abstract class Event {
    void cancel() {cancelled = true;}
    boolean cancelled = false;
}

