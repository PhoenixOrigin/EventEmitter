# EventEmitter
Very simple event emitter library made in java


# How it works
It creates "channels" which can be fired events upon. You can assign each callback a priority and cancel it during the callback (Callbacks already fired will not be reversed somehow). Events can be anything as long as they extend the Event class.
# Usage [![Javadoc](https://img.shields.io/badge/JavaDoc-Online-green)](https://phoenixorigin.github.io/EventEmitter/javadoc/)

## Add library
### Gradle
#### Groovy
```groovy
dependencies {
    implementation 'io.github.phoenixorigin:eventemitter:1.0.0'
}
```
#### Kotlin
```kotlin
dependencies {
    implementation("io.github.phoenixorigin:eventemitter:1.0.0")
}
```
## Using it
### Generic event handler
```java
EventEmitter<? extends Event> eventEmitter = new EventEmitter<>();
```
Handling Event
```java
eventEmitter.on("channel", ((channel, event) -> {
    CustomEvent event = (CustomEvent) event;
    // Handle event
}));
```
Emitting Events
```java
eventEmitter.emit("channel", new CustomEvent());
```



### Event Specific Event Handlers
```java
EventEmitter<CustomEvent> eventEmitter = new EventEmitter<>();
```
Handling Event
```java
eventEmitter.on("channel", ((channel, event) -> {
    // Handle event. Event will be an instance of whatever was set in EventEmitter instance. 
    // In this case, event will be an instance of CustomEvent
}));
```
Emitting Events
```java
// Same as normal.
eventEmitter.emit("channel", new CustomEvent());
```

### Priorities
If you need to give certain events priorities over others, you can do 
```java
eventEmitter.on("channel", ((channel, event) -> {
    System.out.println(event.e);
}), EventEmitter.Priority.LOW);
// Options are TOP, HIGH, MEDIUM, LOW (Low is default)
```
### Cancelling events
If you need to cancel an event for whatever reason, you can just do
```java
eventEmitter.on("channel", ((channel, event) -> {
    event.cancel();
}));
```
### Unregistering an eventhandler
```java
// Record the UUID of the eventhandler instance
String eventHandlerUUID = eventEmitter.on("channel", ((channel, event) -> {
    // Whatever
}));
// Unregister the instance
eventEmitter.off(eventHandlerUUID);
```