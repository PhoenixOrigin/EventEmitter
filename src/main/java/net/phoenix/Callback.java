package net.phoenix;

/**
 * Simple callback
 * @param <T> Event
 */
@FunctionalInterface
public interface Callback<T extends Event> {
    /**
     * Can be used in two ways;
     *
     * <pre>
     * <code>
     * eventEmitter.on("channel", (channel, event) -> {
     *     // Handle Event
     *   }
     * </code>
     * </pre>
     * OR
     * <pre></pre>
     * <pre>
     * <code>
     * public class EventHandler implements Callback<CustomEvent> {
     *         {@code @Override}
     *         public void call(String channel, CustomEvent event) {
     *             // Handle Event
     *         }
     *      }
     *
     *      eventEmitter.on("channel, new EventHandler());
     * </code>
     * </pre>
     *
     * @param channel Channel that message is incoming from. If it is channel specific, it will be the channel name it was registered under. If it is a global
     * @param event Instance of event
     */
    void call(String channel, T event);
}