package service;

import domain.Message;
import repository.MessageRepository;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class MessageService {

    private final MessageRepository repo = new MessageRepository();

    // Send message without reply
    public void sendMessage(long from, long to, String content) {
        sendMessage(from, to, content, null); // use null instead of 0
    }

    // Send message with optional replyToId
    public void sendMessage(long from, long to, String content, Long replyToId) {
        try {
            // Only pass a valid replyToId if it exists; otherwise null
            Message message = new Message(
                    0,             // id will be auto-generated
                    from,
                    to,
                    content,
                    LocalDateTime.now(),
                    replyToId      // null if not replying
            );
            repo.save(message);
        } catch (SQLException e) {
            System.err.println("Error sending message: " + e.getMessage());
        }
    }

    public List<Message> getConversation(long u1, long u2) {
        try {
            return repo.getConversation(u1, u2);
        } catch (SQLException e) {
            System.err.println("Error loading conversation: " + e.getMessage());
            return List.of();
        }
    }
}
