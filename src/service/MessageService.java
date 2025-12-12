package service;

import domain.Message;
import repository.MessageRepository;

import java.sql.SQLException;
import java.util.List;

public class MessageService {

    private final MessageRepository repo = new MessageRepository();

    public void sendMessage(long from, long to, String content) {
        try {
            repo.save(new Message(from, to, content));
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
