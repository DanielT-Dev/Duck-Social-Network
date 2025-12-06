package repository;

import database.DatabaseConnection;
import domain.Card;
import domain.Duck;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CardRepository {
    public void save(Card card) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();

        // Save card
        String cardSql = "INSERT INTO cards (id, nume_card) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(cardSql)) {
            stmt.setLong(1, card.getId());
            stmt.setString(2, card.getNumeCard());
            stmt.executeUpdate();
        }

        // Save card members
        String memberSql = "INSERT INTO card_members (card_id, duck_id) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(memberSql)) {
            for (Duck duck : card.getMembri()) {
                stmt.setLong(1, card.getId());
                stmt.setLong(2, duck.getId());
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }

    public void delete(long cardId) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();

        // Delete card members first
        String memberSql = "DELETE FROM card_members WHERE card_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(memberSql)) {
            stmt.setLong(1, cardId);
            stmt.executeUpdate();
        }

        // Delete card
        String cardSql = "DELETE FROM cards WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(cardSql)) {
            stmt.setLong(1, cardId);
            stmt.executeUpdate();
        }
    }

    public Card findById(long cardId) throws SQLException {
        String cardSql = "SELECT * FROM cards WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(cardSql)) {

            stmt.setLong(1, cardId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                long id = rs.getLong("id");
                String numeCard = rs.getString("nume_card");
                List<Duck> membri = getCardMembers(cardId);

                return new Card(id, numeCard, membri);
            }
            return null;
        }
    }

    public List<Card> findAll() throws SQLException {
        List<Card> cards = new ArrayList<>();
        String sql = "SELECT * FROM cards";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                long id = rs.getLong("id");
                String numeCard = rs.getString("nume_card");
                List<Duck> membri = getCardMembers(id);

                cards.add(new Card(id, numeCard, membri));
            }
        }
        return cards;
    }

    private List<Duck> getCardMembers(long cardId) throws SQLException {
        List<Duck> members = new ArrayList<>();
        String sql = "SELECT d.*, u.username, u.email, u.password " +
                "FROM ducks d " +
                "JOIN users u ON d.id = u.id " +
                "JOIN card_members cm ON d.id = cm.duck_id " +
                "WHERE cm.card_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, cardId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Duck duck = new Duck(
                        rs.getLong("id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("password"),
                        domain.TipRata.valueOf(rs.getString("tip")),
                        rs.getDouble("viteza"),
                        rs.getDouble("rezistenta")
                );
                members.add(duck);
            }
        }
        return members;
    }

    public void addMember(long cardId, long duckId) throws SQLException {
        String sql = "INSERT INTO card_members (card_id, duck_id) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, cardId);
            stmt.setLong(2, duckId);
            stmt.executeUpdate();
        }
    }

    public void removeMember(long cardId, long duckId) throws SQLException {
        String sql = "DELETE FROM card_members WHERE card_id = ? AND duck_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, cardId);
            stmt.setLong(2, duckId);
            stmt.executeUpdate();
        }
    }
}