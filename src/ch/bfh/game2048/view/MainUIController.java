package ch.bfh.game2048.view;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.xml.bind.JAXBException;

import org.apache.commons.lang3.time.DurationFormatUtils;

import ch.bfh.game2048.engine.GameEngine;
import ch.bfh.game2048.model.Direction;
import ch.bfh.game2048.model.GameStatistics;
import ch.bfh.game2048.model.Highscore;
import ch.bfh.game2048.model.Player;
import ch.bfh.game2048.model.Tile;
import ch.bfh.game2048.persistence.ScoreHandler;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
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

public class MainUIController implements Observer {

	@FXML
	private GridPane gameBoard;

	@FXML
	private Button startButton;

	@FXML
	private Label labelScoreName;

	@FXML
	private Label labelScoreNumber;

	@FXML
	private Label labelTimerTime;

	@FXML
	private List<List<Label>> labelList;

	GameEngine game;

	ScoreHandler scoreHandler;
	Timer timer;
	Highscore highscoreList;

	// Needs to be moved to general Properties later:
	final static String HIGHSCORE_FILE = "highscores.xml";

	@FXML
	public void initialize() throws FileNotFoundException, JAXBException {

		scoreHandler = new ScoreHandler();
		highscoreList = scoreHandler.readScores();

		installEventHandler(startButton);

	}

	@FXML
	void startGame(ActionEvent event) {

		GameStatistics stats = new GameStatistics(new Player());
		game = new GameEngine(4, stats);
		stats.addObserver(this);
		timer = new Timer();
		timer.addObserver(this);
		fromIntToLabel(game.getBoard());
		labelScoreNumber.setText("0");

	}

	@FXML
	void showHighScore(ActionEvent event) {
		showHighscoreList();
	}

	public void setHighscoreList(Highscore highscoreList) {
		this.highscoreList = highscoreList;
	}

	private void showHighscoreList() {
		HighScoreDialog highScore = new HighScoreDialog("Highscore", highscoreList.getHighscore());
		highScore.show();
	}

	private void installEventHandler(final Node keyNode) {
		// handler for enter key press / release events, other keys are
		// handled by the parent (keyboard) node handler
		final EventHandler<KeyEvent> keyEventHandler = new EventHandler<KeyEvent>() {
			public void handle(final KeyEvent keyEvent) {

				if (game != null && !game.getStats().isGameOver()) {
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

					if (moved) {
						fromIntToLabel(game.getBoard());
						labelScoreNumber.setText("" + game.getStats().getFormattedScore() + " Pts");
					}
				}
				keyEvent.consume();
			}

		};

		keyNode.setOnKeyPressed(keyEventHandler);
	}

	private void fromIntToLabel(Tile[][] tileArray) {

		int i = 0;
		int j = 0;
		for (List<Label> row : labelList) {
			for (Label label : row) {
				label.setText("" + tileArray[i][j].getValue());
				setStyle(label);

				if (tileArray[i][j].isMerged()) {
					// fadeIn(label, 300, 0.5, 1.0, 3);
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
		int value = Integer.parseInt(label.getText());

		if (value == 0)
			label.setText("");

		label.setStyle("-fx-font-size: 24pt ;-fx-font-weight: bold; -fx-text-fill: rgb("
				+ UITheme.valueOf(value).getFontColor()
				+ ") ; -fx-border-color: rgb(187, 173, 160); -fx-border-width: 5; -fx-background-color: rgb("
				+ UITheme.valueOf(value).getBackgroundcolor() + ");");
	}

	public void update(Observable o, Object arg) {

		if (o instanceof Timer) {

			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					long millis = ((Timer) o).getMillisElapsed();
					labelTimerTime.setText(DurationFormatUtils.formatDuration(millis, "HH:mm:ss"));
				}
			});
		}

		else if (o instanceof GameStatistics) {

			Platform.runLater(new Runnable() {
				@Override
				public void run() {

					GameStatistics stats = (GameStatistics) o;

					if (stats.isGameOver()) {

						timer.stop();
						GameOverDialog dialog = new GameOverDialog("Game Over", stats.getScore());
						if (dialog.showAndWait().isPresent()) {
							stats.getPlayer().setNickName(dialog.getPlayerName());
							highscoreList.getHighscore().add(stats);
							showHighscoreList();
							try {
								scoreHandler.writeScores(highscoreList);
							} catch ( JAXBException e) {
								e.printStackTrace();
							}
						}
					}
				}
			});
		}
	}

}