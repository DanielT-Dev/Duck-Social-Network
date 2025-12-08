package controller;

import domain.Duck;
import domain.Friendship;
import domain.Person;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import repository.DuckRepository;
import repository.FriendshipRepository;
import repository.PersonRepository;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    // Duck tab components
    @FXML private TableView<Duck> duckTable;
    @FXML private TableColumn<Duck, Long> duckIdColumn;
    @FXML private TableColumn<Duck, String> duckUsernameColumn;
    @FXML private TableColumn<Duck, String> duckEmailColumn;
    @FXML private TableColumn<Duck, String> duckTypeColumn;
    @FXML private TableColumn<Duck, Double> duckSpeedColumn;
    @FXML private TableColumn<Duck, Double> duckResistanceColumn;
    @FXML private Button duckPrevButton;
    @FXML private Button duckNextButton;
    @FXML private Label duckPageLabel;

    // Person tab components
    @FXML private TableView<Person> personTable;
    @FXML private TableColumn<Person, Long> personIdColumn;
    @FXML private TableColumn<Person, String> personUsernameColumn;
    @FXML private TableColumn<Person, String> personEmailColumn;
    @FXML private TableColumn<Person, String> personFirstNameColumn;
    @FXML private TableColumn<Person, String> personLastNameColumn;
    @FXML private TableColumn<Person, String> personOccupationColumn;
    @FXML private TableColumn<Person, Long> personEmpathyColumn;
    @FXML private Button personPrevButton;
    @FXML private Button personNextButton;
    @FXML private Label personPageLabel;

    // Friendship tab components
    @FXML private TableView<Friendship> friendshipTable;
    @FXML private TableColumn<Friendship, Long> friendshipUser1Column;
    @FXML private TableColumn<Friendship, Long> friendshipUser2Column;
    @FXML private TableColumn<Friendship, String> friendshipCreatedAtColumn;
    @FXML private Button friendshipPrevButton;
    @FXML private Button friendshipNextButton;
    @FXML private Label friendshipPageLabel;

    @FXML private TabPane tabPane;

    private DuckRepository duckRepository;
    private PersonRepository personRepository;
    private FriendshipRepository friendshipRepository;

    private int duckPage = 1;
    private int personPage = 1;
    private int friendshipPage = 1;
    private final int pageSize = 6;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        duckRepository = new DuckRepository();
        personRepository = new PersonRepository();
        friendshipRepository = new FriendshipRepository();

        setupDuckTable();
        setupPersonTable();
        setupFriendshipTable();

        loadDucks();
        loadPersons();
        loadFriendships();

        // Listen to tab changes to refresh data
        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab.getText().equals("Ducks")) {
                loadDucks();
            } else if (newTab.getText().equals("Persons")) {
                loadPersons();
            } else if (newTab.getText().equals("Friendships")) {
                loadFriendships();
            }
        });
    }

    private void setupDuckTable() {
        duckIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        duckUsernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        duckEmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        duckTypeColumn.setCellValueFactory(new PropertyValueFactory<>("tip"));
        duckSpeedColumn.setCellValueFactory(new PropertyValueFactory<>("viteza"));
        duckResistanceColumn.setCellValueFactory(new PropertyValueFactory<>("rezistenta"));
    }

    private void setupPersonTable() {
        personIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        personUsernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        personEmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        personFirstNameColumn.setCellValueFactory(new PropertyValueFactory<>("nume"));
        personLastNameColumn.setCellValueFactory(new PropertyValueFactory<>("prenume"));
        personOccupationColumn.setCellValueFactory(new PropertyValueFactory<>("ocupatie"));
        personEmpathyColumn.setCellValueFactory(new PropertyValueFactory<>("nivelEmpatie"));
    }

    private void setupFriendshipTable() {
        friendshipUser1Column.setCellValueFactory(new PropertyValueFactory<>("user1Id"));
        friendshipUser2Column.setCellValueFactory(new PropertyValueFactory<>("user2Id"));
        friendshipCreatedAtColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
    }

    private void loadDucks() {
        try {
            int totalDucks = duckRepository.getTotalCount();
            int offset = (duckPage - 1) * pageSize;
            List<Duck> ducks = duckRepository.findAllPaginated(offset, pageSize);

            ObservableList<Duck> duckList = FXCollections.observableArrayList(ducks);
            duckTable.setItems(duckList);

            int totalPages = (int) Math.ceil((double) totalDucks / pageSize);
            duckPageLabel.setText("Page " + duckPage + " of " + totalPages + " (" + totalDucks + " total)");
            duckPrevButton.setDisable(duckPage <= 1);
            duckNextButton.setDisable(duckPage >= totalPages);

        } catch (SQLException e) {
            showAlert("Database Error", "Failed to load ducks: " + e.getMessage());
        }
    }

    private void loadPersons() {
        try {
            int totalPersons = personRepository.getTotalCount();
            int offset = (personPage - 1) * pageSize;
            List<Person> persons = personRepository.findAllPaginated(offset, pageSize);

            ObservableList<Person> personList = FXCollections.observableArrayList(persons);
            personTable.setItems(personList);

            int totalPages = (int) Math.ceil((double) totalPersons / pageSize);
            personPageLabel.setText("Page " + personPage + " of " + totalPages + " (" + totalPersons + " total)");
            personPrevButton.setDisable(personPage <= 1);
            personNextButton.setDisable(personPage >= totalPages);

        } catch (SQLException e) {
            showAlert("Database Error", "Failed to load persons: " + e.getMessage());
        }
    }

    private void loadFriendships() {
        try {
            int totalFriendships = friendshipRepository.getTotalCount();
            int offset = (friendshipPage - 1) * pageSize;
            List<Friendship> friendships = friendshipRepository.findAllPaginated(offset, pageSize);

            ObservableList<Friendship> friendshipList = FXCollections.observableArrayList(friendships);
            friendshipTable.setItems(friendshipList);

            int totalPages = (int) Math.ceil((double) totalFriendships / pageSize);
            friendshipPageLabel.setText("Page " + friendshipPage + " of " + totalPages + " (" + totalFriendships + " total)");
            friendshipPrevButton.setDisable(friendshipPage <= 1);
            friendshipNextButton.setDisable(friendshipPage >= totalPages);

        } catch (SQLException e) {
            showAlert("Database Error", "Failed to load friendships: " + e.getMessage());
        }
    }

    @FXML
    private void handleDuckPrevious() {
        if (duckPage > 1) {
            duckPage--;
            loadDucks();
        }
    }

    @FXML
    private void handleDuckNext() {
        duckPage++;
        loadDucks();
    }

    @FXML
    private void handlePersonPrevious() {
        if (personPage > 1) {
            personPage--;
            loadPersons();
        }
    }

    @FXML
    private void handlePersonNext() {
        personPage++;
        loadPersons();
    }

    @FXML
    private void handleFriendshipPrevious() {
        if (friendshipPage > 1) {
            friendshipPage--;
            loadFriendships();
        }
    }

    @FXML
    private void handleFriendshipNext() {
        friendshipPage++;
        loadFriendships();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}