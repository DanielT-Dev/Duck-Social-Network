package repository;

import database.DatabaseConnection;
import domain.Friendship;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FriendshipRepository {
    public void save(Friendship friendship) throws SQLException {
        String sql = "INSERT INTO friendships (user1_id, user2_id) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Store with smaller ID first to avoid duplicates like (1,2) and (2,1)
            long user1 = Math.min(friendship.getUser1Id(), friendship.getUser2Id());
            long user2 = Math.max(friendship.getUser1Id(), friendship.getUser2Id());

            stmt.setLong(1, user1);
            stmt.setLong(2, user2);
            stmt.executeUpdate();
        }
    }

    public void delete(long user1Id, long user2Id) throws SQLException {
        String sql = "DELETE FROM friendships WHERE (user1_id = ? AND user2_id = ?) OR (user1_id = ? AND user2_id = ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, user1Id);
            stmt.setLong(2, user2Id);
            stmt.setLong(3, user2Id);
            stmt.setLong(4, user1Id);
            stmt.executeUpdate();
        }
    }

    public boolean exists(long user1Id, long user2Id) throws SQLException {
        String sql = "SELECT 1 FROM friendships WHERE (user1_id = ? AND user2_id = ?) OR (user1_id = ? AND user2_id = ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, user1Id);
            stmt.setLong(2, user2Id);
            stmt.setLong(3, user2Id);
            stmt.setLong(4, user1Id);

            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
    }

    public List<Friendship> findAll() throws SQLException {
        List<Friendship> friendships = new ArrayList<>();
        String sql = "SELECT user1_id, user2_id FROM friendships";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                friendships.add(new Friendship(
                        rs.getLong("user1_id"),
                        rs.getLong("user2_id")
                ));
            }
        }
        return friendships;
    }

    public List<Long> getFriendsOf(long userId) throws SQLException {
        List<Long> friends = new ArrayList<>();
        String sql = "SELECT user1_id, user2_id FROM friendships WHERE user1_id = ? OR user2_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);
            stmt.setLong(2, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                long user1 = rs.getLong("user1_id");
                long user2 = rs.getLong("user2_id");

                if (user1 == userId) {
                    friends.add(user2);
                } else {
                    friends.add(user1);
                }
            }
        }
        return friends;
    }
}