package ch.bfh.game2048.view;

import java.util.Optional;

import ch.bfh.game2048.persistence.Config;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

/**
 * 
 * Victory screen which pops up when game is won
 * 
 * - Informs the player that he reached the game-winning tile
 * - Asks the player if he wants to continue playing
 *
 */

public class VictoryAlert {
	Alert alert;
	Image image;
	ImageView imageView;

	ButtonType yesButton;
	ButtonType noButton;

	Config conf;
	String title;
	String text;

	public VictoryAlert(String title, String text) {
		super();
		this.title = title;
		this.text = text;
	}

	public boolean show() {

		conf = Config.getInstance();

		// initialize alert
		alert = new Alert(AlertType.CONFIRMATION);

		// set alert-title
		alert.setTitle(title);

		// set main-text of alert
		alert.setHeaderText(text);

		// set title-bar icon
		Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
		stage.getIcons().add(new Image(getClass().getResourceAsStream("images/meteor.png")));

		// create and add image
		image = new Image(getClass().getResource("images/WinningSmiley.png").toExternalForm());
		imageView = new ImageView(image);
		alert.setGraphic(imageView);

		// create and add buttons
		ButtonType yesButton = new ButtonType(conf.getPropertyAsString("yes.button"));
		ButtonType noButton = new ButtonType(conf.getPropertyAsString("no.button"));

		alert.getButtonTypes().setAll(yesButton, noButton);

		Optional<ButtonType> result = alert.showAndWait();

		/*
		 * Return true if player clicked the "Yes"-button, which means he wants
		 * to continue playing
		 */
		return result.get() == yesButton ? true : false;
	}

}
