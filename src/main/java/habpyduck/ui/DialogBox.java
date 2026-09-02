package habpyduck.ui;

import java.io.IOException;
import java.util.Objects;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

/**
 * Shows one chat message together with a small sender avatar.
 */
public class DialogBox extends HBox {
    private static final int AVATAR_SIZE = 40;
    private static final int MESSAGE_MAX_WIDTH = 260;
    private static final String DIALOG_BOX_FXML_PATH = "/view/DialogBox.fxml";
    private static final String HABPY_DUCK_IMAGE_PATH = "/images/habpyduck.png";
    private static final String USER_IMAGE_PATH = "/images/user.png";

    @FXML
    private Label message;

    @FXML
    private ImageView avatar;

    /**
     * Creates a dialog box with a text message and an avatar image.
     *
     * @param text the message to show.
     * @param imagePath the classpath location of the avatar image.
     */
    public DialogBox(String text, String imagePath) {
        loadDialogBoxView();

        Image image = new Image(Objects.requireNonNull(DialogBox.class.getResourceAsStream(imagePath)));
        message.setWrapText(true);
        message.setMaxWidth(MESSAGE_MAX_WIDTH);
        avatar.setImage(image);
        avatar.setFitHeight(AVATAR_SIZE);
        avatar.setFitWidth(AVATAR_SIZE);
        avatar.setPreserveRatio(true);
        message.setText(text);
        setAlignment(Pos.TOP_LEFT);
    }

    /**
     * Returns a dialog box that represents the user's message.
     *
     * @param text the message to show.
     * @return a dialog box aligned to the right.
     */
    public static DialogBox getUserDialog(String text) {
        DialogBox dialogBox = new DialogBox(text, USER_IMAGE_PATH);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        dialogBox.getChildren().setAll(spacer, dialogBox.message, dialogBox.avatar);
        dialogBox.setAlignment(Pos.TOP_RIGHT);
        return dialogBox;
    }

    /**
     * Returns a dialog box that represents HabpyDuck's message.
     *
     * @param text the message to show.
     * @return a dialog box aligned to the left.
     */
    public static DialogBox getHabpyDuckDialog(String text) {
        DialogBox dialogBox = new DialogBox(text, HABPY_DUCK_IMAGE_PATH);
        dialogBox.message.getStyleClass().add("reply-label");
        return dialogBox;
    }

    /**
     * Loads the FXML structure used by this dialog box.
     */
    private void loadDialogBoxView() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(DialogBox.class.getResource(DIALOG_BOX_FXML_PATH));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Could not load dialog box FXML.", e);
        }
    }
}
