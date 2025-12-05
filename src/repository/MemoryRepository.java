package repository;

import domain.Duck;
import domain.Person;

import java.util.ArrayList;
import java.util.List;

public class MemoryRepository {
    private final List<Duck> ducks;
    private final List<Person> persons;

    public MemoryRepository() {
        this.ducks = new ArrayList<>();
        this.persons = new ArrayList<>();
    }

    public void addDuck(Duck duck) {
        this.ducks.add(duck);
    }

    public void deleteDuck(long id) {
        this.ducks.removeIf(duck -> duck.getId() == id);
    }

    public List<Duck> getDucks() {
        return ducks;
    }

    public void addPerson(Person person) {
        this.persons.add(person);
    }

    public void deletePerson(long id) {
        this.persons.removeIf(person -> person.getId() == id);
    }

    public List<Person> getPersons() {
        return persons;
    }
}
