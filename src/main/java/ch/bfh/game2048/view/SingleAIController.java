package ch.bfh.game2048.view;

import java.io.FileNotFoundException;

import javax.xml.bind.JAXBException;

import ch.bfh.game2048.ai.AIGameEngine;
import ch.bfh.game2048.ai.strategies.BaseAIStrategy;
import ch.bfh.game2048.ai.strategies.Strategy;
import ch.bfh.game2048.model.Direction;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class SingleAIController extends GamePaneController {

	BaseAIStrategy aiStrategy;
	Thread aiPlayer;

	public SingleAIController() {

	}

	@FXML
	public void initialize() throws FileNotFoundException, JAXBException {
		super.initialize();

		game = new AIGameEngine(numbOfBoardColumns, conf.getPropertyAsInt("winningNumber"));
		game.addObserver(this);

		labelTimerName.setText("Moves: ");

		ignoreWinMessage = true;
		btnHint.setVisible(false);
	}

	private void loadAIEngine() {
		aiStrategy = Strategy.getAIStrategy(Strategy.findStateByDescription(conf.getPropertyAsString("strategy")),conf.getPropertyAsInt("boardSize"));
		aiStrategy.initializeAI();
		
		game = aiStrategy.getEngine();	
		game.addObserver(this);
	}

	@FXML
	@Override
	protected void startGame(ActionEvent event) {

		loadAIEngine();
		updateLabelList(game.getBoard());
		game.startGame();

		labelScoreNumber.setText(conf.getPropertyAsString("startScore"));
		startButton.setText(conf.getPropertyAsString("restart.button"));
		pauseResumeButton.setVisible(true);
		pauseResumeButton.setText(conf.getPropertyAsString("pause.button"));
		isActive = true;
		isRunning = true;

		timer.play();

		playGame();
	}

	@Override
	protected void handleResume() {

		super.handleResume();
			
		playGame();
	}

	private void playGame() {

		// start thread
		aiPlayer = new Thread(() -> {

			while (isRunning && isActive) {
				try {
					Thread.sleep(100);

					Direction dir = aiStrategy.getMove(game.getBoard());

					System.out.println("doing move: " + dir);
					game.move(dir);

					Platform.runLater(new Runnable() {
						@Override
						public void run() {

							updateLabelList(game.getBoard());
							updateScoreLabel(game.getStats().getScore());
						}
					});

				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		System.out.println("Starting...");
		aiPlayer.start();

	}

	protected void updateGui() {
		if (isRunning && isActive) {
			labelTimerTime.setText(" " + game.getStats().getAmountOfMoves());
		}
	}

}
