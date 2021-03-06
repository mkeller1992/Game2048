package ch.bfh.game2048.view;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.commons.lang3.time.StopWatch;

import ch.bfh.game2048.ai.AIGameEngine;
import ch.bfh.game2048.ai.strategies.BaseAIStrategy;
import ch.bfh.game2048.ai.strategies.Strategy;
import ch.bfh.game2048.model.Direction;
import ch.bfh.game2048.persistence.Config;
import ch.bfh.game2048.view.model.BoardSizes;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class MultiAIController {

	@FXML
	ChoiceBox<Strategy> chbStrategy;

	@FXML
	ChoiceBox<String> chbThreadAmount;
	
	@FXML
	ChoiceBox<BoardSizes>chbBoardSize;

	@FXML
	Button buttonStart;

	@FXML
	TextArea resultScreen;

	@FXML
	Label labelTime;

	@FXML
	Label labelStrategy;

	@FXML
	Label labelNumbOfThreads;

	@FXML
	Label labelRunningTime;	


	MultiAIStats multiAiStats;
	MultiAIController instance;
	ExecutorService executor;

	StopWatch stopwatch;

	public MultiAIController() {
		this.instance = this;
	}

	/**
	 * - sets the settings of the result-screen
	 * - populates the choiceBoxes and adds an event-handler
	 */

	@FXML
	public void initialize() {
		multiAiStats = new MultiAIStats();

		stopwatch = new StopWatch();

		// Set display-font and set screen to non-editable:
		resultScreen.setFont(Font.font("Consolas", FontWeight.BOLD, 16));
		resultScreen.setEditable(false);
		
		// Set Strategy-Selection ChoiceBox:
		chbStrategy.getItems().setAll(Strategy.values());
		Strategy strategy = Strategy.findStateByDescription(Config.getInstance().getPropertyAsString("multiAI.strategy"));
		chbStrategy.getSelectionModel().select(strategy);
		
		// Add event-handler to Strategy-ChoiceBox to save selection to properties
		chbStrategy.setOnAction((event) -> {
			String strat = chbStrategy.getSelectionModel().getSelectedItem().getDescription();
			Config.getInstance().setProperty("multiAI.strategy", strat);
		});

		// Populate Thread-Amount ChoiceBox:
		chbThreadAmount.setItems(Config.getInstance().getPropertyAsObservableList("threadArray"));
		chbThreadAmount.getSelectionModel().select(Config.getInstance().getPropertyAsString("selectedThreadAmount"));

		// Add event-handler to Thread-Amount ChoiceBox to save selection to properties
		chbThreadAmount.setOnAction((event) -> {
			String selectedEntry = chbThreadAmount.getSelectionModel().getSelectedItem();
			Config.getInstance().setProperty("selectedThreadAmount", selectedEntry);
		});
		
		// Populate Board-Size ChoiceBox:
		chbBoardSize.getItems().addAll(BoardSizes.values());
		
		// Get previously selected entry and select it
		BoardSizes selectedSize = BoardSizes.findStateByBoardSize(Config.getInstance().getPropertyAsInt("multiAI.boardsize"));
		chbBoardSize.getSelectionModel().select(selectedSize);
		
		// Add event-handler to Board-Size ChoiceBox to save selection to properties
		chbBoardSize.setOnAction(ae -> {					
			BoardSizes size= chbBoardSize.getSelectionModel().getSelectedItem();
			Config.getInstance().setProperty("multiAI.boardsize", size.getBoardSize());
		});
	}

	/**
	 * Conducts the initializing up to completion of all threads which get assigned to play a game:
	 */

	@FXML
	public void start() {

		buttonStart.setText(Config.getInstance().getPropertyAsString("restart.button"));
		buttonStart.setVisible(false);

		// start StopWatch
		if (stopwatch != null) {
			stopwatch.reset();
		}
		stopwatch.start();

		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {

				executor = Executors.newFixedThreadPool(100);

				ArrayList<AiPlayer> players = new ArrayList<AiPlayer>();

				int amountOfThreads = Config.getInstance().getPropertyAsInt("selectedThreadAmount");
				
				Integer boardSize = Config.getInstance().getPropertyAsInt("multiAI.boardsize");
				Strategy selectedStrategy = chbStrategy.getSelectionModel().getSelectedItem();

				// Initialize threads, give every thread an AI-Player (a runnable) with an AI-Strategy
				for (int i = 0; i < amountOfThreads; i++) {					
					// In multi-AI-mode games are always played on 4x4 boards:
								
					BaseAIStrategy strategy = Strategy.getAIStrategy(selectedStrategy, boardSize);
					strategy.initializeAI();
					strategy.getEngine().setAiNumber(i);
					Runnable aiPlayer = new AiPlayer(strategy, instance, strategy.getEngine());					
					players.add((AiPlayer) aiPlayer);
					executor.execute(aiPlayer);
				}

				// Accept no more new threads but execute the current ones till completion:
				executor.shutdown();
				
				resultScreen.setText("loading...");
				
				try {
					Thread.sleep(250);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				
				// while game is playing, update statistics every 0.5 seconds:
				while (!executor.isTerminated()) {
					
					updateGameStatistic(players);
					updateGUI(players);
					
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				// Responsible for displaying the stats of the longest game:
				updateGameStatistic(players);
				updateGUI(players);
				buttonStart.setVisible(true);
			}
		});
		t.start();
	}

	/**
	 * - Retrieves the current score-line of each AI-Player (== of each Thread)
	 * - Assembles overall live-statistics and saves it to Multi-AI Stats
	 * 
	 * @param players
	 *            contains all active AI-Players (each includes a GameStatistics-Object)
	 */

	private void updateGameStatistic(ArrayList<AiPlayer> players) {
		int amountOfGameOver = 0;
		int amountOfWins = 0;
		int maxPoints = 0;
		int minPoints = Integer.MAX_VALUE;
		int totalPoints = 0;

		for (AiPlayer p : players) {

			if(p.game.getStats() != null){
			totalPoints += p.game.getStats().getScore();
			}
			
			// Computes highest and lowest points of players who are still playing
			if (p.game.getStats() != null && p.game.isRunning() || executor.isTerminated()) {
				maxPoints = Math.max(p.game.getStats().getScore().intValue(), maxPoints);
				minPoints = Math.min(p.game.getStats().getScore().intValue(), minPoints);
			}

			// Computes number of games finished
			if (!p.game.isRunning()) {
				amountOfGameOver++;
			}

			// Computes number of games won
			if (p.game.getStats() != null && p.game.getStats().getHighestValue() >= 2048) {
				amountOfWins++;
			}
		}

		multiAiStats.setAmountOfGameOver(amountOfGameOver);
		multiAiStats.setAmountOfWins(amountOfWins);
		multiAiStats.setMaxPoints(maxPoints);
		multiAiStats.setMinPoints(minPoints);
		multiAiStats.setAveragePoints(totalPoints / players.size());
	}

	/**
	 * - Assembles the Statistics-String and displays it on the screen
	 * - Receives ArrayList with AI-Players to check who is still playing
	 * 
	 * @param players
	 *            contains all active AI-Players (each includes a GameStatistics-Object)
	 */

	private void updateGUI(ArrayList<AiPlayer> players) {

		NumberFormat nf = NumberFormat.getNumberInstance(Locale.getDefault());
		StringBuilder sb = new StringBuilder();

		for (AiPlayer p : players) {
			if (p.game.getStats() != null && !p.game.isRunning()) {
				sb.append(String.format("%1$-5s %2$-6s %3$-10s %4$-7s", p.game.getAiNumber(), (p.game.getDuration() / 1000), nf.format(p.game.getStats().getScore()), p.game.getStats().getHighestValue()) + "\n");
			}
		}

		Platform.runLater(new Runnable() {
			@Override
			public void run() {

				String text = String.format("%1$-13s %2$-6s", "Games won: ", multiAiStats.getAmountOfWins()) + "\n";
				text += String.format("%1$-13s %2$-6s", "Games ended: ", multiAiStats.getAmountOfGameOver() + "/" + players.size()) + "\n";
				text += String.format("%1$-13s %2$-6s", "Max. Points: ", nf.format(multiAiStats.getMaxPoints())) + "\n";
				text += String.format("%1$-13s %2$-6s", "Av. Points: ", nf.format(multiAiStats.getAveragePoints())) + "\n";
				text += String.format("%1$-13s %2$-6s", "Min. Points: ", nf.format(multiAiStats.getMinPoints())) + "\n\n";
				text += "-------------------------------\n";
				text += String.format("%1$-5s %2$-6s %3$-10s %4$-7s", "Th#", "Time", "Points", "Highest");
				text += "\n-------------------------------\n" + sb.toString();

				if (multiAiStats.getAmountOfGameOver() > 0) {
					text += "-------------------------------\n";
				}

				resultScreen.setText(text);

				// display current stopwatch-time:
					stopwatch.split();
					labelTime.setText(DurationFormatUtils.formatDuration(stopwatch.getSplitTime(), Config.getInstance().getPropertyAsString("timerTimeFormat")));

				// if all threads have ended --> Stop stopwatch:
				if (executor.isTerminated()) {
					stopwatch.stop();
				}
			}
		});
	}

	/**
	 * Multi AI Statistics:
	 * 
	 * - Sort of a push-object which contains the summarized statistics of the current simulation.
	 * - Is used by updateGUI-method to display new stats every 0.5 seconds
	 *
	 */

	public class MultiAIStats {

		int maxPoints;
		int minPoints;
		int averagePoints;

		int maxMoves;
		int minMoves;
		int averageMoves;

		int amountOfGameOver;
		int amountOfWins;

		public int getMaxPoints() {
			return maxPoints;
		}

		public void setMaxPoints(int maxPoints) {
			this.maxPoints = maxPoints;
		}

		public int getMinPoints() {
			return minPoints;
		}

		public void setMinPoints(int minPoints) {
			this.minPoints = minPoints;
		}

		public int getAveragePoints() {
			return averagePoints;
		}

		public void setAveragePoints(int averagePoints) {
			this.averagePoints = averagePoints;
		}

		public int getMaxMoves() {
			return maxMoves;
		}

		public void setMaxMoves(int maxMoves) {
			this.maxMoves = maxMoves;
		}

		public int getMinMoves() {
			return minMoves;
		}

		public void setMinMoves(int minMoves) {
			this.minMoves = minMoves;
		}

		public int getAverageMoves() {
			return averageMoves;
		}

		public void setAverageMoves(int averageMoves) {
			this.averageMoves = averageMoves;
		}

		public int getAmountOfGameOver() {
			return amountOfGameOver;
		}

		public void setAmountOfGameOver(int amountOfGameOver) {
			this.amountOfGameOver = amountOfGameOver;
		}

		public int getAmountOfWins() {
			return amountOfWins;
		}

		public void setAmountOfWins(int amountOfWins) {
			this.amountOfWins = amountOfWins;
		}
	}

	/**
	 * AI-Player Runnable
	 * 
	 * For each Thread which plays a game, such a runnable will be assigned
	 *
	 */

	public class AiPlayer implements Runnable {
		BaseAIStrategy strategy;
		AIGameEngine game;
		MultiAIController controller;

		AiPlayer(BaseAIStrategy strategy, MultiAIController controller, AIGameEngine game) {
			this.strategy = strategy;
			this.controller = controller;
			this.game = game;
			
			
		}

		@Override
		public void run() {
			
			game.startGame();

			// Plays the game while game-engine is running == while not game over

			while (game.isRunning()) {
				Direction dir = strategy.getMove(game.getBoard());
				game.move(dir);
				try {
					Thread.sleep(25);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		}
	}
}
