package ch.bfh.game2048.view;

import java.io.FileNotFoundException;
import java.lang.reflect.Constructor;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

import javax.xml.bind.JAXBException;

import org.apache.commons.lang3.time.DurationFormatUtils;

import ch.bfh.game2048.ai.AIGameEngine;
import ch.bfh.game2048.ai.strategies.BaseAIStrategy;
import ch.bfh.game2048.ai.strategies.Strategy;
import ch.bfh.game2048.engine.GameEngine;
import ch.bfh.game2048.model.Direction;
import ch.bfh.game2048.model.GameStatistics;
import ch.bfh.game2048.model.Highscore;
import ch.bfh.game2048.model.Tile;
import ch.bfh.game2048.persistence.Config;
import ch.bfh.game2048.persistence.ScoreHandler;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;
import javafx.util.Pair;

public class GamePaneController implements Observer {

	@FXML
	private BorderPane rootPane;

	@FXML
	private GridPane gameBoard;

	@FXML
	protected Button startButton;

	@FXML
	protected Button pauseResumeButton;

	@FXML
	protected Button btnHint;

	@FXML
	private Label labelScoreName;

	@FXML
	protected Label labelScoreNumber; // where the current score gets displayed

	@FXML
	protected Label labelTimerName;

	@FXML
	protected Label labelTimerTime; // where the time of the stop-watch gets
									// displayed

	private SuperLabel[][] labelList;

	GameEngine game;

	protected Config conf;

	protected int numbOfBoardColumns = 4;

	protected BaseAIStrategy aiStrategy;
	protected Strategy strategyEnum;

	protected boolean isActive = false; // pause / no-pause
	protected boolean isRunning = false; // game-running / (game-over/not yet
											// started)
	protected boolean ignoreWinMessage = false;

	protected Timeline timer;

	/**
	 * 
	 * Initialization upon opening of the application:
	 * 
	 * Initializes and stores the following properties: - conf: For accessing
	 * the data in the property-file - scoreHandler: Marshaller / Unmarshaller
	 * for database - game: Game-Engine - timer: Stopwatch on main-screen
	 * 
	 * > Sets up gameBoard on main-screen with empty tiles > Registers
	 * Key-Handler (for triggering tile-moves upon keystrokes)
	 * 
	 * 
	 */
	@FXML
	public void initialize() throws FileNotFoundException, JAXBException {
		// set properties
		conf = Config.getInstance();

		numbOfBoardColumns = conf.getPropertyAsInt("boardSize");

		// prepare gui
		initializeBoard();
		pauseResumeButton.setVisible(false);

		timer = new Timeline(new KeyFrame(Duration.millis(50), ae -> updateGui()));
		timer.setCycleCount(Animation.INDEFINITE);

		game = new GameEngine(numbOfBoardColumns, conf.getPropertyAsInt("winningNumber"));
		game.addObserver(this);
	}

	// Event-Handlers:

	/**
	 * Start-/ restart game function:
	 * 
	 * - Creates a new instance of GameStatitics which gathers game-specific
	 * information
	 * 
	 * - Creates a new instance of the game-engine: > A new Tile-array with two
	 * randomly placed numbers will automatically be created by the engine
	 * 
	 * - updateLabelList() initiates the mapping of the newly created Tile-array
	 * to the tile-Labels
	 * 
	 * - Sets buttons / Label to their appropriate state - Starts the
	 * time-counter on the main-screen
	 * 
	 */
	@FXML
	protected void startGame(ActionEvent event) {

		activateKeyHandler(rootPane);

		isRunning = false;
		updateBoardSize();
		game.startGame();
		updateLabelList(game.getBoard());

		labelScoreNumber.setText(conf.getPropertyAsString("startScore"));
		startButton.setText(conf.getPropertyAsString("restart.button"));
		pauseResumeButton.setVisible(true);
		pauseResumeButton.setText(conf.getPropertyAsString("pause.button"));
		isActive = true;
		isRunning = true;

		timer.play();
	}

	public void updateBoardSize() {
		// check for changed board size
		if (numbOfBoardColumns != conf.getPropertyAsInt("boardSize")) {
			if (isRunning == false) {
				numbOfBoardColumns = conf.getPropertyAsInt("boardSize");
				game = new GameEngine(numbOfBoardColumns, conf.getPropertyAsInt("winningNumber"));
				game.addObserver(this);
				initializeBoard();
			}
		}
	}

	/**
	 * Pause-/ resume function:
	 * 
	 * Based on whether the game is running or paused it...
	 * 
	 * - ... pauses or restarts the time-counter on the main-screen - ... pauses
	 * or resumes the time measuring in Game Statistics - ... sets the
	 * appropriate description of the Pause/ Resume-Button - ... sets isRunning
	 * = true in order to quit the pause, or = false to start the pause
	 * 
	 */

