package ch.bfh.game2048.view;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.xml.bind.JAXBException;

import org.apache.commons.lang3.time.DurationFormatUtils;

import ch.bfh.game2048.Main;
import ch.bfh.game2048.engine.GameEngine;
import ch.bfh.game2048.model.Direction;
import ch.bfh.game2048.model.GameStatistics;
import ch.bfh.game2048.model.Highscore;
import ch.bfh.game2048.model.Player;
import ch.bfh.game2048.model.Tile;
import ch.bfh.game2048.persistence.Config;
import ch.bfh.game2048.persistence.ScoreHandler;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
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
	Config conf;

	Stage stage;

	// Needs to be moved to general Properties later:
	// final static String HIGHSCORE_FILE = "highscores.xml";
	// final static String VICTORY_ALERT_TITLE = "Victory :)";
	// final static String VICTORY_ALERT_TEXT = "Congratulations you won!\nWould
	// you like to continue?";
	// final static int WINNING_NUMBER = 2048;

	@FXML
	private void handleKeyPressed(KeyEvent ke) {
		System.out.println("Was here");
		System.out.println("Key Pressed: " + ke.getCode());
	}

	@FXML
	public void initialize() throws FileNotFoundException, JAXBException {

		conf = Config.getInstance();
		scoreHandler = new ScoreHandler();
		highscoreList = scoreHandler.readScores(conf.getPropertyAsString("highscoreFileName"));
		game = new GameEngine(4);
		fromIntToLabel(game.getBoard());
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

		// activateKeyListener();

	}

	public void activateKeyListener() {

		Main.getStage().getScene().setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				System.out.println(event.getCode());

				if (event.getCode() == KeyCode.UP) {
					System.out.println("was here");
					game.move(Direction.UP);
				}
			}
		});
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

				if (game.getStats() != null && !game.getStats().isGameOver()) {
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
				// label.setText("" + tileArray[i][j].getValue());

				setStyle(label, tileArray[i][j].getValue());

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

	private void setStyle(Label label, int tileValue) {
		
		if (tileValue == 0) {
			label.setGraphic(new Text(""));
		} else {
			Text tileText = new Text("" + tileValue);
			label.setGraphic(tileText);
			scaleText(tileText, tileValue, label);
			tileText.setFill(UITheme.valueOf(tileValue).getFontColor());
		}

		label.setStyle("-fx-font-weight: bold; -fx-border-color: rgb(187, 173, 160); -fx-border-width: 5; -fx-background-color: rgb("
				+ UITheme.valueOf(tileValue).getBackgroundcolor() + ");");
	}

	private void scaleText(Text text, int tileValue, Label label) {

		Bounds boundsOfText = text.getBoundsInLocal();
		Bounds boundsOfLabel = label.getBoundsInLocal();

		double multiplicator = 0;

		if (10000 < tileValue) {
			multiplicator = 0.9;
		}
		if (1000 < tileValue) {
			multiplicator = 0.8;
		} else if (100 < tileValue) {
			multiplicator = 0.7;
		} else {
			multiplicator = 0.6;
		}

		double scaleX = multiplicator * (boundsOfLabel.getWidth()) / boundsOfText.getWidth();
		double scaleY = multiplicator * (boundsOfLabel.getHeight()) / boundsOfText.getHeight();

		double finalScale = Math.min(scaleX, scaleY);

		text.setScaleX(finalScale);
		text.setScaleY(finalScale);

	}

	public void update(Observable o, Object arg) {

		if (o instanceof Timer) {

			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					long millis = ((Timer) o).getMillisElapsed();
					labelTimerTime.setText(
							DurationFormatUtils.formatDuration(millis, conf.getPropertyAsString("timerTimeFormat")));
				}
			});
		}

		else if (o instanceof GameStatistics) {

			Platform.runLater(new Runnable() {
				@Override
				public void run() {

					GameStatistics stats = (GameStatistics) o;

					if (stats.isGameOver()) {
						processGameOver(stats);
					}

					else if (stats.isGameContinue() == false
							&& stats.getHighestValue() == conf.getPropertyAsInt("winningNumber")) {

						VictoryAlert dialog = new VictoryAlert(conf.getPropertyAsString("victoryTitle.alert"),
								conf.getPropertyAsString("victoryText.alert"));
						boolean continuation = dialog.show();
						if (continuation) {
							stats.setGameContinue(true);
						} else {
							stats.setGameOver(true);
						}
					}
				}
			});
		}
	}

	private void processGameOver(GameStatistics stats) {

		timer.stop();
		GameOverDialog dialog = new GameOverDialog(conf.getPropertyAsString("gameOverDialog.title"), stats.getScore());
		if (dialog.showAndWait().isPresent()) {
			stats.getPlayer().setNickName(dialog.getPlayerName());
			highscoreList.getHighscore().add(stats);
			showHighscoreList();
			try {
				scoreHandler.writeScores(highscoreList, conf.getPropertyAsString("highscoreFileName"));
			} catch (JAXBException | FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

}