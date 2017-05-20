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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;

public class MainUIController implements Observer {

	@FXML
	private BorderPane rootPane;

	@FXML
	private GridPane gameBoard;

	@FXML
	private Button startButton;

	@FXML
	private Button pauseResumeButton;
	
	@FXML
	private Label labelScoreName;

	@FXML
	private Label labelScoreNumber;  // where the current score gets displayed

	@FXML
	private Label labelTimerTime;    // where the time of the stop-watch gets displayed

	private SuperLabel[][] labelList;
	
	GameEngine game;

	private ScoreHandler scoreHandler;
	private Timer timer;
	private Highscore highscoreList;
	private Config conf;

	private int numbOfBoardColumns = 4;

	private boolean isRunning = false;
	
	
	/**
	 * 
	 * Initialization upon opening of the application:
	 * 
	 * Initializes and stores the following properties:
	 * - conf: For accessing the data in the property-file
	 * - scoreHandler: Marshaller / Unmarshaller for database
	 * - game: Game-Engine
	 * - timer: Stopwatch on main-screen
	 * 
	 * > Sets up gameBoard on main-screen with empty tiles
	 * > Registers Key-Handler (for triggering tile-moves upon keystrokes)
	 * 
	 * 
	 */	
	
	@FXML
	public void initialize() throws FileNotFoundException, JAXBException {

		// set properties
		conf = Config.getInstance();
		scoreHandler = new ScoreHandler();
		highscoreList = scoreHandler.readScores(conf.getPropertyAsString("highscoreFileName"));
		game = new GameEngine(numbOfBoardColumns);
		timer = new Timer();
		timer.addObserver(this);
		
		// prepare gui
		initializeBoard();
		pauseResumeButton.setVisible(false);
		activateKeyHandler(startButton);
	}
	

	// Event-Handlers:

	/**
	 * Start-/ restart game function:
	 * 
	 * - Creates a new instance of GameStatitics which gathers game-specific information
	 * 
	 * - Creates a new instance of the game-engine:
	 *   > A new Tile-array with two randomly placed numbers will automatically be created by the engine
	 *    
	 * - updateLabelList() initiates the mapping of the newly created Tile-array to the tile-Labels
	 * 
	 * - Sets buttons / Label to their appropriate state
	 * - Starts the time-counter on the main-screen
	 * 
	 */
	
	@FXML
	void startGame(ActionEvent event) {

			GameStatistics stats = new GameStatistics("" ,numbOfBoardColumns);
			stats.addObserver(this);						
			game = new GameEngine(numbOfBoardColumns, stats);
			updateLabelList(game.getBoard());
			
			labelScoreNumber.setText(conf.getPropertyAsString("startScore"));
			startButton.setText(conf.getPropertyAsString("restart.button"));
			pauseResumeButton.setVisible(true);
			timer.start();
			isRunning = true;
	}

	
	/**
	 * Pause-/ resume function:
	 * 
	 * Based on whether the game is running or paused it...
	 * 
	 * - ... pauses or restarts the time-counter on the main-screen
	 * - ... pauses or resumes the time measuring in Game Statistics
	 * - ... sets the appropriate description of the Pause/ Resume-Button
	 * - ... sets isRunning = true in order to quit the pause, or = false to start the pause
	 * 
	 */
	
	@FXML
	void handlePauseResume(ActionEvent event) {
				
		// If game is currently running:
			if (isRunning) {
				timer.stop();
				game.getStats().pauseTime();
				pauseResumeButton.setText(conf.getPropertyAsString("resume.button"));
				isRunning = false;
		// If game is currently paused:		
			} else {
				timer.start();
				game.getStats().resumeTime();
				pauseResumeButton.setText(conf.getPropertyAsString("pause.button"));
				isRunning = true;
		}
	}
	

	/**
	 * Key-Handler to move board-tiles in a given direction
	 *
	 * - reacts on key-Up, key-Down, key-left, key-right
	 * 
	 * - provided the move was valid (boolean move == true):
	 *  > triggers the update of the Label-array (UI) based on the Tile-array (Engine)
	 *  > sets the new score on the main-screen
	 * 
	 * 
	 * @param keyNode : The javafx-component upon which the Key-Handler
	 * 					is to be registered
	 */
	