	@FXML
	void handlePauseResume(ActionEvent event) {
		if (isActive) {
			handlePause();
			// If game is currently paused:
		} else {
			handleResume();
		}
	}

	/**
	 * Pauses the game after pause-button was pushed
	 * - Timer gets suspended and Pause-Button-text turns into "Resume"
	 */

	protected void handlePause() {
		if (isRunning) {
			if (isActive) {
				game.pauseGame();
				timer.pause();
				pauseResumeButton.setText(conf.getPropertyAsString("resume.button"));
				isActive = false;
			}
		}
	}

	/**
	 * Resumes the game after resume-button was pushed
	 * - Timer continues and Resume-Button-text turns into "Pause"
	 * 
	 */

	protected void handleResume() {
		if (isRunning) {
			if (!isActive) {
				game.resumeGame();
				timer.play();
				pauseResumeButton.setText(conf.getPropertyAsString("pause.button"));
				isActive = true;
			}
		}
	}

	/**
	 * Key-Handler to move board-tiles in a given direction
	 *
	 * - reacts on key-Up, key-Down, key-left, key-right
	 * 
	 * - provided the move was valid (boolean move == true): > triggers the
	 * update of the Label-array (UI) based on the Tile-array (Engine) > sets
	 * the new score on the main-screen
	 * 
	 * 
	 * @param keyNode
	 *            The javafx-component upon which the Key-Handler is to be
	 *            registered
	 */

