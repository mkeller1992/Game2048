package ch.bfh.game2048.view;

import java.io.FileNotFoundException;
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
import ch.bfh.game2048.persistence.Config;
import ch.bfh.game2048.persistence.ScoreHandler;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
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

	private Label[][] labelList;

	GameEngine game;

	private ScoreHandler scoreHandler;
	private Timer timer;
	private Highscore highscoreList;
	private Config conf;
	
	private int sizeOfBoard = 4;
	private int boardWidth = 400;
	private int boardHeight = 400;


	@FXML
	public void initialize() throws FileNotFoundException, JAXBException {

		setBoardMeasures(3,400,400);
		initializeBoard();

		conf = Config.getInstance();
		scoreHandler = new ScoreHandler();
		highscoreList = scoreHandler.readScores(conf.getPropertyAsString("highscoreFileName"));
		game = new GameEngine(sizeOfBoard);
		fromIntToLabel(game.getBoard());
		installEventHandler(startButton);

	}
	
	public void setBoardMeasures(int numbOfTilesPerRow, int boardWidth, int boardHeight){
		this.sizeOfBoard = numbOfTilesPerRow;
		this.boardWidth = boardWidth;
		this.boardHeight = boardHeight;
	}

	private void initializeBoard() {

		gameBoard.setPrefSize(boardWidth, boardHeight);
		gameBoard.setMinSize(boardWidth, boardHeight);
		gameBoard.setMaxSize(boardWidth, boardHeight);

		labelList = new Label[sizeOfBoard][sizeOfBoard];

		for (int i = 0; i < sizeOfBoard; i++) {
			for (int j = 0; j < sizeOfBoard; j++) {

				Label label = new Label();
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

		if(timer!=null){
		timer.stop();
		}
		GameStatistics stats = new GameStatistics(new Player());
		game = new GameEngine(sizeOfBoard, stats);
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
		for (Label[] row : labelList) {
			for (Label label : row) {
				// label.setText("" + tileArray[i][j].getValue());

				setStyleOfTile(label, tileArray[i][j].getValue());

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

	private void setStyleOfTile(Label label, int tileValue) {

		if (tileValue == 0) {
			label.setGraphic(new Text(""));
		} else {
			Text tileText = new Text("" + tileValue);
			label.setGraphic(tileText);
			scaleText(tileText, tileValue, label);
			tileText.setFill(UITheme.valueOf(tileValue).getFontColor());
		}

		label.setStyle(
				"-fx-font-weight: bold; -fx-border-color: rgb(187, 173, 160); -fx-border-width: 5; -fx-background-color: rgb("
						+ UITheme.valueOf(tileValue).getBackgroundcolor() + ");");
	}

	private void scaleText(Text text, int tileValue, Label label) {

		Bounds boundsOfText = text.getBoundsInLocal();
		Bounds boundsOfLabel = label.getBoundsInLocal();

		double multiplicator = 0;

		if (10000 < tileValue) {
			multiplicator = 0.9;
		} else if (1000 < tileValue) {
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