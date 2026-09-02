package habpyduck.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * Displays the main JavaFX window for HabpyDuck.
 */
public class Main extends Application {
    /**
     * Launches the JavaFX application.
     *
     * @param args Command line arguments supplied by the Java launcher.
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Creates the first JavaFX scene shown to the user.
     *
     * @param stage the main application window.
     */
    @Override
    public void start(Stage stage) {
        Label greeting = new Label("Hello from HabpyDuck!");
        Scene scene = new Scene(greeting, 400, 200);
        stage.setTitle("HabpyDuck");
        stage.setScene(scene);
        stage.show();
    }
}
