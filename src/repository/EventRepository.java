package repository;

import database.DatabaseConnection;
import domain.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EventRepository {

    public void save(Event event) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();

        String sql = "INSERT INTO events (id, name, description, created_at) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, event.getId());
            stmt.setString(2, event.getName());
            stmt.setString(3, event.getDescription());
            stmt.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            stmt.executeUpdate();
        }

        // Save subscribers if any
        saveSubscribers(event);
    }

    private void updateSubscribers(Event event) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();

        // First, remove all existing subscribers for this event
        String deleteSql = "DELETE FROM event_subscribers WHERE event_id = ?";
        try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
            deleteStmt.setLong(1, event.getId());
            deleteStmt.executeUpdate();
        }

        // Then add current subscribers back
        saveSubscribers(event);
    }

    public void update(Event event) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();

        String sql = "UPDATE events SET name = ?, description = ?, updated_at = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, event.getName());
            stmt.setString(2, event.getDescription());
            stmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            stmt.setLong(4, event.getId());
            stmt.executeUpdate();
        }

        // Update subscribers
        updateSubscribers(event);
    }

    public void delete(long id) throws SQLException {
        // First delete from event_subscribers (foreign key constraint)
        String deleteSubscribersSql = "DELETE FROM event_subscribers WHERE event_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(deleteSubscribersSql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }

        // Then delete from events
        String sql = "DELETE FROM events WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }

    public Event findById(long id) throws SQLException {
        String sql = "SELECT * FROM events WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Event event = new Event(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("description")
                );
                event.setCreatedAt(rs.getTimestamp("created_at"));
                event.setUpdatedAt(rs.getTimestamp("updated_at"));

                // Load subscribers for this event
                loadSubscribers(event);

                return event;
            }
            return null;
        }
    }

    public List<Event> findAll() throws SQLException {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM events ORDER BY created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Event event = new Event(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("description")
                );
                event.setCreatedAt(rs.getTimestamp("created_at"));
                event.setUpdatedAt(rs.getTimestamp("updated_at"));

                // Load subscribers for each event
                loadSubscribers(event);

                events.add(event);
            }
        }
        return events;
    }

    public List<Event> findByName(String name) throws SQLException {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM events WHERE name LIKE ? ORDER BY created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + name + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Event event = new Event(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("description")
                );
                event.setCreatedAt(rs.getTimestamp("created_at"));
                event.setUpdatedAt(rs.getTimestamp("updated_at"));

                loadSubscribers(event);

                events.add(event);
            }
        }
        return events;
    }

    public void addSubscriber(long eventId, long userId) throws SQLException {
        String sql = "INSERT INTO event_subscribers (event_id, user_id, subscribed_at) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, eventId);
            stmt.setLong(2, userId);
            stmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            stmt.executeUpdate();
        }
    }

    public void removeSubscriber(long eventId, long userId) throws SQLException {
        String sql = "DELETE FROM event_subscribers WHERE event_id = ? AND user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, eventId);
            stmt.setLong(2, userId);
            stmt.executeUpdate();
        }
    }

    public List<User> getSubscribers(long eventId) throws SQLException {
        List<User> subscribers = new ArrayList<>();
        String sql = "SELECT u.*, p.nume, p.prenume, p.data_nasterii, p.ocupatie, p.nivel_empatie, " +
                "d.tip, d.viteza, d.rezistenta " +
                "FROM users u " +
                "LEFT JOIN persons p ON u.id = p.id " +
                "LEFT JOIN ducks d ON u.id = d.id " +
                "JOIN event_subscribers es ON u.id = es.user_id " +
                "WHERE es.event_id = ? " +
                "ORDER BY es.subscribed_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, eventId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                User user = createUserFromResultSet(rs);
                subscribers.add(user);
            }
        }
        return subscribers;
    }

    private User createUserFromResultSet(ResultSet rs) throws SQLException {
        long id = rs.getLong("id");
        String username = rs.getString("username");
        String email = rs.getString("email");
        String password = rs.getString("password");

        // Check if it's a Person (has data in persons table)
        String nume = rs.getString("nume");
        if (nume != null) {
            // This is a Person
            return new Person(
                    id,
                    username,
                    email,
                    password,
                    nume,
                    rs.getString("prenume"),
                    rs.getString("data_nasterii"),
                    rs.getString("ocupatie"),
                    rs.getLong("nivel_empatie")
            );
        }

        // Check if it's a Duck (has data in ducks table)
        String tipStr = rs.getString("tip");
        if (tipStr != null) {
            // This is a Duck
            TipRata tip = TipRata.valueOf(tipStr);
            double viteza = rs.getDouble("viteza");
            double rezistenta = rs.getDouble("rezistenta");

            return new Duck(
                    id,
                    username,
                    email,
                    password,
                    tip,
                    viteza,
                    rezistenta
            );
        }

        // If neither, throw exception
        throw new SQLException("User type not recognized for user ID: " + id);
    }

    // Update the loadSubscribers method to use the new createUserFromResultSet
    private void loadSubscribers(Event event) throws SQLException {
        List<User> subscribers = getSubscribers(event.getId());
        for (User subscriber : subscribers) {
            event.subscribe(subscriber);
        }
    }

    // Update saveSubscribers to handle different User types
    private void saveSubscribers(Event event) throws SQLException {
        if (event.getSubscribers() == null || event.getSubscribers().isEmpty()) {
            return;
        }

        String sql = "INSERT INTO event_subscribers (event_id, user_id, subscribed_at) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (User subscriber : event.getSubscribers()) {
                stmt.setLong(1, event.getId());
                stmt.setLong(2, subscriber.getId());
                stmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }

    public List<Event> findByUserId(long userId) throws SQLException {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT e.* FROM events e " +
                "JOIN event_subscribers es ON e.id = es.event_id " +
                "WHERE es.user_id = ? " +
                "ORDER BY es.subscribed_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Event event = new Event(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("description")
                );
                event.setCreatedAt(rs.getTimestamp("created_at"));
                event.setUpdatedAt(rs.getTimestamp("updated_at"));

                loadSubscribers(event);

                events.add(event);
            }
        }
        return events;
    }
}