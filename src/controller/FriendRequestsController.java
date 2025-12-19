package controller;

import domain.Duck;
import domain.Person;
import domain.FriendRequest;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import service.DuckService;
import service.PersonService;
import service.FriendRequestService;

import java.util.List;
import java.util.Optional;

public class FriendRequestsController {

    @FXML private TextField usernameField;
    @FXML private Button sendRequestButton;

    @FXML private TableView<FriendRequest> table;
    @FXML private TableColumn<FriendRequest, String> fromCol;
    @FXML private TableColumn<FriendRequest, Void> actionsCol;

    private long currentUserId;

    private final FriendRequestService friendRequestService = new FriendRequestService();
    private final PersonService personService = new PersonService();
    private final DuckService duckService = new DuckService();

    public void setCurrentUser(long userId) {
        this.currentUserId = userId;
        refreshRequests();
    }

    @FXML
    public void initialize() {
        sendRequestButton.setOnAction(e -> sendFriendRequest());

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
                refreshRequests();
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
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

        friendRequestService.sendRequest(currentUserId, receiverId.get());
        usernameField.clear();
        refreshRequests();
    }

    @FXML
    public void refreshRequests() {
        List<FriendRequest> requests =
                friendRequestService.getPendingRequestsForUser(currentUserId);

        table.setItems(FXCollections.observableArrayList(requests));
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
