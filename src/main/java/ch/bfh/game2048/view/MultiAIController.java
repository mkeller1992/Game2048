package ch.bfh.game2048.view;

import java.lang.reflect.Constructor;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.commons.lang3.time.StopWatch;

import ch.bfh.game2048.ai.AIGameEngine;
import ch.bfh.game2048.ai.strategies.AIStrategyMatthias;
import ch.bfh.game2048.ai.strategies.BaseAIStrategy;
import ch.bfh.game2048.ai.strategies.SimpleUpLeftStrategy;
import ch.bfh.game2048.ai.strategies.Strategy;
import ch.bfh.game2048.model.Direction;
import ch.bfh.game2048.persistence.Config;
import ch.bfh.game2048.view.model.Scene;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Pair;

public class MultiAIController implements Observer {

	@FXML
	ChoiceBox<Strategy> chbStrategy;

	@FXML
	ChoiceBox<String> chbThreadAmount;

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

	@FXML
	public void initialize() {
		multiAiStats = new MultiAIStats();

		stopwatch = new StopWatch();

		resultScreen.setFont(Font.font("Consolas", FontWeight.BOLD, 16));
		resultScreen.setEditable(false);

		// Populate Thread-Amount ChoiceBox:
		chbThreadAmount.setItems(Config.getInstance().getPropertyAsObservableList("threadArray"));
		chbThreadAmount.getSelectionModel().select(Config.getInstance().getPropertyAsString("selectedThreadAmount"));

		// Add event-handler to Thread-Amount ChoiceBox:
		chbThreadAmount.setOnAction((event) -> {
			String selectedEntry = chbThreadAmount.getSelectionModel().getSelectedItem();
			Config.getInstance().setProperty("selectedThreadAmount", selectedEntry);
		});
		
		chbStrategy.setItems(FXCollections.observableArrayList(Strategy.values()));
		chbStrategy.getSelectionModel().selectFirst();
	}

	@Override
	public void update(Observable observable, Object object) {
		AIGameEngine engine = (AIGameEngine) observable;

		Pair<String, Boolean> pair = (Pair) object;
		System.out.println(String.format("Thread %d meldet %s: %s", engine.getAiNumber(), pair.getKey(), pair.getValue()));

	}

	public void start() {

		buttonStart.setText(Config.getInstance().getPropertyAsString("restart.button"));

		// start StopWatch
		if (stopwatch != null) {
			stopwatch.reset();
		}
		stopwatch.start();

		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {

				System.out.println("let the fun begin...");

				executor = Executors.newFixedThreadPool(100);

				ArrayList<AiPlayer> players = new ArrayList<AiPlayer>();

				int amountOfThreads = Config.getInstance().getPropertyAsInt("selectedThreadAmount");
				
				for (int i = 0; i < amountOfThreads; i++) {
					AIGameEngine engine = new AIGameEngine(4, 2048, i);
										
					BaseAIStrategy strategy = Strategy.getAIStrategy(chbStrategy.getSelectionModel().getSelectedItem());
					
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
					updateGUI(players);

					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				// sleep 100
				
				// Responsible for displaying the stats of the longest game:
				updateGameStatistic(players);
				updateGUI(players);				
			}
		});
		t.start();

	}

	private void updateGameStatistic(ArrayList<AiPlayer> players) {
		int amountOfGameOver = 0;
		int amountOfWins = 0;
		int maxPoints = 0;
		int minPoints = Integer.MAX_VALUE;
		int totalPoints = 0;

		for (AiPlayer p : players) {

			totalPoints += p.game.getStats().getScore();

			if (p.game.isRunning() || executor.isTerminated()) {
				maxPoints = Math.max(p.game.getStats().getScore().intValue(), maxPoints);
				minPoints = Math.min(p.game.getStats().getScore().intValue(), minPoints);
			}
			if (!p.game.isRunning()) {
				amountOfGameOver++;
				
				if (p.game.getStats().getHighestValue() >= 2048) {
					amountOfWins++;
				}				
			}
		}

		multiAiStats.setAmountOfGameOver(amountOfGameOver);
		multiAiStats.setAmountOfWins(amountOfWins);
		multiAiStats.setMaxPoints(maxPoints);
		multiAiStats.setMinPoints(minPoints);
		multiAiStats.setAveragePoints(totalPoints / players.size());
	}

	private void updateGUI(ArrayList<AiPlayer> players) {

		NumberFormat nf = NumberFormat.getNumberInstance(Locale.getDefault());
		StringBuilder sb = new StringBuilder();

		for (AiPlayer p : players) {
			if (!p.game.isRunning()) {
				sb.append(String.format("%1$-5s %2$-6s %3$-10s %4$-7s", p.game.getAiNumber(), (p.game.getDuration() / 1000), nf.format(p.game.getStats().getScore()), p.game.getStats().getHighestValue()) + "\n");
			}
		}

		Platform.runLater(new Runnable() {
			@Override
			public void run() {

				String text = String.format("%1$-13s %2$-6s", "Games won: ", multiAiStats.getAmountOfWins() ) + "\n";
				text += String.format("%1$-13s %2$-6s", "Games ended: ", multiAiStats.getAmountOfGameOver()+"/"+players.size()) + "\n";
				text += String.format("%1$-13s %2$-6s", "Max. Points: ", nf.format(multiAiStats.getMaxPoints())) + "\n";
				text += String.format("%1$-13s %2$-6s", "Av. Points: ", nf.format(multiAiStats.getAveragePoints())) + "\n";
				text += String.format("%1$-13s %2$-6s", "Min. Points: ", nf.format(multiAiStats.getMinPoints())) + "\n\n";
				text += "-------------------------------\n";
				text += String.format("%1$-5s %2$-6s %3$-10s %4$-7s", "Th#", "Time", "Points", "Highest");
				text += "\n-------------------------------\n" + sb.toString();

				if (multiAiStats.getAmountOfGameOver() > 0) {
					text += "-------------------------------\n";
				}

				System.out.println("Second");

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
