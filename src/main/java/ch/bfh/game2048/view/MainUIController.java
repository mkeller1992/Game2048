package ch.bfh.game2048.view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ch.bfh.game2048.view.model.HighscoreEntry;
import ch.bfh.game2048.view.model.Scene;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class MainUIController {
	@FXML
	BorderPane mainPane;

	@FXML
	ChoiceBox<Scene> gameModeSelector;

	Stage mainStage;

	Pane gamePane;
	GamePaneController gameController;

	Pane singleAIPane;
	SingleAIController singleAIController;

	Pane multiAiPane;
	MultiAIController multiAIController;

	Pane settingsPane;

	@FXML
	public void initialize() {
		gameModeSelector.getItems().setAll(Scene.values());
		gameModeSelector.getSelectionModel().selectFirst();
		gameModeSelector.setOnAction((event) -> {
			Scene selectedEntry = gameModeSelector.getSelectionModel().getSelectedItem();
			System.out.println(selectedEntry);
			switchScene(selectedEntry);
		});

		// try {
		// FXMLLoader settingsLoader = new FXMLLoader(getClass().getClassLoader().getResource("view/Settings.fxml"));
		// settingsPane = (Pane) settingsLoader.load();
		//
		// FXMLLoader multiAiLoader = new FXMLLoader(getClass().getClassLoader().getResource("view/MulitAI.fxml"));
		// multiAiPane = (Pane) multiAiLoader.load();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }

	}

	public void switchScene(Scene scene) {
		try {
			if (gameController != null) {
				gameController.handlePause();
			}
			if (singleAIController != null) {
				singleAIController.handlePause();
			}
			if (scene.equals(Scene.MAINSCENE)) {

				if (gamePane == null) {
					FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("view/GamePane.fxml"));
					gameController = new GamePaneController();
					loader.setController(gameController);
					gamePane = (Pane) loader.load();
				}

				gameController.updateBoardSize();
				gameController.handleResume();

				mainPane.setCenter(gamePane);

				mainStage.setWidth(420);
			} else if (scene.equals(Scene.HIGHSCORE)) {

				List<HighscoreEntry> highscores = new ArrayList<HighscoreEntry>();

				highscores.add(new HighscoreEntry(1, "ludi", 234, 34, 234, 234, System.currentTimeMillis(), 4));
				highscores.add(new HighscoreEntry(2, "ladsf", 124, 34, 234, 234, System.currentTimeMillis(), 4));

				HighscorePane highscorePane = new HighscorePane(highscores);

				gameController.handlePauseResume(null);
				mainPane.setCenter(highscorePane);

				mainStage.setWidth(800);
			} else if (scene.equals(Scene.SETTINGS)) {
				SettingsController settingsController = new SettingsController();
				FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("view/Settings.fxml"));
				loader.setController(settingsController);
				Pane settingsPane = (Pane) loader.load();

				mainPane.setCenter(settingsPane);
				mainStage.setWidth(425);

			} else if (scene.equals(Scene.SINGLEAI)) {

				if (singleAIPane == null) {
					singleAIController = new SingleAIController();
					FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("view/GamePane.fxml"));
					loader.setController(singleAIController);
					singleAIPane = (Pane) loader.load();
					singleAIController.updateBoardSize();
				}

				singleAIController.handleResume();
				mainPane.setCenter(singleAIPane);
				mainStage.setWidth(420);

			} else if (scene.equals(Scene.MULTIAI)) {

				if (multiAiPane == null) {
					multiAIController = new MultiAIController();
					FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("view/MultiAI.fxml"));
					loader.setController(multiAIController);
					multiAiPane = (Pane) loader.load();					
				}				
				mainPane.setCenter(multiAiPane);
				mainStage.setWidth(650);				
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void setStage(Stage stage) {
		this.mainStage = stage;
	}
}
