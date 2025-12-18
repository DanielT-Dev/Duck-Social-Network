package repository;

import database.DatabaseConnection;
import domain.FriendRequest;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FriendRequestRepository {

    public void save(FriendRequest request) throws SQLException {
        String sql = "INSERT INTO friend_requests (sender_id, receiver_id, status, created_at) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, request.getSenderId());
            stmt.setLong(2, request.getReceiverId());
            stmt.setString(3, request.getStatus());
            stmt.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            stmt.executeUpdate();
        }
    }

    public void delete(long senderId, long receiverId) throws SQLException {
        String sql = "DELETE FROM friend_requests WHERE sender_id = ? AND receiver_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, senderId);
            stmt.setLong(2, receiverId);
            stmt.executeUpdate();
        }
    }

    public boolean exists(long senderId, long receiverId) throws SQLException {
        String sql = "SELECT 1 FROM friend_requests WHERE sender_id = ? AND receiver_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, senderId);
            stmt.setLong(2, receiverId);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
    }

    public void updateStatus(long senderId, long receiverId, String status) throws SQLException {
        String sql = "UPDATE friend_requests SET status = ? WHERE sender_id = ? AND receiver_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setLong(2, senderId);
            stmt.setLong(3, receiverId);
            stmt.executeUpdate();
        }
    }

    public List<FriendRequest> findAll() throws SQLException {
        List<FriendRequest> requests = new ArrayList<>();
        String sql = "SELECT sender_id, receiver_id, status, created_at FROM friend_requests";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                requests.add(new FriendRequest(
                        rs.getLong("sender_id"),
                        rs.getLong("receiver_id"),
                        rs.getString("status"),
                        rs.getString("created_at")
                ));
            }
        }
        return requests;
    }

    public List<FriendRequest> findPendingForUser(long userId) throws SQLException {
        List<FriendRequest> requests = new ArrayList<>();
        String sql = "SELECT sender_id, receiver_id, status, created_at FROM friend_requests WHERE receiver_id = ? AND status = 'pending'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                requests.add(new FriendRequest(
                        rs.getLong("sender_id"),
                        rs.getLong("receiver_id"),
                        rs.getString("status"),
                        rs.getString("created_at")
                ));
            }
        }
        return requests;
    }

    public int getTotalCount() throws SQLException {
        String sql = "SELECT COUNT(*) as total FROM friend_requests";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("total");
            }
            return 0;
        }
    }
}
