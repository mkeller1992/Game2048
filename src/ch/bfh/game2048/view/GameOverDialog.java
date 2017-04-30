package ch.bfh.game2048.view;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class GameOverDialog extends Dialog<String> implements InvalidationListener {

	TextField nameField;
	Node okButton;

	public GameOverDialog(String title, int finalScore) {
		this.setTitle(title);
		this.setHeaderText("The Game is over. Your reached " + finalScore+" Pts.");

		// Set the button types.
		ButtonType loginButtonType = new ButtonType("OK", ButtonData.OK_DONE);
		this.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

		// Create the pane, the labels and the fields.
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 50, 10, 10));

		nameField = new TextField();
		nameField.setPromptText("Max Mustermann");

		grid.add(new Label("Please enter your nickname: "), 0, 0);
		grid.add(nameField, 1, 0);

		// Enable/Disable OK button depending on whether a Channel Name / URL
		// was entered.
		okButton = this.getDialogPane().lookupButton(loginButtonType);
		okButton.setDisable(true);

		// Do some validation
		nameField.textProperty().addListener(this);
		
		// Save user-input as String values
		// Convert the result to a channel-name/url-pair when the OK button is
		// clicked.
		this.setResultConverter(dialogButton -> {
			if (dialogButton == loginButtonType) {
				return nameField.getText();
			}
			return null;
		});

		this.getDialogPane().setContent(grid);
	}

	public String getPlayerName() {
		return nameField.getText();
	}

	@Override
	public void invalidated(Observable arg0) {

		if (!nameField.getText().equals("")) {

			okButton.setDisable(false);
		} else {
			okButton.setDisable(true);
		}
	}
}