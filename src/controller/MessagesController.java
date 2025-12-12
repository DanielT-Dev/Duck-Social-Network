package controller;

import domain.Message;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import service.MessageService;
import service.DuckService;
import service.PersonService;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MessagesController {

    @FXML private ListView<Long> userList;
    @FXML private VBox chatBox;
    @FXML private TextField messageField;

    private final MessageService messageService = new MessageService();
    private final DuckService duckService = new DuckService();
    private final PersonService personService = new PersonService();

    // TEMP: replace with logged-in user from your login system
    private long currentUser = 1;

    private long selectedUser = -1;

    @FXML
    public void initialize() {
        userList.getItems().clear();
        userList.getItems().addAll(getAllUserIds());

        // display usernames instead of IDs
        userList.setCellFactory(lv -> new ListCell<Long>() {
            @Override
            protected void updateItem(Long id, boolean empty) {
                super.updateItem(id, empty);
                if (empty || id == null) {
                    setText(null);
                } else {
                    setText(getUsername(id));
                }
            }
        });

        userList.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                selectedUser = newV;
                loadChat();
            }
        });
    }

    public void setCurrentUser(long userId) {
        this.currentUser = userId;
    }

    private List<Long> getAllUserIds() {
        List<Long> ids = new ArrayList<>();
        duckService.getDucks().forEach(d -> ids.add(d.getId()));
        personService.getPersons().forEach(p -> ids.add(p.getId()));
        return ids;
    }

    private void loadChat() {
        chatBox.getChildren().clear();

        var messages = messageService.getConversation(currentUser, selectedUser);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM HH:mm"); // day + month name + hour:minute

        for (Message m : messages) {
            String user = getUsername(m.getSenderId());
            String time = m.getTimestamp().format(formatter);
            String content = m.getContent();

            Label label = new Label("[" + time + "] " + user + ": " + content);
            label.setWrapText(true);
            label.setFont(new Font(16)); // slightly bigger font

            HBox messageContainer = new HBox();
            messageContainer.setPadding(new Insets(5, 15, 5, 15)); // more padding around message
            messageContainer.setMaxWidth(400); // optional: limit message width

            if (m.getSenderId() == currentUser) {
                // Current user's message on the right
                messageContainer.setAlignment(Pos.CENTER_RIGHT);
                label.setBackground(new Background(new BackgroundFill(Color.RED, new CornerRadii(5), Insets.EMPTY)));
                label.setTextFill(Color.WHITE);
            } else {
                // Other person's message on the left
                messageContainer.setAlignment(Pos.CENTER_LEFT);
                label.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, new CornerRadii(5), Insets.EMPTY)));
                label.setTextFill(Color.BLACK);
            }

            messageContainer.getChildren().add(label);
            chatBox.getChildren().add(messageContainer);
        }
    }


    @FXML
    public void handleSendMessage() {
        if (selectedUser == -1) return;

        String content = messageField.getText().trim();
        if (content.isEmpty()) return;

        messageService.sendMessage(currentUser, selectedUser, content);
        messageField.clear();
        loadChat();
    }

    private String getUsername(long id) {
        var duck = duckService.getDucks().stream()
                .filter(d -> d.getId() == id)
                .findFirst()
                .orElse(null);

        if (duck != null) return duck.getUsername();

        var person = personService.getPersons().stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElse(null);

        return person != null ? person.getUsername() : "Unknown";
    }

}
