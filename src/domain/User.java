package domain;

import java.util.Objects;

public abstract class User {
    private final long id;
    private final String username;
    private final String email;
    private final String password;

    public User(long id, String username, String email, String password) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public long getId() {
        return id;
    }
    public String getUsername() {
        return username;
    }
    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        User user = (User) obj;
        return id == user.id &&
                Objects.equals(username, user.username) &&
                Objects.equals(email, user.email) &&
                Objects.equals(password, user.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, email, password);
    }

    @Override
    public String toString() {
        return "id: " + id +
                ", username: " + username +
                ", email: " + email;
    }
}
