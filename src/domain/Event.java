package domain;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Event {
    private long id;
    private String name;
    private String description;
    private List<User> subscribers;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Event(long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.subscribers = new ArrayList<>();
    }

    public Event(String name, String description) {
        this(0, name, description); // 0 as temporary ID
    }

    // Getters and setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) {
        this.name = name;
        notifySubscribers("Event name changed to: " + name);
    }

    public String getDescription() { return description; }
    public void setDescription(String description) {
        this.description = description;
        notifySubscribers("Event description has been updated");
    }

    public List<User> getSubscribers() {
        return new ArrayList<>(subscribers);
    }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    // Observer pattern methods
    public void subscribe(User user) {
        if (user != null && !subscribers.contains(user)) {
            subscribers.add(user);
            System.out.println(user.getUsername() + " subscribed to event: " + name);
        }
    }

    public void unsubscribe(User user) {
        if (user != null && subscribers.contains(user)) {
            subscribers.remove(user);
            System.out.println(user.getUsername() + " unsubscribed from event: " + name);
        }
    }

    public void notifySubscribers(String message) {
        System.out.println("\n=== Notifying subscribers for event: " + name + " ===");
        for (User subscriber : subscribers) {
            subscriber.update(this, message);
        }
    }

    public void notifySubscribers() {
        notifySubscribers("Event '" + name + "' has been updated!");
    }

    public int getSubscriberCount() {
        return subscribers.size();
    }

    public boolean isSubscribed(User user) {
        return subscribers.contains(user);
    }

    @Override
    public String toString() {
        return "Event [id=" + id + ", name=" + name + ", description=" + description +
                ", subscribers=" + subscribers.size() + ", createdAt=" + createdAt + "]";
    }
}