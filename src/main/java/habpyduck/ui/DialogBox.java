package habpyduck.ui;

import java.util.Objects;

import javafx.geometry.Insets;
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
    private static final String HABPY_DUCK_IMAGE_PATH = "/images/habpyduck.png";
    private static final String USER_IMAGE_PATH = "/images/user.png";

    private final Label message;
    private final ImageView avatar;

    /**
     * Creates a dialog box with a text message and an avatar image.
     *
     * @param text the message to show.
     * @param imagePath the classpath location of the avatar image.
     */
    public DialogBox(String text, String imagePath) {
        message = new Label(text);
        message.setWrapText(true);
        message.setMaxWidth(MESSAGE_MAX_WIDTH);
        message.setPadding(new Insets(8));
        message.setStyle("-fx-background-color: white; -fx-border-color: lightgray;");

        avatar = createAvatar(imagePath);
        setAlignment(Pos.TOP_LEFT);
        setSpacing(8);
        getChildren().addAll(avatar, message);
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
        return new DialogBox(text, HABPY_DUCK_IMAGE_PATH);
    }

    /**
     * Creates an avatar from an image resource.
     *
     * @param imagePath the classpath location of the avatar image.
     * @return the avatar image view.
     */
    private static ImageView createAvatar(String imagePath) {
        Image image = new Image(Objects.requireNonNull(DialogBox.class.getResourceAsStream(imagePath)));
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(AVATAR_SIZE);
        imageView.setFitWidth(AVATAR_SIZE);
        imageView.setPreserveRatio(true);
        return imageView;
    }
}
