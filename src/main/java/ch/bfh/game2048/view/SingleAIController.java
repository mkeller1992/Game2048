package ch.bfh.game2048.view;

import java.io.FileNotFoundException;

import javax.xml.bind.JAXBException;

import org.apache.commons.lang3.time.DurationFormatUtils;

import ch.bfh.game2048.ai.AIGameEngine;
import ch.bfh.game2048.ai.strategies.BaseAIStrategy;
import ch.bfh.game2048.ai.strategies.RandomStrategy;
import ch.bfh.game2048.ai.strategies.SimpleUpLeftAI;
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
		
		labelTimerName.setText("Moves:");

	}

	private void loadAIEngine() {
		aiStrategy = new RandomStrategy((AIGameEngine) game);
		aiStrategy.initializeAI();
	}

	@FXML
	@Override
	protected void startGame(ActionEvent event) {
		game.startGame();
		updateLabelList(game.getBoard());

		loadAIEngine();

		labelScoreNumber.setText(conf.getPropertyAsString("startScore"));
		startButton.setText(conf.getPropertyAsString("restart.button"));
		pauseResumeButton.setVisible(true);
		isActive = true;
		isRunning = true;

		timer.play();

		// start thread
		aiPlayer = new Thread(() -> {

			while (isRunning && isActive) {
				try {
					Thread.sleep(50);

					Direction dir = aiStrategy.getMove(game.getBoard());
					System.out.println("doing move: "+ dir);
					game.move(dir);
					
					Platform.runLater(new Runnable() {
						@Override
						public void run() {							
	
							updateLabelList(game.getBoard());
							updateScoreLabel(game.getStats().getScore());
						}
					});

				} catch (InterruptedException e) {
				}
			}
		});
		System.out.println("Starting...");
		aiPlayer.start();

	}
	
	protected void updateGui() {
		if (isRunning && isActive) {
			labelTimerTime.setText(""+game.getStats().getAmountOfMoves());
		}
	}

}
