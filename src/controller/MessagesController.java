package controller;

import domain.Message;
import service.MessageService;
import service.DuckService;
import service.PersonService;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

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

        for (Message m : messages) {
            String user = getUsername(m.getSenderId());
            String time = m.getTimestamp().toString(); // ISO format is fine
            String txt = "[" + time + "] " + user + ": " + m.getContent();

            Label label = new Label(txt);
            label.setWrapText(true);

            chatBox.getChildren().add(label);
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
