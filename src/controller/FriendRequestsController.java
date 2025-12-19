package controller;

import domain.Duck;
import domain.Person;
import domain.FriendRequest;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import repository.DuckRepository;
import repository.FriendRequestRepository;
import repository.FriendshipRepository;
import repository.PersonRepository;
import service.DuckService;
import service.FriendshipService;
import service.PersonService;
import service.FriendRequestService;

import java.util.Optional;

public class FriendRequestsController {

    @FXML private TextField usernameField;
    @FXML private Button sendRequestButton;

    @FXML private TableView<FriendRequest> table;
    @FXML private TableColumn<FriendRequest, String> fromCol;
    @FXML private TableColumn<FriendRequest, Void> actionsCol;

    private long currentUserId;

    private final DuckRepository duckRepository = new DuckRepository();
    private final FriendshipRepository friendshipRepository = new FriendshipRepository();
    private final FriendRequestRepository friendRequestRepository = new FriendRequestRepository();
    private final PersonRepository personRepository = new PersonRepository();

    private final FriendshipService friendshipService = new FriendshipService(friendshipRepository);
    private final FriendRequestService friendRequestService = new FriendRequestService(duckRepository, personRepository, friendRequestRepository, friendshipService);
    private final PersonService personService = new PersonService(personRepository);
    private final DuckService duckService = new DuckService(duckRepository);

    public void setCurrentUser(long userId) {
        this.currentUserId = userId;
        friendRequestService.refreshPendingRequests(currentUserId);
    }

    @FXML
    public void initialize() {
        sendRequestButton.setOnAction(e -> sendFriendRequest());

        // Bind TableView to observable list
        table.setItems(friendRequestService.getPendingRequestsObservable());

        fromCol.setCellValueFactory(c ->
                new SimpleStringProperty(getUsernameById(c.getValue().getSenderId()))
        );

        actionsCol.setCellFactory(col -> new TableCell<>() {
            private final Button accept = new Button("Accept");
            private final Button reject = new Button("Reject");
            private final HBox box = new HBox(8, accept, reject);

            {
                box.setAlignment(Pos.CENTER);

                accept.setOnAction(e -> respond("accepted"));
                reject.setOnAction(e -> respond("rejected"));
            }

            private void respond(String status) {
                FriendRequest fr = getTableView().getItems().get(getIndex());
                friendRequestService.respondToRequest(
                        fr.getSenderId(),
                        fr.getReceiverId(),
                        status
                );
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });

        // Optional: listen for changes to update other UI elements
        friendRequestService.getPendingRequestsObservable().addListener((ListChangeListener<FriendRequest>) change -> {
            // Example: update badge count
            // badgeLabel.setText(String.valueOf(friendRequestService.getPendingRequestsObservable().size()));
        });
    }

    private void sendFriendRequest() {
        String username = usernameField.getText().trim();
        if (username.isEmpty()) {
            showAlert("Input Error", "Please enter a username");
            return;
        }

        Optional<Long> receiverId = personService.getPersons().stream()
                .filter(p -> p.getUsername().equals(username))
                .map(Person::getId)
                .findFirst();

        if (receiverId.isEmpty()) {
            receiverId = duckService.getDucks().stream()
                    .filter(d -> d.getUsername().equals(username))
                    .map(Duck::getId)
                    .findFirst();
        }

        if (receiverId.isEmpty()) {
            showAlert("User not found", "No user with username: " + username);
            return;
        }

        long receiver = receiverId.get();

        try {
            if (friendRequestService.friendshipService.areFriends(currentUserId, receiver)) {
                showAlert("Friendship Exists", "You are already friends with " + username);
                return;
            }
        } catch (Exception e) {
            showAlert("Error", "Could not check friendship: " + e.getMessage());
            return;
        }

        if (friendRequestService.requestExists(currentUserId, receiver)) {
            showAlert("Request Exists", "You have already sent a friend request to " + username);
            return;
        }

        friendRequestService.sendRequest(currentUserId, receiver);
        usernameField.clear();
    }

    private String getUsernameById(long userId) {
        return personService.getPersons().stream()
                .filter(p -> p.getId() == userId)
                .map(Person::getUsername)
                .findFirst()
                .or(() -> duckService.getDucks().stream()
                        .filter(d -> d.getId() == userId)
                        .map(Duck::getUsername)
                        .findFirst())
                .orElse("Unknown");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
