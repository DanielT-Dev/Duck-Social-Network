package domain;

import java.time.LocalDateTime;

public class Message {
    private long id;
    private long senderId;
    private long receiverId;
    private String content;
    private LocalDateTime timestamp;

    public Message(long id, long senderId, long receiverId, String content, LocalDateTime timestamp) {
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.timestamp = timestamp;
    }

    public Message(long senderId, long receiverId, String content) {
        this(0, senderId, receiverId, content, LocalDateTime.now());
    }

    public long getId() { return id; }
    public long getSenderId() { return senderId; }
    public long getReceiverId() { return receiverId; }
    public String getContent() { return content; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
