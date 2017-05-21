package ch.bfh.game2048.view;

import java.io.IOException;

import ch.bfh.game2048.model.Highscore;
import ch.bfh.game2048.view.model.Scene;
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
	
	
	@FXML
	public void initialize() {
		gameModeSelector.getItems().setAll(Scene.values());

		gameModeSelector.setOnAction((event) -> {
			Scene selectedEntry = gameModeSelector.getSelectionModel().getSelectedItem();
			System.out.println(selectedEntry);
			switchScene(selectedEntry);

		});
	}

	public void switchScene(Scene scene) {
		if (scene.equals(Scene.MAINSCENE)) {

			try {
				FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("view/GamePane.fxml"));
				Pane gamePane;
				gamePane = (Pane) loader.load();
				mainPane.setCenter(gamePane);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			mainStage.setWidth(420);
		} else if(scene.equals(Scene.HIGHSCORE)){
			HighscorePane highscorePane = new HighscorePane(new Highscore(), 4);
			
			mainPane.setCenter(highscorePane);
			
			mainStage.setWidth(800);
		}
	} 

	public void setStage(Stage stage){
		this.mainStage  = stage;
	}
}
