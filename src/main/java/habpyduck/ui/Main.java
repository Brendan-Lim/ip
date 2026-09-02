package habpyduck.ui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Displays the main JavaFX window for HabpyDuck.
 */
public class Main extends Application {
    private static final int WINDOW_WIDTH = 400;
    private static final int WINDOW_HEIGHT = 600;

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
        VBox dialogContainer = new VBox();
        dialogContainer.setPadding(new Insets(10));
        dialogContainer.setSpacing(8);
        dialogContainer.getChildren().add(DialogBox.getHabpyDuckDialog(
                "Hi friend! I'm HabpyDuck.\nWhat can I do for you today?"));

        ScrollPane scrollPane = new ScrollPane(dialogContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        TextField userInput = new TextField();
        userInput.setPromptText("Type your command here");
        HBox.setHgrow(userInput, Priority.ALWAYS);

        Button sendButton = new Button("Send");
        sendButton.setDefaultButton(true);
        sendButton.setOnAction(event -> {
            String input = userInput.getText().trim();
            if (!input.isEmpty()) {
                dialogContainer.getChildren().add(DialogBox.getUserDialog(input));
                dialogContainer.getChildren().add(DialogBox.getHabpyDuckDialog(
                        "I'll learn how to respond to commands in Part 3."));
                userInput.clear();
            }
        });

        HBox inputArea = new HBox(userInput, sendButton);
        inputArea.setPadding(new Insets(10));
        inputArea.setSpacing(8);

        VBox root = new VBox(scrollPane, inputArea);
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        stage.setTitle("HabpyDuck");
        stage.setScene(scene);
        stage.show();
    }
}
