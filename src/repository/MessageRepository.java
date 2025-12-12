package repository;

import database.DatabaseConnection;
import domain.Message;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessageRepository {

    public void save(Message m) throws SQLException {
        String sql = "INSERT INTO messages (sender_id, receiver_id, content, timestamp) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, m.getSenderId());
            stmt.setLong(2, m.getReceiverId());
            stmt.setString(3, m.getContent());
            stmt.setTimestamp(4, Timestamp.valueOf(m.getTimestamp()));

            stmt.executeUpdate();
        }
    }

    public List<Message> getConversation(long user1, long user2) throws SQLException {
        List<Message> messages = new ArrayList<>();

        String sql = """
            SELECT * FROM messages 
            WHERE (sender_id = ? AND receiver_id = ?)
               OR (sender_id = ? AND receiver_id = ?)
            ORDER BY timestamp
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, user1);
            stmt.setLong(2, user2);
            stmt.setLong(3, user2);
            stmt.setLong(4, user1);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                messages.add(new Message(
                        rs.getLong("id"),
                        rs.getLong("sender_id"),
                        rs.getLong("receiver_id"),
                        rs.getString("content"),
                        rs.getTimestamp("timestamp").toLocalDateTime()
                ));
            }
        }

        return messages;
    }

    public List<Long> getAllUserIdsFromMessages() throws SQLException {
        List<Long> ids = new ArrayList<>();
        String sql = "SELECT DISTINCT sender_id FROM messages UNION SELECT DISTINCT receiver_id FROM messages";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) ids.add(rs.getLong(1));
        }

        return ids;
    }
}
