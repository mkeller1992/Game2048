package ch.bfh.game2048.view;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ch.bfh.game2048.ai.AIGameEngine;
import ch.bfh.game2048.ai.strategies.AIStrategyMatthias;
import ch.bfh.game2048.ai.strategies.BaseAIStrategy;
import ch.bfh.game2048.ai.strategies.SimpleUpLeftStrategy;
import ch.bfh.game2048.model.Direction;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.util.Pair;

public class MultiAIController implements Observer {

	@FXML
	ChoiceBox comboStrategy;

	@FXML
	ChoiceBox comboThreadAmount;

	@FXML
	Button buttonStart;

	@FXML
	TextArea resultScreen;

	@FXML
	Label labelTime;

	MultiAIStats multiAiStats;

	MultiAIController instance;

	public MultiAIController() {
		this.instance = this;
	}

	@FXML
	public void initialize() {
		multiAiStats = new MultiAIStats();
	}

	@Override
	public void update(Observable observable, Object object) {
		AIGameEngine engine = (AIGameEngine) observable;

		Pair<String, Boolean> pair = (Pair) object;
		System.out.println(String.format("Thread %d meldet %s: %s", engine.getAiNumber(), pair.getKey(), pair.getValue()));

	}

	public void start() {
		// start StopWatch
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub

				System.out.println("let the fun begin...");

				ExecutorService executor = Executors.newFixedThreadPool(50);

				int amountOfThreads = 8;

				ArrayList<AiPlayer> players = new ArrayList<AiPlayer>();

				for (int i = 0; i < amountOfThreads; i++) {
					AIGameEngine engine = new AIGameEngine(4, 2048, i);
					BaseAIStrategy strategy = new AIStrategyMatthias(engine);
					Runnable aiPlayer = new AiPlayer(strategy, instance, engine);
					players.add((AiPlayer) aiPlayer);
					executor.execute(aiPlayer);
				}

				executor.shutdown(); // ?

				try {
					Thread.sleep(100);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}

				while (!executor.isTerminated()) {
					// AI's am spilen warten auf punkte und infos und blah blah
					System.out.println("waiting loop...");
					updateGameStatistic(players);
					updateGUI();

					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				// Stop timer (StopWatch)
				System.out.println("all threads finished...");
			}
		});
		t.start();

	}

	private void updateGameStatistic(ArrayList<AiPlayer> players) {
		int amountOfLosses = 0;
		int amountOfWins = 0;
		int maxPoints = 0;
		int minPoints = Integer.MAX_VALUE;

		for (AiPlayer p : players) {

			if (p.game.isRunning()) {
				maxPoints = Math.max(p.game.getStats().getScore().intValue(), maxPoints);
				minPoints = Math.min(p.game.getStats().getScore().intValue(), minPoints);
			}
			if (!p.game.isRunning()) {
				amountOfLosses++;
			}

			if (p.game.getStats().getHighestValue() >= 2048) {
				amountOfWins++;
			}
		}

		multiAiStats.setAmountOfLosses(amountOfLosses);
		multiAiStats.setAmountOfWins(amountOfWins);
		multiAiStats.setMaxPoints(maxPoints);
		multiAiStats.setMinPoints(minPoints);

	}

	private void updateGUI() {

		Platform.runLater(new Runnable() {
			@Override
			public void run() {

				String text = "Amount of losses: " + multiAiStats.getAmountOfLosses() + "\n" + "Amount of wins: " + multiAiStats.getAmountOfWins() + "\n" + "Max. points: " + multiAiStats.getMaxPoints() + "\n" + "Min. points: "
						+ multiAiStats.getMinPoints() + "\n";
				resultScreen.setText(text);
				// use stopwatch
			}
		});

	}

	public class MultiAIStats {

		int maxPoints;
		int minPoints;
		int averagePoints;

		int maxMoves;
		int minMoves;
		int averageMoves;

		int amountOfLosses;
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

		public int getAmountOfLosses() {
			return amountOfLosses;
		}

		public void setAmountOfLosses(int amountOfLosses) {
			this.amountOfLosses = amountOfLosses;
		}

		public int getAmountOfWins() {
			return amountOfWins;
		}

		public void setAmountOfWins(int amountOfWins) {
			this.amountOfWins = amountOfWins;
		}
	}

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

			strategy.initializeAI();
			game.startGame();

			game.addObserver(controller);

			while (game.isRunning()) {
				Direction dir = strategy.getMove(game.getBoard());
				game.move(dir);
				try {
					Thread.sleep(25);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
	}

}
