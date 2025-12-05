package service;

import domain.Person;
import repository.MemoryRepository;

import java.util.List;

public class PersonService {
    private final MemoryRepository memoryRepository;

    public PersonService(MemoryRepository memoryRepository) {
        this.memoryRepository = memoryRepository;
    }

    public void addPerson(Person person) {
        this.memoryRepository.addPerson(person);
    }

    public void deletePerson(long id) {
        this.memoryRepository.deletePerson(id);
    }

    public List<Person> getPersons() {
        return this.memoryRepository.getPersons();
    }
}
