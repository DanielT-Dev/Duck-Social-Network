package repository;

import database.DatabaseConnection;
import domain.Person;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PersonRepository {
    public void save(Person person) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();

        // Save to users table
        String userSql = "INSERT INTO users (id, username, email, password) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(userSql)) {
            stmt.setLong(1, person.getId());
            stmt.setString(2, person.getUsername());
            stmt.setString(3, person.getEmail());
            stmt.setString(4, person.getPassword());
            stmt.executeUpdate();
        }

        // Save to persons table
        String personSql = "INSERT INTO persons (id, nume, prenume, data_nasterii, ocupatie, nivel_empatie) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(personSql)) {
            stmt.setLong(1, person.getId());
            stmt.setString(2, person.getNume());
            stmt.setString(3, person.getPrenume());
            stmt.setString(4, person.getDataNasterii());
            stmt.setString(5, person.getOcupatie());
            stmt.setLong(6, person.getNivelEmpatie());
            stmt.executeUpdate();
        }
    }

    public void delete(long id) throws SQLException {
        // Just delete from users - persons will auto-delete
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }

    public Person findById(long id) throws SQLException {
        String sql = "SELECT u.*, p.nume, p.prenume, p.data_nasterii, p.ocupatie, p.nivel_empatie " +
                "FROM users u JOIN persons p ON u.id = p.id " +
                "WHERE u.id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Person(
                        rs.getLong("id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("nume"),
                        rs.getString("prenume"),
                        rs.getString("data_nasterii"),
                        rs.getString("ocupatie"),
                        rs.getLong("nivel_empatie")
                );
            }
            return null;
        }
    }

    public List<Person> findAll() throws SQLException {
        List<Person> persons = new ArrayList<>();
        String sql = "SELECT u.*, p.nume, p.prenume, p.data_nasterii, p.ocupatie, p.nivel_empatie " +
                "FROM users u JOIN persons p ON u.id = p.id";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Person person = new Person(
                        rs.getLong("id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("nume"),
                        rs.getString("prenume"),
                        rs.getString("data_nasterii"),
                        rs.getString("ocupatie"),
                        rs.getLong("nivel_empatie")
                );
                persons.add(person);
            }
        }
        return persons;
    }

    public Person findByUsername(String username) throws SQLException {
        String sql = "SELECT u.*, p.nume, p.prenume, p.data_nasterii, p.ocupatie, p.nivel_empatie " +
                "FROM users u JOIN persons p ON u.id = p.id " +
                "WHERE u.username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Person(
                        rs.getLong("id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("nume"),
                        rs.getString("prenume"),
                        rs.getString("data_nasterii"),
                        rs.getString("ocupatie"),
                        rs.getLong("nivel_empatie")
                );
            }
            return null;
        }
    }

    public List<Person> findAllPaginated(int offset, int limit) throws SQLException {
        List<Person> persons = new ArrayList<>();

        String sql = "SELECT u.*, p.nume, p.prenume, p.data_nasterii, p.ocupatie, p.nivel_empatie " +
                "FROM users u " +
                "JOIN persons p ON u.id = p.id " +
                "ORDER BY u.id " +
                "LIMIT ? OFFSET ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, limit);
            stmt.setInt(2, offset);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Person person = new Person(
                        rs.getLong("id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("nume"),
                        rs.getString("prenume"),
                        rs.getString("data_nasterii"),
                        rs.getString("ocupatie"),
                        rs.getLong("nivel_empatie")
                );
                persons.add(person);
            }
        }
        return persons;
    }

    public int getTotalCount() throws SQLException {
        String sql = "SELECT COUNT(*) as total FROM persons";

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