package ch.bfh.game2048.view;

import java.util.Optional;

import ch.bfh.game2048.persistence.Config;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;

public class VictoryAlert {

	String title;
	String text;

	public VictoryAlert(String title, String text) {
		super();
		this.title = title;
		this.text = text;
	}

	public boolean show() {

		Config conf = Config.getInstance();
		
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle(title);
		alert.setHeaderText(text);

		ButtonType buttonTypeOne = new ButtonType(conf.getPropertyAsString("yes.button"));
		ButtonType buttonTypeTwo = new ButtonType(conf.getPropertyAsString("no.button"));

		alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo);

		Optional<ButtonType> result = alert.showAndWait();

		return result.get() == buttonTypeOne ? true : false;
	}

}