	private void activateKeyHandler(final Node keyNode) {
		
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
						updateLabelList(game.getBoard());
						labelScoreNumber.setText("" + game.getStats().getFormattedScore() + " Pts");
					}
				}
				keyEvent.consume();
			}
		};
		keyNode.setOnKeyPressed(keyEventHandler);
	}
	
	
	// Setters and Getters:
	

	public int getNumbOfBoardColumns() {
		return numbOfBoardColumns;
	}

	public void setNumbOfBoardColumns(int numbOfBoardColumns) {
		this.numbOfBoardColumns = numbOfBoardColumns;
	}


	/**
	 * -  Method will be removed after integration of scene-switch
	 * 
	 */
	
	public HighscorePane getHighScorePane() throws FileNotFoundException, JAXBException{		
		return new HighscorePane(highscoreList, numbOfBoardColumns);	
	}
	
	
	
	/**
	 * - Populates the displayed game-board with EMPTY Labels (tiles)
	 * 
	 * - sets the width of each label based on board-width and tile-number per column
	 * 
	 * - Adds the created labels (tiles) to the labelList-Array for later updating
	 * 
	 */
		
	private void initializeBoard() {

		labelList = new SuperLabel[numbOfBoardColumns][numbOfBoardColumns];
		double boardLength=(gameBoard.getPrefWidth() * 1.0) / numbOfBoardColumns;

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
	 * - the displayed numbers on the board-tiles get updated
	 * - a visual effect is being triggered if the tile has just been merged / spawned
	 * 
	 */
		
	private void updateLabelList(Tile[][] tileArray) {

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
	 * @param label: the label upon which the visual effect has to be applied
	 * @param durationMillis: duration of the fade-in in milliseconds
	 * @param from: opacity of label at the beginning of effect (range: 0.0 to 1.0)
	 * @param to: opacity of label at the end of the effect (range: 0.0 to 1.0)
	 * @param nbOfcycles: number of times the fade-in effect gets executed upon method-call
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
	 * > Updates stop-watch on main-screen
	 * > Checks if boolean gameOver has to be set to "true"
	 * > Handles cases of game-won and game-over
	 * 
	 * 
	 * @param Observable: Class where the current notification comes from
	 * @param Object
	 * 
	 */
	
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

					// case: player has reached game-over:
					if (stats.isGameOver()) {
						processGameOver(stats);
					}

					// case: Game-winning number has just been reached:
					else if (stats.isGameContinue() == false
							&& stats.getHighestValue() == conf.getPropertyAsInt("winningNumber")) {

						// Display victory notification + ask user if he wants to continue:
						VictoryAlert dialog = new VictoryAlert(conf.getPropertyAsString("victoryTitle.alert"),
								conf.getPropertyAsString("victoryText.alert"));
						boolean continuation = dialog.show();
						
						// case: Winner wants to continue till game-over:
						if (continuation) {
							stats.setGameContinue(true);
						
						// case: Winner wants to quit game upon achieving the winning-number:
						} else {
							stats.setGameOver(true);
						}
					}
				}
			});
		}
	}

	
	
	/**
	 * Triggers following actions in case of game-over:
	 * 
	 * - resets the timer, hides the Pause-Button
	 * - opens the Game-Over alert where user can put his name
	 * - saves the the typed username to the GameStatistics-object and adds that object to Highscores
	 * - switches to the highscore-screen
	 * - Writes the new score to the database
	 * 
	 * 
	 * @param stats: GameStatistics, containing exclusively information about the current game
	 * 
	 */
		
	private void processGameOver(GameStatistics stats) {

		timer.reset();
		isRunning = false;
		pauseResumeButton.setVisible(false);
		
		// Display game-over alert:
		GameOverDialog dialog = new GameOverDialog(conf.getPropertyAsString("gameOverDialog.title"), stats.getScore());
		
		if (dialog.showAndWait().isPresent()) {
			stats.setPlayerName(dialog.getPlayerName());
			highscoreList.addHighscore(stats);

			try {
				// Display highscore-list:
				Main.switchScene(Scenes.HIGHSCORE);
				
				// Write scores to database:
				scoreHandler.writeScores(highscoreList, conf.getPropertyAsString("highscoreFileName"));
			} catch (JAXBException | FileNotFoundException e) {
			}
		}
	}

}