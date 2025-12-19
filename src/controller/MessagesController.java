package controller;

import domain.Message;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import repository.DuckRepository;
import repository.MessageRepository;
import repository.PersonRepository;
import service.DuckService;
import service.MessageService;
import service.PersonService;

import javafx.fxml.FXML;

import java.time.format.DateTimeFormatter;
import java.util.*;

public class MessagesController {

    @FXML private ListView<Long> userList;
    @FXML private VBox chatBox;
    @FXML private ScrollPane chatScroll;
    @FXML private TextField messageField;
    @FXML private Label replyingLabel; // NEW: shows "replying to ..."

    private final DuckRepository duckRepository = new DuckRepository();
    private final MessageRepository messageRepository = new MessageRepository();
    private final PersonRepository personRepository = new PersonRepository();

    private final MessageService messageService = new MessageService(messageRepository);
    private final DuckService duckService = new DuckService(duckRepository);
    private final PersonService personService = new PersonService(personRepository);

    private long currentUser = 1;
    private long selectedUser = -1;

    private Message replyingTo = null;
    private final Map<Long, HBox> messageUIMap = new HashMap<>();

    @FXML
    public void initialize() {
        userList.getItems().clear();
        userList.getItems().addAll(getAllUserIds());

        userList.setCellFactory(lv -> new ListCell<Long>() {
            @Override
            protected void updateItem(Long id, boolean empty) {
                super.updateItem(id, empty);
                setText((empty || id == null) ? null : getUsername(id));
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
        messageUIMap.clear();

        var messages = messageService.getConversation(currentUser, selectedUser);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM HH:mm");

        for (Message m : messages) {
            String user = getUsername(m.getSenderId());
            String time = m.getTimestamp().format(formatter);

            Label messageLabel = new Label("[" + time + "] " + user + ": " + m.getContent());
            messageLabel.setWrapText(true);
            messageLabel.setFont(new Font(16));
            messageLabel.setPadding(new Insets(5));

            HBox messageContainer = new HBox();
            messageContainer.setMaxWidth(400);
            messageContainer.setPadding(new Insets(5, 10, 5, 10));

            VBox messageContent = new VBox(5);
            messageContent.getChildren().add(messageLabel);

            // Reply button inside message box
            Button replyBtn = new Button("Reply");
            replyBtn.setStyle("-fx-background-color: #4a90e2; -fx-text-fill: white; -fx-background-radius: 5;");
            replyBtn.setOnAction(ev -> {
                replyingTo = m;
                replyingLabel.setText("Replying to: " + user + " - " + m.getContent());
            });

            HBox buttonBox = new HBox(replyBtn);
            buttonBox.setAlignment(Pos.CENTER_RIGHT);
            messageContent.getChildren().add(buttonBox);

            // "Go to original" if this message is a reply
            if (m.getReplyToId() != null && m.getReplyToId() != 0) {
                Button goToBtn = new Button("Go to original");
                goToBtn.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white; -fx-background-radius: 5;");
                goToBtn.setOnAction(ev -> {
                    HBox originalBox = messageUIMap.get(m.getReplyToId());
                    if (originalBox != null) {
                        chatScroll.setVvalue(originalBox.getLayoutY() / chatBox.getHeight());
                        originalBox.setBorder(new Border(new BorderStroke(Color.YELLOW,
                                BorderStrokeStyle.SOLID, new CornerRadii(5), new BorderWidths(2))));
                    }
                });
                buttonBox.getChildren().add(goToBtn);
                buttonBox.setSpacing(5);
            }

            if (m.getSenderId() == currentUser) {
                messageContainer.setAlignment(Pos.CENTER_RIGHT);
                messageLabel.setBackground(new Background(new BackgroundFill(Color.RED, new CornerRadii(5), Insets.EMPTY)));
                messageLabel.setTextFill(Color.WHITE);
            } else {
                messageContainer.setAlignment(Pos.CENTER_LEFT);
                messageLabel.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, new CornerRadii(5), Insets.EMPTY)));
                messageLabel.setTextFill(Color.BLACK);
            }

            messageContainer.getChildren().add(messageContent);
            chatBox.getChildren().add(messageContainer);
            messageUIMap.put(m.getId(), messageContainer);
        }
    }

    @FXML
    public void handleSendMessage() {
        if (selectedUser == -1) return;

        String content = messageField.getText().trim();
        if (content.isEmpty()) return;

        Long replyId = replyingTo != null ? replyingTo.getId() : null;
        messageService.sendMessage(currentUser, selectedUser, content, replyId);
        messageField.clear();

        if (replyingTo != null) {
            replyingLabel.setText("Replying to: " + replyingTo.getContent());
            replyingLabel.setStyle("-fx-background-color: yellow; -fx-padding: 5px;"); // highlight

            // Reset highlight after 2 seconds
            javafx.animation.Timeline timeline = new javafx.animation.Timeline(
                    new javafx.animation.KeyFrame(
                            javafx.util.Duration.seconds(2),
                            evt -> replyingLabel.setStyle("")
                    )
            );
            timeline.setCycleCount(1);
            timeline.play();
        }

        replyingTo = null;
        loadChat();
    }


    private String getUsername(long id) {
        var duck = duckService.getDucks().stream().filter(d -> d.getId() == id).findFirst().orElse(null);
        if (duck != null) return duck.getUsername();

        var person = personService.getPersons().stream().filter(p -> p.getId() == id).findFirst().orElse(null);
        return person != null ? person.getUsername() : "Unknown";
    }
}
