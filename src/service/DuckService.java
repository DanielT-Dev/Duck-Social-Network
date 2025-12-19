package service;

import domain.Duck;
import repository.DuckRepository;
import repository.MemoryRepository;
import util.SecurityUtils;
import validator.DuckValidator;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DuckService {
    private final DuckRepository duckRepository;

    public DuckService(DuckRepository duckRepository) {
        this.duckRepository = duckRepository;
    }

    public void addDuck(Duck duck) {
        try {
            DuckValidator.validate(duck);
        } catch (IllegalArgumentException e) {
            System.err.println("Validation error: " + e.getMessage());
        }

        try {
            String hashedPassword = SecurityUtils.hashPassword(duck.getPassword());
            duck.setPassword(hashedPassword);
            duckRepository.save(duck);
            System.out.println("Duck added successfully!");
        } catch (SQLException e) {
            System.err.println("Error adding duck: " + e.getMessage());
        }
    }

    public void deleteDuck(long id) {
        try {
            duckRepository.delete(id);
            System.out.println("Duck deleted successfully!");
        } catch (SQLException e) {
            System.err.println("Error deleting duck: " + e.getMessage());
        }
    }

    public Duck getDuck(long id) {
        try {
            return duckRepository.findById(id);
        } catch (SQLException e) {
            System.err.println("Error finding duck: " + e.getMessage());
            return null;
        }
    }

    public List<Duck> getDucks() {
        try {
            return duckRepository.findAll();
        } catch (SQLException e) {
            System.err.println("Error getting ducks: " + e.getMessage());
            return List.of();
        }
    }
}
