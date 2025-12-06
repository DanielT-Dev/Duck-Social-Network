package service;

import domain.Duck;
import repository.DuckRepository;
import repository.MemoryRepository;
import validator.DuckValidator;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DuckService {
    private final MemoryRepository memoryRepository;
    private final DuckRepository duckRepository = new DuckRepository();

    public DuckService(MemoryRepository memoryRepository) {
        this.memoryRepository = memoryRepository;
    }

    public void addDuck(Duck duck) {
        try {
            DuckValidator.validate(duck);
        } catch (IllegalArgumentException e) {
            System.err.println("Validation error: " + e.getMessage());
        }

        try {
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
