package controller;

import domain.*;
import repository.*;
import service.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class ConsoleMenu {

    public static final ConsoleMenu INSTANCE = new ConsoleMenu();

    private final CardRepository cardRepository = new CardRepository();
    private final DuckRepository duckRepository = new DuckRepository();
    private final EventRepository eventRepository = new EventRepository();
    private final FriendshipRepository friendshipRepository = new FriendshipRepository();
    private final PersonRepository personRepository = new PersonRepository();

    private final MemoryRepository memoryRepository = new MemoryRepository();
    private final DuckService duckService = new DuckService(duckRepository);
    private final PersonService personService = new PersonService(personRepository);
    private final FriendshipService friendshipService = new FriendshipService(friendshipRepository);
    private final CardService cardService = new CardService(cardRepository, duckRepository);
    private final EventService eventService = new EventService(eventRepository);

    private final Scanner scanner = new Scanner(System.in);

    public void printMenu() {
        System.out.println("DUCK SOCIAL NETWORK INTELLIJ:");

        System.out.println("0. EXIT");
        System.out.println("1. CREATE Duck");
        System.out.println("2. CREATE Person");
        System.out.println("3. CREATE Friendship");
        System.out.println("4. CREATE Card");
        System.out.println("5. CREATE Event");
        System.out.println("6. DELETE Duck");
        System.out.println("7. DELETE Person");
        System.out.println("8. DELETE Friendship");
        System.out.println("9. DELETE Card");
        System.out.println("10. DELETE Event");
        System.out.println("11. PRINT Ducks");
        System.out.println("12. PRINT Persons");
        System.out.println("13. PRINT Friendships");
        System.out.println("14. PRINT Cards");
        System.out.println("15. PRINT Events");
        System.out.println("16. SUBSCRIBE to an event");
    }

    public void start() {
        printMenu();

        while(true) {

            System.out.print("> ");

            int choice = scanner.nextInt();

            switch (choice) {
                case 0:
                    return;
                    case 1: this.createDuck(); break;
                    case 2: this.createPerson(); break;
                    case 3: this.createFriendship(); break;
                    case 4: this.createCard(); break;
                    case 5: this.createEvent(); break;
                    case 6: this.deleteDuck(); break;
                    case 7: this.deletePerson(); break;
                    case 8: this.deleteFriendship(); break;
                    case 9: this.deleteCard(); break;
                    case 10: this.deleteEvent(); break;
                    case 11: this.printDucks(); break;
                    case 12: this.printPersons(); break;
                    case 13: this.printFriendships(); break;
                    case 14: this.printCards(); break;
                    case 15: this.printEvents(); break;
                    case 16: this.subscribeToEvent(); break;

                    default:
                        System.out.println("Invalid option.");
            }
        }
    }

    private void createDuck() {
        System.out.println("CREATE DUCK:");

        long id = 0;
        String username = "";
        String email = "";
        String password = "";
        TipRata tip = null;
        double viteza = 0;
        double rezistenta = 0;

        // Validate ID
        while (true) {
            System.out.print("ID: ");
            id = scanner.nextLong();
            scanner.nextLine();
            if (id > 0) break;
            System.err.println("Error: ID must be positive");
        }

        // Validate username
        while (true) {
            System.out.print("NAME: ");
            username = scanner.nextLine();
            if (!username.trim().isEmpty()) break;
            System.err.println("Error: Username cannot be empty");
        }

        // Validate email
        while (true) {
            System.out.print("EMAIL: ");
            email = scanner.nextLine();
            if (email.contains("@")) break;
            System.err.println("Error: Invalid email format");
        }

        // Validate password
        while (true) {
            System.out.print("PASSWORD (min 6 chars): ");
            password = scanner.nextLine();
            if (password.length() >= 6) break;
            System.err.println("Error: Password must be at least 6 characters");
        }

        // Validate TipRata
        while (true) {
            System.out.println("TYPE: ");
            String tipStr = scanner.nextLine().toUpperCase();
            try {
                tip = TipRata.valueOf(tipStr);
                break;
            } catch (IllegalArgumentException e) {
                System.err.println("Error: Invalid TipRata value. Valid values: " + Arrays.toString(TipRata.values()));
            }
        }

        // Validate viteza
        while (true) {
            System.out.println("VITEZA:");
            viteza = scanner.nextDouble();
            scanner.nextLine();
            if (viteza > 0) break;
            System.err.println("Error: Viteza must be positive");
        }

        // Validate rezistenta
        while (true) {
            System.out.println("REZISTENTA:");
            rezistenta = scanner.nextDouble();
            scanner.nextLine();
            if (rezistenta > 0) break;
            System.err.println("Error: Rezistenta must be positive");
        }

        Duck duck = new Duck(id, username, email, password, tip, viteza, rezistenta);
        this.duckService.addDuck(duck);
        System.out.println("DUCK CREATED!");
    }
    private void createPerson() {
        System.out.println("CREATE PERSON:");

        long id = 0;
        String username = "";
        String email = "";
        String password = "";
        String nume = "";
        String prenume = "";
        String dataNasterii = "";
        String ocupatie = "";
        long nivelEmpatie = 0;

        // Validate ID
        while (true) {
            System.out.print("ID (positive number): ");
            id = scanner.nextLong();
            scanner.nextLine();
            if (id > 0) break;
            System.err.println("Error: ID must be positive");
        }

        // Validate username
        while (true) {
            System.out.print("USERNAME: ");
            username = scanner.nextLine();
            if (!username.trim().isEmpty()) break;
            System.err.println("Error: Username cannot be empty");
        }

        // Validate email
        while (true) {
            System.out.print("EMAIL: ");
            email = scanner.nextLine();
            if (email.contains("@")) break;
            System.err.println("Error: Invalid email format");
        }

        // Validate password
        while (true) {
            System.out.print("PASSWORD (min 6 chars): ");
            password = scanner.nextLine();
            if (password.length() >= 6) break;
            System.err.println("Error: Password must be at least 6 characters");
        }

        // Validate nume
        while (true) {
            System.out.print("NUME: ");
            nume = scanner.nextLine();
            if (!nume.trim().isEmpty()) break;
            System.err.println("Error: Nume cannot be empty");
        }

        // Validate prenume
        while (true) {
            System.out.print("PRENUME: ");
            prenume = scanner.nextLine();
            if (!prenume.trim().isEmpty()) break;
            System.err.println("Error: Prenume cannot be empty");
        }

        // Validate dataNasterii (simple format check)
        while (true) {
            System.out.print("DATA NASTERII (DD/MM/YYYY): ");
            dataNasterii = scanner.nextLine();
            if (dataNasterii.matches("\\d{2}/\\d{2}/\\d{4}")) break;
            System.err.println("Error: Date must be in DD/MM/YYYY format");
        }

        // Validate ocupatie
        while (true) {
            System.out.print("OCUPATIE: ");
            ocupatie = scanner.nextLine();
            if (!ocupatie.trim().isEmpty()) break;
            System.err.println("Error: Ocupatie cannot be empty");
        }

        // Validate nivelEmpatie (1-10 range example)
        while (true) {
            System.out.print("NIVEL EMPATIE (1-10): ");
            nivelEmpatie = scanner.nextLong();
            scanner.nextLine();
            if (nivelEmpatie >= 1 && nivelEmpatie <= 10) break;
            System.err.println("Error: Nivel empatie must be between 1 and 10");
        }

        Person person = new Person(id, username, email, password, nume, prenume,
                dataNasterii, ocupatie, nivelEmpatie);
        this.personService.addPerson(person);
        System.out.println("PERSON CREATED!");
    }
    private void createFriendship() {
        System.out.println("CREATE FRIENDSHIP:");

        long user1Id = 0;
        long user2Id = 0;

        // Validate first ID
        while (true) {
            System.out.print("First User ID: ");
            user1Id = scanner.nextLong();
            scanner.nextLine();
            if (user1Id > 0) break;
            System.err.println("Error: ID must be positive");
        }

        // Validate second ID
        while (true) {
            System.out.print("Second User ID: ");
            user2Id = scanner.nextLong();
            scanner.nextLine();
            if (user2Id > 0) break;
            System.err.println("Error: ID must be positive");
        }

        // Call service
        this.friendshipService.addFriendship(user1Id, user2Id);
    }
    private void createCard() {
        System.out.println("CREATE CARD:");

        long cardId = 0;
        String numeCard = "";
        List<Duck> membri = new ArrayList<>();

        // Validate card ID
        while (true) {
            System.out.print("Card ID (positive number): ");
            cardId = scanner.nextLong();
            scanner.nextLine();
            if (cardId > 0) break;
            System.err.println("Error: ID must be positive");
        }

        // Validate card name
        while (true) {
            System.out.print("Card Name: ");
            numeCard = scanner.nextLine();
            if (!numeCard.trim().isEmpty()) break;
            System.err.println("Error: Card name cannot be empty");
        }

        // Add ducks to card
        while (true) {
            System.out.print("Add duck to card? (yes/no): ");
            String response = scanner.nextLine().toLowerCase();

            if (response.equals("no") || response.equals("n")) {
                break;
            } else if (response.equals("yes") || response.equals("y")) {
                System.out.print("Enter Duck ID: ");
                long duckId = scanner.nextLong();
                scanner.nextLine();

                Duck duck = duckService.getDuck(duckId);
                if (duck != null) {
                    membri.add(duck);
                    System.out.println("Duck added to card!");
                } else {
                    System.err.println("Error: Duck not found with ID " + duckId);
                }
            }
        }

        Card card = new Card(cardId, numeCard, membri);
        this.cardService.addCard(card);
        System.out.println("CARD CREATED!");
    }
    private void createEvent() {
        System.out.println("CREATE EVENT:");

        String name = "";
        String description = "";

        // Clear the scanner buffer first
        scanner.nextLine();

        // Validate name
        while (true) {
            System.out.print("EVENT NAME: ");
            name = scanner.nextLine().trim();
            if (!name.isEmpty()) {
                if (name.length() <= 255) {
                    break;
                }
                System.out.println("Error: Name must be 255 characters or less");
            } else {
                System.out.println("Error: Event name cannot be empty");
            }
        }

        // Get description
        System.out.print("DESCRIPTION (press Enter to skip): ");
        description = scanner.nextLine().trim();
        if (description.length() > 1000) {
            System.out.println("Warning: Description truncated to 1000 characters");
            description = description.substring(0, 1000);
        }

        try {
            Event event = this.eventService.createEvent(name, description);
            System.out.println("EVENT CREATED SUCCESSFULLY! ID: " + event.getId());
        } catch (SQLException e) {
            System.out.println("Database error creating event: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error creating event: " + e.getMessage());
        }
    }
    private void deleteDuck() {
        System.out.println("DELETE DUCK:");

        long id = 0;

        // Validate ID
        while (true) {
            System.out.print("ID: ");
            id = scanner.nextLong();
            scanner.nextLine();
            if (id > 0) break;
            System.err.println("Error: ID must be positive");
        }

        this.duckService.deleteDuck(id);

        System.out.println("DUCK DELETED!");
    }
    private void deletePerson() {
        System.out.println("DELETE PERSON:");

        long id = 0;

        // Validate ID
        while (true) {
            System.out.print("ID: ");
            id = scanner.nextLong();
            scanner.nextLine();
            if (id > 0) break;
            System.err.println("Error: ID must be positive");
        }

        this.personService.deletePerson(id);

        System.out.println("PERSON DELETED!");
    }
    private void deleteFriendship() {
        System.out.println("DELETE FRIENDSHIP:");

        long user1Id = 0;
        long user2Id = 0;

        // Validate first ID
        while (true) {
            System.out.print("First User ID: ");
            user1Id = scanner.nextLong();
            scanner.nextLine();
            if (user1Id > 0) break;
            System.err.println("Error: ID must be positive");
        }

        // Validate second ID
        while (true) {
            System.out.print("Second User ID: ");
            user2Id = scanner.nextLong();
            scanner.nextLine();
            if (user2Id > 0) break;
            System.err.println("Error: ID must be positive");
        }

        // Call service
        this.friendshipService.removeFriendship(user1Id, user2Id);
    }
    private void deleteCard() {
        System.out.println("DELETE CARD:");

        long cardId = 0;

        // Validate card ID
        while (true) {
            System.out.print("Card ID to delete: ");
            cardId = scanner.nextLong();
            scanner.nextLine();
            if (cardId > 0) break;
            System.err.println("Error: ID must be positive");
        }

        // Ask for confirmation
        System.out.print("Are you sure you want to delete card " + cardId + "? (yes/no): ");
        String confirm = scanner.nextLine().toLowerCase();

        if (confirm.equals("yes") || confirm.equals("y")) {
            this.cardService.removeCard(cardId);
        } else {
            System.out.println("Deletion cancelled.");
        }
    }
    private void deleteEvent() {
        System.out.println("DELETE EVENT:");

        long eventId = 0;

        // Validate event ID
        while (true) {
            System.out.print("ENTER EVENT ID: ");
            if (scanner.hasNextLong()) {
                eventId = scanner.nextLong();
                scanner.nextLine(); // Consume newline

                // Check if event exists
                try {
                    Event event = eventService.getEventById(eventId);
                    if (event != null) {
                        System.out.println("Event found: " + event.getName());
                        System.out.println("Description: " + event.getDescription());
                        System.out.println("Subscribers: " + event.getSubscriberCount());

                        // Confirm deletion
                        System.out.print("Are you sure you want to delete this event? (y/n): ");
                        String confirm = scanner.nextLine().trim().toLowerCase();

                        if (confirm.equals("y") || confirm.equals("yes")) {
                            // Optional: Notify subscribers before deletion
                            System.out.print("Send notification to subscribers before deletion? (y/n): ");
                            String notify = scanner.nextLine().trim().toLowerCase();

                            if (notify.equals("y") || notify.equals("yes")) {
                                event.notifySubscribers("This event has been cancelled and will be removed");
                            }

                            eventService.deleteEvent(eventId);
                            System.out.println("EVENT DELETED SUCCESSFULLY!");
                        } else {
                            System.out.println("Deletion cancelled.");
                        }
                        break;
                    } else {
                        System.err.println("Error: No event found with ID " + eventId);
                    }
                } catch (SQLException e) {
                    System.err.println("Error checking event: " + e.getMessage());
                }
            } else {
                scanner.nextLine(); // Clear invalid input
                System.err.println("Error: ID must be a number");
            }
        }
    }
    private void printDucks() {
        System.out.println("PRINT DUCKS:");

        List<Duck> ducks = duckService.getDucks();

        for (Duck duck : ducks) {
            System.out.println(duck);
        }
    }
    private void printPersons() {
        System.out.println("PRINT PERSONS:");

        List<Person> persons = personService.getPersons();

        for (Person person : persons) {
            System.out.println(person);
        }
    }
    private void printFriendships() {
        System.out.println("PRINT FRIENDSHIPS:");

        List<Friendship> friendships =  friendshipService.getFriendships();

        for (Friendship friendship : friendships) {
            System.out.println(friendship);
        }
    }
    private void printCards() {
        System.out.println("PRINT CARDS:");

        List<Card> cards = cardService.getAllCards();
        for (Card card : cards) {
            System.out.println(card);
        }
    }
    private void printEvents() {
        System.out.println("LIST ALL EVENTS:");

        try {
            List<Event> events = eventService.getAllEvents();

            if (events.isEmpty()) {
                System.out.println("No events found.");
                return;
            }

            System.out.println("\n=== ALL EVENTS (" + events.size() + " total) ===");
            for (int i = 0; i < events.size(); i++) {
                Event event = events.get(i);
                System.out.println("\n[" + (i + 1) + "] ID: " + event.getId());
                System.out.println("    Name: " + event.getName());
                System.out.println("    Description: " +
                        (event.getDescription() != null && !event.getDescription().isEmpty() ?
                                event.getDescription() : "(No description)"));
                System.out.println("    Subscribers: " + event.getSubscriberCount());
                System.out.println("    Created: " +
                        (event.getCreatedAt() != null ? event.getCreatedAt() : "N/A"));
                System.out.println("    Updated: " +
                        (event.getUpdatedAt() != null ? event.getUpdatedAt() : "N/A"));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving events: " + e.getMessage());
        }
    }
    private void subscribeToEvent() {
        System.out.println("SUBSCRIBE USER TO EVENT:");

        try {
            System.out.print("Enter Event ID: ");
            long eventId = scanner.nextLong();
            scanner.nextLine();

            System.out.print("Enter User ID: ");
            long userId = scanner.nextLong();
            scanner.nextLine();

            // Try to get the user as Person first, then as Duck
            User user = null;

            // Check if it's a Person
            Person person = personService.getPerson(userId);
            if (person != null) {
                user = person;
            } else {
                // Check if it's a Duck
                Duck duck = duckService.getDuck(userId);
                if (duck != null) {
                    user = duck;
                }
            }

            if (user == null) {
                System.out.println("Error: User not found with ID " + userId);
                return;
            }

            // Subscribe using the event service
            eventService.subscribeToEvent(eventId, user);
            System.out.println(user.getClass().getSimpleName() + " " + user.getUsername() + " subscribed to event.");

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
