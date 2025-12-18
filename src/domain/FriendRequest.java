package domain;

public class FriendRequest {
    private final long senderId;
    private final long receiverId;
    private String status;      // e.g., "pending", "accepted", "rejected"
    private String createdAt;   // optional timestamp

    public FriendRequest(long senderId, long receiverId) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.status = "pending"; // default
    }

    public FriendRequest(long senderId, long receiverId, String status, String createdAt) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.status = status;
        this.createdAt = createdAt;
    }

    public long getSenderId() {
        return senderId;
    }

    public long getReceiverId() {
        return receiverId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt != null ? createdAt : "";
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    // JavaFX property getters for TableView
    public Long getSenderIdProperty() {
        return senderId;
    }

    public Long getReceiverIdProperty() {
        return receiverId;
    }

    public String getStatusProperty() {
        return status;
    }

    public String getCreatedAtProperty() {
        return getCreatedAt();
    }

    @Override
    public String toString() {
        return "sender-id: " + senderId + ", receiver-id: " + receiverId + ", status: " + status;
    }
}
