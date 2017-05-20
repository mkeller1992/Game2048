package ch.bfh.game2048.view;

import java.text.MessageFormat;

import ch.bfh.game2048.persistence.Config;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 * 
 * Game-over screen which pops up when game is lost
 * 
 * - Displays the score reached by end of the game - Asks the player to enter
 * his name for the highscore-list
 *
 */

public class GameOverDialog extends Dialog<String> implements InvalidationListener {

	GridPane grid;
	TextField nameField;
	Image image;
	ImageView imageView;
	Node okButton;
	Config conf;

	/**
	 * 
	 * @param title
	 *            : text to be displayed in the title-bar
	 * @param finalScore
	 *            : final score of game that just ended
	 */

	public GameOverDialog(String title, int finalScore) {

		conf = Config.getInstance();

		// Set text in title-bar
		this.setTitle(title);

		// Set main-text of dialog
		this.setHeaderText(
				MessageFormat.format(conf.getPropertyAsString("gameOverText1.dialog"), new Object[] { finalScore }));

		// Set icons
		Stage stage = (Stage) this.getDialogPane().getScene().getWindow();
		stage.getIcons().add(new Image(getClass().getResourceAsStream("../meteor.png")));

		// Set image
		image = new Image(getClass().getResource("images/LosingSmiley.png").toExternalForm());
		imageView = new ImageView(image);
		this.setGraphic(imageView);

		// Set the button types.
		ButtonType loginButtonType = new ButtonType(conf.getPropertyAsString("ok.button"), ButtonData.OK_DONE);
		this.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

		// Create the pane, the labels and the fields.
		grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 50, 10, 10));

		nameField = new TextField();
		nameField.setPromptText(conf.getPropertyAsString("promptTextName.dialog"));

		grid.add(new Label(conf.getPropertyAsString("gameOverText2.dialog")), 0, 0);
		grid.add(nameField, 1, 0);

		// Enable/Disable OK button depending on whether a Channel Name / URL
		// was entered.
		okButton = this.getDialogPane().lookupButton(loginButtonType);
		okButton.setDisable(true);

		// Add Listener which checks if input-text is valid
		nameField.textProperty().addListener(this);

		// Add grid-content to the main-pane
		this.getDialogPane().setContent(grid);
	}

	/**
	 * get the player-name entered by the user
	 * 
	 */
	public String getPlayerName() {
		return nameField.getText();
	}

	/**
	 * - Check if input is valid
	 * - Enable okay-Button when input valid
	 */
	@Override
	public void invalidated(Observable arg0) {

		if (!nameField.getText().equals("")) {

			okButton.setDisable(false);
		} else {
			okButton.setDisable(true);
		}
	}
}