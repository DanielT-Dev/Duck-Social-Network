package controller;

import domain.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import repository.DuckRepository;
import repository.FriendshipRepository;
import repository.PersonRepository;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import service.DuckService;
import service.FriendshipService;
import service.LoginService;
import service.PersonService;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Button;
import util.SecurityUtils;

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
    private final DuckService duckService = new DuckService();
    private final PersonService personService = new PersonService();
    private final FriendshipService friendshipService = new FriendshipService();
    private final LoginService loginService = new LoginService(duckService, personService);

    private int duckPage = 1;
    private int personPage = 1;
    private int friendshipPage = 1;
    private final int pageSize = 6;

    // Communities tab components
    @FXML private Label totalCommunitiesLabel;
    @FXML private Label mostSocialCommunityLabel;
    @FXML private TableView<User> communityTable;
    @FXML private TableColumn<User, Long> userIdColumn;
    @FXML private TableColumn<User, String> userUsernameColumn;

    @FXML
    private Label loginStatusLabel;
    private long loggedInUserId = -1; // -1 means not logged in

    @FXML private ComboBox<String> duckTypeFilter;

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        duckRepository = new DuckRepository();
        personRepository = new PersonRepository();
        friendshipRepository = new FriendshipRepository();

        setupDuckTable();
        setupPersonTable();
        setupFriendshipTable();
        setupCommunityTable();

        // Initialize duck type filter
        duckTypeFilter.getItems().addAll(
                "All",
                "FLYING",
                "SWIMMING",
                "SWIMMING_AND_FLYING"
        );
        duckTypeFilter.setValue("All");
        duckTypeFilter.setOnAction(ev -> applyDuckFilter());

        loadDucks();
        loadPersons();
        loadFriendships();
        loadCommunities();

        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab.getText().equals("Ducks")) {
                loadDucks();
            } else if (newTab.getText().equals("Persons")) {
                loadPersons();
            } else if (newTab.getText().equals("Friendships")) {
                loadFriendships();
            } else if (newTab.getText().equals("Communities")) {
                loadCommunities();
            }
        });
    }

    private void applyDuckFilter() {
        String selectedType = duckTypeFilter.getValue();
        if (selectedType == null || selectedType.equals("All")) {
            loadDucks(); // show all ducks
        } else {
            List<Duck> filtered = duckService.getDucks().stream()
                    .filter(d -> d.getTip().name().equals(selectedType))
                    .toList();
            duckTable.setItems(FXCollections.observableArrayList(filtered));
        }
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

    private void setupCommunityTable() {
        userIdColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleLongProperty(data.getValue().getId()).asObject());
        userUsernameColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getUsername()));
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

    private void loadCommunities() {
        int total = friendshipService.getTotalCommunities();
        totalCommunitiesLabel.setText("Total communities: " + total);

        List<Long> userIds = friendshipService.getMostSocialCommunityWithMembers();

        List<User> users = userIds.stream().map(id -> {
            User u = personService.getPerson(id);
            if (u != null) return u;
            return duckService.getDucks().stream().filter(d -> d.getId() == id).findFirst().orElse(null);
        }).filter(u -> u != null).toList();

        mostSocialCommunityLabel.setText("Most social community: " + users.size() + " users");

        communityTable.setItems(FXCollections.observableArrayList(users));
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

    @FXML
    private void createDuck() {
        Dialog<Duck> dialog = new Dialog<>();
        dialog.setTitle("Create Duck");
        dialog.setHeaderText("Enter duck details:");

        ButtonType createButtonType = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        TextField idField = new TextField();
        idField.setPromptText("ID");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        TextField passwordField = new TextField();
        passwordField.setPromptText("Password");
        TextField typeField = new TextField();
        typeField.setPromptText("Type (MALLARD, etc)");
        TextField speedField = new TextField();
        speedField.setPromptText("Speed");
        TextField resistanceField = new TextField();
        resistanceField.setPromptText("Resistance");

        grid.add(new Label("ID:"), 0, 0);
        grid.add(idField, 1, 0);
        grid.add(new Label("Username:"), 0, 1);
        grid.add(usernameField, 1, 1);
        grid.add(new Label("Email:"), 0, 2);
        grid.add(emailField, 1, 2);
        grid.add(new Label("Password:"), 0, 3);
        grid.add(passwordField, 1, 3);
        grid.add(new Label("Type:"), 0, 4);
        grid.add(typeField, 1, 4);
        grid.add(new Label("Speed:"), 0, 5);
        grid.add(speedField, 1, 5);
        grid.add(new Label("Resistance:"), 0, 6);
        grid.add(resistanceField, 1, 6);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                try {
                    long id = Long.parseLong(idField.getText());
                    String username = usernameField.getText();
                    String email = emailField.getText();
                    String password = passwordField.getText();
                    TipRata type = TipRata.valueOf(typeField.getText().toUpperCase());
                    double speed = Double.parseDouble(speedField.getText());
                    double resistance = Double.parseDouble(resistanceField.getText());

                    return new Duck(id, username, email, password, type, speed, resistance);
                } catch (Exception e) {
                    showAlert("Input Error", "Invalid input: " + e.getMessage());
                    return null;
                }
            }
            return null;
        });

        Optional<Duck> result = dialog.showAndWait();
        result.ifPresent(duck -> {
            // Save duck to database
            duckService.addDuck(duck);
            loadDucks();
        });
    }

    @FXML
    private void createPerson() {
        Dialog<Person> dialog = new Dialog<>();
        dialog.setTitle("Create Person");
        dialog.setHeaderText("Enter person details:");

        ButtonType createButtonType = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        TextField idField = new TextField();
        idField.setPromptText("ID");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        TextField passwordField = new TextField();
        passwordField.setPromptText("Password");
        TextField firstNameField = new TextField();
        firstNameField.setPromptText("First Name");
        TextField lastNameField = new TextField();
        lastNameField.setPromptText("Last Name");
        TextField birthDateField = new TextField();
        birthDateField.setPromptText("Birth Date (YYYY-MM-DD)");
        TextField occupationField = new TextField();
        occupationField.setPromptText("Occupation");
        TextField empathyField = new TextField();
        empathyField.setPromptText("Empathy Level");

        grid.add(new Label("ID:"), 0, 0);
        grid.add(idField, 1, 0);
        grid.add(new Label("Username:"), 0, 1);
        grid.add(usernameField, 1, 1);
        grid.add(new Label("Email:"), 0, 2);
        grid.add(emailField, 1, 2);
        grid.add(new Label("Password:"), 0, 3);
        grid.add(passwordField, 1, 3);
        grid.add(new Label("First Name:"), 0, 4);
        grid.add(firstNameField, 1, 4);
        grid.add(new Label("Last Name:"), 0, 5);
        grid.add(lastNameField, 1, 5);
        grid.add(new Label("Birth Date:"), 0, 6);
        grid.add(birthDateField, 1, 6);
        grid.add(new Label("Occupation:"), 0, 7);
        grid.add(occupationField, 1, 7);
        grid.add(new Label("Empathy:"), 0, 8);
        grid.add(empathyField, 1, 8);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                try {
                    long id = Long.parseLong(idField.getText());
                    String username = usernameField.getText();
                    String email = emailField.getText();
                    String password = passwordField.getText();
                    String firstName = firstNameField.getText();
                    String lastName = lastNameField.getText();
                    String birthDate = birthDateField.getText();
                    String occupation = occupationField.getText();
                    long empathy = Long.parseLong(empathyField.getText());

                    return new Person(id, username, email, password, firstName, lastName, birthDate, occupation, empathy);
                } catch (Exception e) {
                    showAlert("Input Error", "Invalid input: " + e.getMessage());
                    return null;
                }
            }
            return null;
        });

        Optional<Person> result = dialog.showAndWait();
        result.ifPresent(person -> {
            personService.addPerson(person);
            loadPersons();
        });
    }

    @FXML
    private void createFriendship() {
        Dialog<Friendship> dialog = new Dialog<>();
        dialog.setTitle("Create Friendship");
        dialog.setHeaderText("Enter user IDs to create friendship:");

        ButtonType createButtonType = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        TextField user1Field = new TextField();
        user1Field.setPromptText("User 1 ID");
        TextField user2Field = new TextField();
        user2Field.setPromptText("User 2 ID");

        grid.add(new Label("User 1 ID:"), 0, 0);
        grid.add(user1Field, 1, 0);
        grid.add(new Label("User 2 ID:"), 0, 1);
        grid.add(user2Field, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                try {
                    long user1Id = Long.parseLong(user1Field.getText());
                    long user2Id = Long.parseLong(user2Field.getText());

                    if (user1Id == user2Id) {
                        showAlert("Input Error", "Users cannot be friends with themselves");
                        return null;
                    }

                    return new Friendship(user1Id, user2Id);
                } catch (Exception e) {
                    showAlert("Input Error", "Invalid input: " + e.getMessage());
                    return null;
                }
            }
            return null;
        });

        Optional<Friendship> result = dialog.showAndWait();
        result.ifPresent(friendship -> {
            try {
                friendshipRepository.save(friendship);
                loadFriendships();
            } catch (SQLException e) {
                showAlert("Database Error", "Failed to create friendship: " + e.getMessage());
            }
        });
    }

    @FXML
    private void deleteSelectedDuck() {
        Duck selected = duckTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Selection Error", "Please select a duck to delete");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete Duck");
        confirm.setContentText("Are you sure you want to delete duck: " + selected.getUsername() + "?");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // Delete from database
                // duckRepository.delete(selected.getId());
                duckService.deleteDuck(selected.getId());
                loadDucks();
            } catch (Exception e) {
                showAlert("Delete Error", "Failed to delete duck: " + e.getMessage());
            }
        }
    }

    @FXML
    private void deleteSelectedPerson() {
        Person selected = personTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Selection Error", "Please select a person to delete");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete Person");
        confirm.setContentText("Are you sure you want to delete person: " + selected.getUsername() + "?");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // Delete from database
                // personRepository.delete(selected.getId());
                personService.deletePerson(selected.getId());
                loadPersons();
            } catch (Exception e) {
                showAlert("Delete Error", "Failed to delete person: " + e.getMessage());
            }
        }
    }

    @FXML
    private void deleteSelectedFriendship() {
        Friendship selected = friendshipTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Selection Error", "Please select a friendship to delete");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete Friendship");
        confirm.setContentText("Are you sure you want to delete friendship between users " +
                selected.getUser1Id() + " and " + selected.getUser2Id() + "?");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                //friendshipRepository.delete(selected.getUser1Id(), selected.getUser2Id());
                friendshipService.removeFriendship(selected.getUser1Id(), selected.getUser2Id());
                loadFriendships();
            } catch (Exception e) {
                showAlert("Delete Error", "Failed to delete friendship: " + e.getMessage());
            }
        }
    }

    @FXML
    private void refreshCurrentTab() {
        String currentTab = tabPane.getSelectionModel().getSelectedItem().getText();

        if (currentTab.equals("Ducks")) {
            duckPage = 1;
            loadDucks();
        } else if (currentTab.equals("Persons")) {
            personPage = 1;
            loadPersons();
        } else if (currentTab.equals("Friendships")) {
            friendshipPage = 1;
            loadFriendships();
        }
    }

    @FXML
    private void openLoginWindow() {
        Stage loginStage = new Stage();
        loginStage.setTitle("Log In");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField();
        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();

        Button submitButton = new Button("Submit");
        submitButton.setOnAction(e -> {
            String email = emailField.getText();
            String password = passwordField.getText();
            System.out.println("Logging in with: " + email + " / " + password);
            loginStage.close(); // Close after submit (replace with actual login logic)
        });

        grid.add(emailLabel, 0, 0);
        grid.add(emailField, 1, 0);
        grid.add(passwordLabel, 0, 1);
        grid.add(passwordField, 1, 1);
        grid.add(submitButton, 1, 2);

        Scene scene = new Scene(grid, 300, 200);
        loginStage.setScene(scene);
        loginStage.show();

        submitButton.setOnAction(event -> {
            handleLogin(emailField.getText(), passwordField.getText()); // update main window label
            loginStage.close(); // close login window
        });

    }

    public void setLoggedInUserId(long userId) {
        this.loggedInUserId = userId;
    }

    @FXML
    private void handleLogin(String email, String password) {
        String hashedInput = SecurityUtils.hashPassword(password.trim());
        String emailTrimmed = email.trim();

        // Check DuckService first
        Duck loggedDuck = duckService.getDucks().stream()
                .filter(d -> d.getEmail().equals(emailTrimmed) && d.getPassword().equals(hashedInput))
                .findFirst().orElse(null);

        if (loggedDuck != null) {
            loginStatusLabel.setText("Logged in as Duck: " + loggedDuck.getUsername());
            setLoggedInUserId(loggedDuck.getId());
            return;
        }

        // Check PersonService
        Person loggedPerson = personService.getPersons().stream()
                .filter(p -> p.getEmail().equals(emailTrimmed) && p.getPassword().equals(hashedInput))
                .findFirst().orElse(null);

        personService.getPersons().forEach(p ->
                System.out.println(p.getUsername() + " : " + p.getPassword())
        );
        System.out.println("Input hash: " + hashedInput);


        if (loggedPerson != null) {

            loginStatusLabel.setText("Logged in as Person: " + loggedPerson.getUsername());
            setLoggedInUserId(loggedPerson.getId());
            return;
        }

        // Login failed
        loginStatusLabel.setText("Login failed");
        setLoggedInUserId(-1); // no user logged in
    }


    @FXML
    public void openMessagesWindow() {
        if (loggedInUserId == -1) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "You must be logged in to view messages!");
            alert.show();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/messages_view.fxml"));
            Parent root = loader.load();

            MessagesController controller = loader.getController();
            controller.setCurrentUser(loggedInUserId);

            Stage stage = new Stage();
            stage.setScene(new Scene(root, 600, 500));
            stage.setTitle("Messages");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}