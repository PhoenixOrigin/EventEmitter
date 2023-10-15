# EventEmitter
Very simple event emitter library made in java


# How it works
It creates "channels" which can be fired events upon. You can assign each callback a priority and cancel it during the callback (Callbacks already fired will not be reversed somehow). Events can be anything as long as they extend the Event class.
# Usage [![Javadoc](https://img.shields.io/badge/JavaDoc-Online-green)](https://phoenixorigin.github.io/EventEmitter/javadoc/)

## Add library
### Maven
```xml
<repositories>
    <repository>
        <id>gradle</id>
        <name>gradle</name>
        <url>https://gradle.phoenix.is-a.dev</url>
    </repository>
</repositories>


<dependency>
  <groupId>net.phoenix</groupId>
  <artifactId>eventemitter</artifactId>
  <version>0.0.2</version>
</dependency>
```
### Gradle Groovy
```groovy
repositories {
    maven {
        url "https://gradle.phoenix.is-a.dev/repository/gradle/"
    }
}
dependencies {
    implementation 'net.phoenix:eventemitter:0.0.2'
}
```
### Gradle Kotlin
```kotlin
repositories {
    mavenCentral()
    maven(url = "https://gradle.phoenix.is-a.dev/repository/gradle/")
}
dependencies {
    implementation("net.phoenix:eventemitter:0.0.2")
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
# Contributing
This is a one time project thing that I am probably never gonna look at again so if you want to make a change just make a PR and I will probably merge it
# Credits
Idea + some of the code- https://github.com/jafarlihi/eemit
