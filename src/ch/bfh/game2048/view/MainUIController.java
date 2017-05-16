package ch.bfh.game2048.view;

import java.io.FileNotFoundException;
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
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MainUIController implements Observer {

	@FXML
	private BorderPane rootPane;

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

	private SuperLabel[][] labelList;

	GameEngine game;

	private ScoreHandler scoreHandler;
	private Timer timer;
	private Highscore highscoreList;
	private Config conf;

	private int sizeOfBoard = 4;
	private int boardWidth = 400;
	private int boardHeight = 400;

	private boolean isRunning = false;

	@FXML
	public void initialize() throws FileNotFoundException, JAXBException {

		setBoardMeasures(4, 400, 400);
		initializeBoard();

		conf = Config.getInstance();
		scoreHandler = new ScoreHandler();
		highscoreList = scoreHandler.readScores(conf.getPropertyAsString("highscoreFileName"));
		game = new GameEngine(sizeOfBoard);
		fromIntToLabel(game.getBoard());
		installEventHandler(startButton);

	}

	public void setBoardMeasures(int numbOfTilesPerRow, int boardWidth, int boardHeight) {
		this.sizeOfBoard = numbOfTilesPerRow;
		this.boardWidth = boardWidth;
		this.boardHeight = boardHeight;
	}

	private void initializeBoard() {

		gameBoard.setPrefSize(boardWidth, boardHeight);
		gameBoard.setMinSize(boardWidth, boardHeight);
		gameBoard.setMaxSize(boardWidth, boardHeight);

		labelList = new SuperLabel[sizeOfBoard][sizeOfBoard];

		for (int i = 0; i < sizeOfBoard; i++) {
			for (int j = 0; j < sizeOfBoard; j++) {

				SuperLabel label = new SuperLabel(0);
				label.setPrefSize((boardWidth * 1.0) / sizeOfBoard, (boardHeight * 1.0) / sizeOfBoard);
				label.setAlignment(Pos.CENTER);
				GridPane.setConstraints(label, j, i);
				gameBoard.getChildren().add(label);

				labelList[i][j] = label;
			}
		}
	}

	@FXML
	void startGame(ActionEvent event) {

		// If a game is currently ongoing or paused --> Switch between pause and resume
		if (game.getStats() != null && game.getStats().isGameOver() == false) {
			if (isRunning) {
				timer.stop();
				game.getStats().pauseTime();
				startButton.setText(conf.getPropertyAsString("resume.button"));
				isRunning = false;
			} else {
				timer.start();
				game.getStats().resumeTime();
				startButton.setText(conf.getPropertyAsString("pause.button"));
				isRunning = true;
			}
		}

		// If no game is ongoing --> Initialize new game:
		if (game.getStats() == null || game.getStats().isGameOver()) {

			GameStatistics stats = new GameStatistics(new Player());
			game = new GameEngine(sizeOfBoard, stats);
			stats.addObserver(this);
			timer = new Timer();
			timer.addObserver(this);
			fromIntToLabel(game.getBoard());
			labelScoreNumber.setText("0");
			startButton.setText(conf.getPropertyAsString("pause.button"));
			isRunning = true;
		}
	}

	@FXML
	void showHighScore(ActionEvent event) {
		
		try {
			Main.switchScene(Scenes.HIGHSCORE);
		} catch (FileNotFoundException | JAXBException e) {
			e.printStackTrace();
		}
		
		
//		showHighscoreList();
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

				if (isRunning) {
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
		for (SuperLabel[] row : labelList) {
			for (SuperLabel label : row) {

				label.setTileNumber(tileArray[i][j].getValue());

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
		isRunning = false;
		startButton.setText(conf.getPropertyAsString("restart.button"));
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