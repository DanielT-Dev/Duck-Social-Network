package service;

import domain.Duck;
import repository.MemoryRepository;
import validator.DuckValidator;

import java.util.ArrayList;
import java.util.List;

public class DuckService {
    private final MemoryRepository memoryRepository;

    public DuckService(MemoryRepository memoryRepository) {
        this.memoryRepository = memoryRepository;
    }

    public void addDuck(Duck duck) {
        try {
            DuckValidator.validate(duck);
        } catch (IllegalArgumentException e) {
            System.err.println("Validation error: " + e.getMessage());
        }
        this.memoryRepository.addDuck(duck);
    }

    public void deleteDuck(long id) {
        this.memoryRepository.deleteDuck(id);
    }

    public List<Duck> getDucks() {
        return memoryRepository.getDucks();
    }
}
