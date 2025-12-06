package service;

import domain.Person;
import repository.MemoryRepository;
import repository.PersonRepository;

import java.sql.SQLException;
import java.util.List;

public class PersonService {
    private final MemoryRepository memoryRepository;
    private final PersonRepository personRepository =  new PersonRepository();

    public PersonService(MemoryRepository memoryRepository) {
        this.memoryRepository = memoryRepository;
    }

    public void addPerson(Person person) {
        try {
            personRepository.save(person);
            System.out.println("Person added successfully!");
        } catch (SQLException e) {
            System.err.println("Error adding person: " + e.getMessage());
        }
    }

    public void deletePerson(long id) {
        try {
            personRepository.delete(id);
            System.out.println("Person deleted successfully!");
        } catch (SQLException e) {
            System.err.println("Error deleting person: " + e.getMessage());
        }
    }

    public Person getPerson(long id) {
        try {
            return personRepository.findById(id);
        } catch (SQLException e) {
            System.err.println("Error finding person: " + e.getMessage());
            return null;
        }
    }

    public List<Person> getPersons() {
        try {
            return personRepository.findAll();
        } catch (SQLException e) {
            System.err.println("Error getting persons: " + e.getMessage());
            return List.of();
        }
    }
}
