package validator;

import domain.Duck;

public class DuckValidator {
    public static void validate(Duck duck) throws IllegalArgumentException {
        if (duck.getId() <= 0) throw new IllegalArgumentException("ID must be positive");
        if (duck.getUsername() == null || duck.getUsername().trim().isEmpty())
            throw new IllegalArgumentException("Username cannot be empty");
        if (duck.getEmail() == null || !duck.getEmail().contains("@"))
            throw new IllegalArgumentException("Invalid email format");
        if (duck.getPassword() == null || duck.getPassword().length() < 6)
            throw new IllegalArgumentException("Password must be at least 6 characters");
        if (duck.getTip() == null) throw new IllegalArgumentException("TipRata cannot be null");
        if (duck.getViteza() <= 0) throw new IllegalArgumentException("Viteza must be positive");
        if (duck.getRezistenta() <= 0) throw new IllegalArgumentException("Rezistenta must be positive");
    }
}