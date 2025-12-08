package controller;

import domain.Duck;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import repository.DuckRepository;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class DuckTableController implements Initializable {

    @FXML private TableView<Duck> duckTable;
    @FXML private TableColumn<Duck, Long> idColumn;
    @FXML private TableColumn<Duck, String> usernameColumn;
    @FXML private TableColumn<Duck, String> emailColumn;
    @FXML private TableColumn<Duck, String> typeColumn;
    @FXML private TableColumn<Duck, Double> speedColumn;
    @FXML private TableColumn<Duck, Double> resistanceColumn;
    @FXML private Button prevButton;
    @FXML private Button nextButton;
    @FXML private Label pageLabel;

    private DuckRepository duckRepository;
    private int currentPage = 1;
    private int pageSize = 6;
    private int totalDucks = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        duckRepository = new DuckRepository();
        setupTableColumns();
        loadDucks();
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        usernameColumn.setCellValueFactory(cellData -> cellData.getValue().usernameProperty());
        emailColumn.setCellValueFactory(cellData -> cellData.getValue().emailProperty());
        typeColumn.setCellValueFactory(cellData -> cellData.getValue().tipProperty().asString());
        speedColumn.setCellValueFactory(cellData -> cellData.getValue().vitezaProperty().asObject());
        resistanceColumn.setCellValueFactory(cellData -> cellData.getValue().rezistentaProperty().asObject());
    }

    private void loadDucks() {
        try {
            // Get total count for pagination
            totalDucks = duckRepository.getTotalCount();

            // Get paginated ducks
            int offset = (currentPage - 1) * pageSize;
            List<Duck> ducks = duckRepository.findAllPaginated(offset, pageSize);

            ObservableList<Duck> duckList = FXCollections.observableArrayList(ducks);
            duckTable.setItems(duckList);

            // Update page label
            pageLabel.setText("Page " + currentPage + " (" + totalDucks + " total ducks)");

            // Enable/disable buttons
            prevButton.setDisable(currentPage <= 1);
            nextButton.setDisable((currentPage * pageSize) >= totalDucks);

        } catch (SQLException e) {
            showAlert("Database Error", "Failed to load ducks: " + e.getMessage());
        }
    }

    @FXML
    private void handlePrevious() {
        if (currentPage > 1) {
            currentPage--;
            loadDucks();
        }
    }

    @FXML
    private void handleNext() {
        if ((currentPage * pageSize) < totalDucks) {
            currentPage++;
            loadDucks();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}