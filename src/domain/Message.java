package domain;

import java.time.LocalDateTime;

public class Message {
    private long id;
    private long senderId;
    private long receiverId;
    private String content;
    private LocalDateTime timestamp;
    private Long replyToId; // can be null

    public Message(long id, long senderId, long receiverId, String content, LocalDateTime timestamp, Long replyToId) {
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.timestamp = timestamp;
        this.replyToId = replyToId;
    }

    // Getters and setters
    public long getId() { return id; }
    public long getSenderId() { return senderId; }
    public long getReceiverId() { return receiverId; }
    public String getContent() { return content; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public Long getReplyToId() { return replyToId; }

    public void setReplyToId(Long replyToId) { this.replyToId = replyToId; }

    public void setId(long aLong) {
        this.id = aLong;
    }
}
