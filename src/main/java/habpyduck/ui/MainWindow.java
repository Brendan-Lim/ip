package habpyduck.ui;

import habpyduck.HabpyDuck;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

/**
 * Controls the main chat window.
 */
public class MainWindow {
    private static final String WELCOME_MESSAGE = "Hi friend! I'm HabpyDuck.\nWhat can I do for you today?";

    private HabpyDuck habpyDuck;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox dialogContainer;

    @FXML
    private TextField userInput;

    /**
     * Initializes JavaFX bindings and the first greeting message.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
        dialogContainer.getChildren().add(DialogBox.getHabpyDuckDialog(WELCOME_MESSAGE));
    }

    /**
     * Sets the chatbot used to answer user commands.
     *
     * @param habpyDuck the chatbot used by this window.
     */
    public void setHabpyDuck(HabpyDuck habpyDuck) {
        this.habpyDuck = habpyDuck;
    }

    /**
     * Handles one command entered through the JavaFX input field.
     */
    @FXML
    public void handleUserInput() {
        String input = userInput.getText().trim();
        if (input.isEmpty()) {
            return;
        }

        dialogContainer.getChildren().add(DialogBox.getUserDialog(input));
        dialogContainer.getChildren().add(DialogBox.getHabpyDuckDialog(habpyDuck.getResponse(input)));
        userInput.clear();
    }
}
