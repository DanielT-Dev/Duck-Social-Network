import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class FXMLApp extends Application {

    @Override
    public void start(Stage stage) {
        try {
            System.out.println("Looking for FXML file...");

            URL url1 = getClass().getResource("main_view.fxml");
            URL url2 = getClass().getResource("/main_view.fxml");
            URL url3 = getClass().getResource("view/main_view.fxml");
            URL url4 = getClass().getResource("/view/main_view.fxml");

            System.out.println("Path 1: " + url1);
            System.out.println("Path 2: " + url2);
            System.out.println("Path 3: " + url3);
            System.out.println("Path 4: " + url4);

            URL fxmlUrl = null;
            if (url1 != null) fxmlUrl = url1;
            else if (url2 != null) fxmlUrl = url2;
            else if (url3 != null) fxmlUrl = url3;
            else if (url4 != null) fxmlUrl = url4;

            if (fxmlUrl == null) {
                System.err.println("FXML file not found!");
                System.err.println("Current directory: " + System.getProperty("user.dir"));
                return;
            }

            System.out.println("Loading FXML from: " + fxmlUrl);
            Parent root = FXMLLoader.load(fxmlUrl);

            stage.setTitle("Social Network Manager");
            stage.setScene(new Scene(root, 1000, 600));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}