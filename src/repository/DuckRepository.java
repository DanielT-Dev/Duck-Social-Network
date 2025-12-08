package repository;

import database.DatabaseConnection;
import domain.Duck;
import domain.TipRata;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DuckRepository {
    public void save(Duck duck) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();

        // Save to users table
        String userSql = "INSERT INTO users (id, username, email, password) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(userSql)) {
            stmt.setLong(1, duck.getId());
            stmt.setString(2, duck.getUsername());
            stmt.setString(3, duck.getEmail());
            stmt.setString(4, duck.getPassword());
            stmt.executeUpdate();
        }

        // Save to ducks table
        String duckSql = "INSERT INTO ducks (id, tip, viteza, rezistenta) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(duckSql)) {
            stmt.setLong(1, duck.getId());
            stmt.setString(2, duck.getTip().name());
            stmt.setDouble(3, duck.getViteza());
            stmt.setDouble(4, duck.getRezistenta());
            stmt.executeUpdate();
        }
    }

    public void delete(long id) throws SQLException {
        // Just delete from users - ducks will auto-delete
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }

    public Duck findById(long id) throws SQLException {
        String sql = "SELECT u.*, d.tip, d.viteza, d.rezistenta " +
                "FROM users u JOIN ducks d ON u.id = d.id " +
                "WHERE u.id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Duck(
                        rs.getLong("id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("password"),
                        TipRata.valueOf(rs.getString("tip")),
                        rs.getDouble("viteza"),
                        rs.getDouble("rezistenta")
                );
            }
            return null;
        }
    }

    public List<Duck> findAll() throws SQLException {
        List<Duck> ducks = new ArrayList<>();
        String sql = "SELECT u.*, d.tip, d.viteza, d.rezistenta " +
                "FROM users u JOIN ducks d ON u.id = d.id";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Duck duck = new Duck(
                        rs.getLong("id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("password"),
                        TipRata.valueOf(rs.getString("tip")),
                        rs.getDouble("viteza"),
                        rs.getDouble("rezistenta")
                );
                ducks.add(duck);
            }
        }
        return ducks;
    }

    public List<Duck> findAllPaginated(int offset, int limit) throws SQLException {
        List<Duck> ducks = new ArrayList<>();

        String sql = "SELECT u.*, d.tip, d.viteza, d.rezistenta " +
                "FROM users u " +
                "JOIN ducks d ON u.id = d.id " +
                "ORDER BY u.id " +
                "LIMIT ? OFFSET ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, limit);
            stmt.setInt(2, offset);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Duck duck = new Duck(
                        rs.getLong("id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("password"),
                        TipRata.valueOf(rs.getString("tip")),
                        rs.getDouble("viteza"),
                        rs.getDouble("rezistenta")
                );
                ducks.add(duck);
            }
        }
        return ducks;
    }

    public int getTotalCount() throws SQLException {
        String sql = "SELECT COUNT(*) as total FROM ducks";

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