package ch.bfh.game2048.view;

import java.util.List;

import ch.bfh.game2048.engine.GameEngine;
import ch.bfh.game2048.model.Direction;
import ch.bfh.game2048.model.GameStatistics;
import ch.bfh.game2048.model.Player;
import ch.bfh.game2048.model.Tile;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;

public class MainUIController {

	@FXML
	private GridPane gameBoard;

	@FXML
	private Button startButton;

	@FXML
	private Label labelScoreName;

	@FXML
	private Label labelScoreNumber;

	@FXML
	private List<List<Label>> labelList;

	GameEngine game;

	@FXML
	public void initialize() {
		game = new GameEngine(4, new GameStatistics(new Player()));
		fromIntToLabel(game.getBoard());
		installEventHandler(startButton);		
	}

	@FXML
	void startGame(ActionEvent event) {		
		game = new GameEngine(4, new GameStatistics(new Player()));
		fromIntToLabel(game.getBoard());
	}

	private void installEventHandler(final Node keyNode) {
		// handler for enter key press / release events, other keys are
		// handled by the parent (keyboard) node handler
		final EventHandler<KeyEvent> keyEventHandler = new EventHandler<KeyEvent>() {
			public void handle(final KeyEvent keyEvent) {

				System.out.println(keyEvent.getCode());
				boolean moved = false;
				if (keyEvent.getCode() == KeyCode.UP) {
					moved = game.move(Direction.UP);
				}
				if (keyEvent.getCode() == KeyCode.DOWN) {
					moved = game.move(Direction.DOWN);
				}
				if (keyEvent.getCode() == KeyCode.LEFT) {
					moved = game.move(Direction.LEFT);
				}
				if (keyEvent.getCode() == KeyCode.RIGHT) {
					moved = game.move(Direction.RIGHT);
				}

				if(moved){
					fromIntToLabel(game.getBoard());
					labelScoreNumber.setText("" + game.getStats().getScore() + " Pts");
				}
				keyEvent.consume();
			}

		};

		keyNode.setOnKeyPressed(keyEventHandler);
	}

	private void fromIntToLabel(Tile[][] tileArray) {

		int i=0; int j=0;
		for(List<Label> row : labelList){
			for(Label label : row){				
				label.setText(""+tileArray[i][j].getValue());
				setStyle(label);
				
				if (tileArray[i][j].isMerged()) {
//					fadeIn(label, 300, 0.5, 1.0, 3);						
				} else if (tileArray[i][j].isSpawned()) {
					fadeIn(label, 1000, 0.0, 1.0, 1);
				}
				
				j++;
			}
			j = 0;
			i++;
		}			
	}

	private void fadeIn(Label label, int durationMillis, double from, double to, int nbOfcycles) {

		FadeTransition fadeTransition = new FadeTransition(Duration.millis(durationMillis), label);
		fadeTransition.setFromValue(from);
		fadeTransition.setToValue(to);
		fadeTransition.setCycleCount(nbOfcycles);
		fadeTransition.play();

	}

	private void setStyle(Label label) {
	

		String fontColor = "";
		String backgroundColor = "";

		switch (label.getText()) {
		case "0":
			label.setText("");
			fontColor = "255, 255, 255";
			backgroundColor = "204,192,180";
		case "2":
			fontColor = "119,110,101";
			backgroundColor = "238,228,218";
			break;
		case "4":
			fontColor = "119,110,101";
			backgroundColor = "237,224,200";
			break;
		case "8":
			fontColor = "249,246,242";
			backgroundColor = "242,177,121";
			break;
		case "16":
			fontColor = "249,246,242";
			backgroundColor = "245,149,99";
			break;
		case "32":
			fontColor = "249,246,242";
			backgroundColor = "246,124,95";
			break;
		case "64":
			fontColor = "249,246,242";
			backgroundColor = "246,94,59";
			break;
		case "128":
			fontColor = "249,246,242";
			backgroundColor = "237,207,114";
			break;
		case "256":
			fontColor = "249,246,242";
			backgroundColor = "237,204,97";
			break;
		case "512":
			fontColor = "249,246,242";
			backgroundColor = "237,200,80";
			break;
		case "1024":
			fontColor = "249,246,242";
			backgroundColor = "237,197,63";
			break;
		case "2048":
			fontColor = "249,246,242";
			backgroundColor = "237,197,63";
			break;
		default:
			fontColor = "249,246,242";
			backgroundColor = "237,197,63";
			break;
		}

		label.setStyle("-fx-font-size: 24pt ;-fx-font-weight: bold; -fx-text-fill: rgb(" + fontColor
				+ ") ; -fx-border-color: rgb(187, 173, 160); -fx-border-width: 5; -fx-background-color: rgb("
				+ backgroundColor + ");");
	}
}