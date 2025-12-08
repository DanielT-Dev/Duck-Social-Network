package domain;

import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Friendship {
    private final long user1Id;
    private final long user2Id;
    private String createdAt; // Add this field if you want to show creation time

    public Friendship(long user1Id, long user2Id) {
        this.user1Id = user1Id;
        this.user2Id = user2Id;
    }

    public Friendship(long user1Id, long user2Id, String createdAt) {
        this.user1Id = user1Id;
        this.user2Id = user2Id;
        this.createdAt = createdAt;
    }

    public long getUser1Id() {
        return user1Id;
    }

    public long getUser2Id() {
        return user2Id;
    }

    public String getCreatedAt() {
        return createdAt != null ? createdAt : "";
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    // JavaFX property getters for TableView
    public Long getUser1IdProperty() {
        return user1Id;
    }

    public Long getUser2IdProperty() {
        return user2Id;
    }

    public String getCreatedAtProperty() {
        return getCreatedAt();
    }

    @Override
    public String toString() {
        return "user1-id: " + user1Id + ", user2-id: " + user2Id;
    }
}