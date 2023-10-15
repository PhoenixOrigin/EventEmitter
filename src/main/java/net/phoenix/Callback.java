package net.phoenix;

@FunctionalInterface
public interface Callback<T extends Event> {
    void call(String channel, T event);
}