	private void activateKeyHandler(final Node keyNode) {

		final EventHandler<KeyEvent> keyEventHandler = new EventHandler<KeyEvent>() {
			public void handle(final KeyEvent keyEvent) {

				if (isActive) {
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
						updateLabelList(game.getBoard());
						updateScoreLabel(game.getStats().getScore());
					}
				}
				keyEvent.consume();
			}
		};
		keyNode.setOnKeyPressed(keyEventHandler);
	}

	protected void updateScoreLabel(long score) {

		NumberFormat nf = NumberFormat.getNumberInstance(Locale.getDefault());
		labelScoreNumber.setText(nf.format(score) + " Pts");
	}

	// Setters and Getters:

	public int getNumbOfBoardColumns() {
		return numbOfBoardColumns;
	}

	public void setNumbOfBoardColumns(int numbOfBoardColumns) {
		this.numbOfBoardColumns = numbOfBoardColumns;
	}

	/**
	 * - Populates the displayed game-board with EMPTY Labels (tiles)
	 * 
	 * - sets the width of each label based on board-width and tile-number per
	 * column
	 * 
	 * - Adds the created labels (tiles) to the labelList-Array for later
	 * updating
	 * 
	 */
	protected void initializeBoard() {
		gameBoard.getChildren().clear();
		labelList = new SuperLabel[numbOfBoardColumns][numbOfBoardColumns];
		double boardLength = (gameBoard.getPrefWidth() * 1.0) / numbOfBoardColumns;

		for (int i = 0; i < numbOfBoardColumns; i++) {
			for (int j = 0; j < numbOfBoardColumns; j++) {

				SuperLabel label = new SuperLabel(0, boardLength);
				label.setPrefSize(boardLength, boardLength);
				label.setAlignment(Pos.CENTER);
				GridPane.setConstraints(label, j, i);
				gameBoard.getChildren().add(label);

				labelList[i][j] = label;
			}
		}
	}

	/**
	 * 
	 * Maps the current Tile-Array (Engine) to the label-Array (UI)
	 * 
	 * - the displayed numbers on the board-tiles get updated - a visual effect
	 * is being triggered if the tile has just been merged / spawned
	 * 
	 */
	protected void updateLabelList(Tile[][] tileArray) {

		int i = 0;
		int j = 0;
		for (SuperLabel[] row : labelList) {
			for (SuperLabel label : row) {

				label.setTileNumber(tileArray[i][j].getValue());

				if (tileArray[i][j].isMerged()) {
					// fadeIn(label, 300, 0.5, 1.0, 3);
				} else if (tileArray[i][j].isSpawned()) {
					fadeIn(label, 400, 0.0, 1.0, 1);
				}
				j++;
			}
			j = 0;
			i++;
		}
	}

	/**
	 * Visual fade-in effect for tile-labels
	 * 
	 * 
	 * @param label:
	 *            the label upon which the visual effect has to be applied
	 * @param durationMillis:
	 *            duration of the fade-in in milliseconds
	 * @param from:
	 *            opacity of label at the beginning of effect (range: 0.0 to
	 *            1.0)
	 * @param to:
	 *            opacity of label at the end of the effect (range: 0.0 to 1.0)
	 * @param nbOfcycles:
	 *            number of times the fade-in effect gets executed upon
	 *            method-call
	 * 
	 */
	private void fadeIn(Label label, int durationMillis, double from, double to, int nbOfcycles) {

		FadeTransition fadeTransition = new FadeTransition(Duration.millis(durationMillis), label);
		fadeTransition.setFromValue(from);
		fadeTransition.setToValue(to);
		fadeTransition.setCycleCount(nbOfcycles);
		fadeTransition.play();
	}

	/**
	 * Receiver of notifications from Observables:
	 * 
	 * Reacts upon notification:
	 * 
	 * > Updates stop-watch on main-screen > Checks if boolean gameOver has to
	 * be set to "true" > Handles cases of game-won and game-over
	 * 
	 * 
	 * @param Observable:
	 *            Class where the current notification comes from
	 * @param Object
	 * 
	 */
	public void update(Observable o, Object arg) {

		if (o instanceof GameEngine) {
			Pair p = (Pair) arg;

			Platform.runLater(new Runnable() {
				@Override
				public void run() {

					switch ((String) p.getKey()) {
					case "gameOver":
						processGameOver(game.getStats());
						break;
					case "won":
						processGameWon();
						break;
					// case "score":
					// Long score = (Long) p.getValue();
					// labelScoreNumber.setText(score.toString());
					// break;
					}
				}
			});
		}
	}

	/**
	 * Triggers following actions in case of game-over:
	 * 
	 * - resets the timer, hides the Pause-Button - opens the Game-Over alert
	 * where user can put his name - saves the the typed username to the
	 * GameStatistics-object and adds that object to Highscores - switches to
	 * the highscore-screen - Writes the new score to the database
	 * 
	 * 
	 * @param stats:
	 *            GameStatistics, containing exclusively information about the
	 *            current game
	 * 
	 */

	private void processGameOver(GameStatistics stats) {
		isActive = false;
		isRunning = false;
		pauseResumeButton.setVisible(false);

		// Display game-over alert:
		GameOverDialog dialog = new GameOverDialog(conf.getPropertyAsString("gameOverDialog.title"), stats.getScore().intValue());

		if (dialog.showAndWait().isPresent()) {
			stats.setPlayerName(dialog.getPlayerName());
			
			ScoreHandler.getInstance().getHighscore().addHighscore(stats);			
			ScoreHandler.getInstance().writeScores(Config.getInstance().getPropertyAsString("highscoreFileName"));
			try {
				// Display highscore-list:
				// Main.switchScene(Scenes.HIGHSCORE);

				// Write scores to database:
				// scoreHandler.writeScores(highscoreList,
				// conf.getPropertyAsString("highscoreFileName"));
			} catch (Exception e) {
			}
		}
	}

	/**
	 * Display victory notification + ask user if he wants to continue:
	 */
	private void processGameWon() {
		if (!ignoreWinMessage) {
			VictoryAlert dialog = new VictoryAlert(conf.getPropertyAsString("victoryTitle.alert"), conf.getPropertyAsString("victoryText.alert"));
			boolean continuation = dialog.show();

			if (continuation) {
				// case: Winner wants to continue till game-over:
				ignoreWinMessage = true;
			} else {
				// case: Winner wants to quit game upon achieving the
				// winning-number:
				processGameOver(game.getStats());
			}
		}
	}

	/**
	 * This method is periodically triggered by Timeline Animation.
	 */
	protected void updateGui() {
		if (isRunning && isActive) {
			labelTimerTime.setText(DurationFormatUtils.formatDuration(game.getDuration(), conf.getPropertyAsString("timerTimeFormat")));
		}
	}

	/**
	 * Provides the player with a hint about the next move. e.g. which move
	 * gives the most points or merges the most tiles... (maybe configurable)
	 */
	@FXML
	public void hintAction() {
		if (isActive && isRunning) {

			// if the strategy has been changed in the config, reset the variable to null so it gets initialized with the new class
			if (strategyEnum != null && !conf.getPropertyAsString("strategy").equals(strategyEnum.getDescription())) {
				aiStrategy = null;
			}

			if (aiStrategy == null) {
				String strategy = conf.getPropertyAsString("strategy");
				Constructor<? extends BaseAIStrategy> constructor;

				strategyEnum = Strategy.findStateByDescription(strategy);

				Class<? extends BaseAIStrategy> strategyClazz = strategyEnum.getStrategy();

				try {
					constructor = strategyClazz.getConstructor(AIGameEngine.class);

					aiStrategy = constructor.newInstance(new AIGameEngine(numbOfBoardColumns, conf.getPropertyAsInt("winningNumber")));
					aiStrategy.initializeAI();
					aiStrategy.getEngine().startGame();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			Direction dirSuggestion = aiStrategy.getMove(game.getBoard());
			System.out.println(dirSuggestion.toString());

		}
	}
}