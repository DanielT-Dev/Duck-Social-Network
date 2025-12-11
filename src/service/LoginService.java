package service;

import domain.Duck;
import domain.Person;

import java.util.List;

public class LoginService {
    private final DuckService duckService;
    private final PersonService personService;

    public LoginService(DuckService duckService, PersonService personService) {
        this.duckService = duckService;
        this.personService = personService;
    }

    /**
     * Checks login credentials for both Ducks and Persons.
     * Returns a string like "Duck: <username>" or "Person: <username>" if successful.
     * Returns null if login fails.
     */
    public String login(String email, String password) {
        // Check ducks
        List<Duck> ducks = duckService.getDucks();
        for (Duck duck : ducks) {
            if (duck.getEmail().equals(email) && duck.getPassword().equals(password)) {
                return "Duck: " + duck.getUsername();
            }
        }

        // Check persons
        List<Person> persons = personService.getPersons();
        for (Person person : persons) {
            if (person.getEmail().equals(email) && person.getPassword().equals(password)) {
                return "Person: " + person.getUsername();
            }
        }

        return null; // login failed
    }
}
