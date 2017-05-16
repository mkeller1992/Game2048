package ch.bfh.game2048;

import java.io.FileNotFoundException;

import javax.xml.bind.JAXBException;

import ch.bfh.game2048.model.Highscore;
import ch.bfh.game2048.persistence.Config;
import ch.bfh.game2048.persistence.ScoreHandler;
import ch.bfh.game2048.view.HighScoreDialogTest;
import ch.bfh.game2048.view.MainUIController;
import ch.bfh.game2048.view.Scenes;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application {

	static Stage stage;
	static Scene mainScene;
	static MainUIController controller;

	public static void switchScene(Scenes nextScene) throws FileNotFoundException, JAXBException {

		switch (nextScene) {
		case MAINSCENE:
			stage.setScene(mainScene);
			return;
		case HIGHSCORE:
			HighScoreDialogTest highScorePane = controller.getHighScorePane();
			Scene scene = new Scene(highScorePane, 420, 520);
			stage.setScene(scene);
			break;
		case SETTINGS:
			break;
		default:
			break;

		}
	}

	@Override
	public void start(Stage primaryStage) {

		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("view/MainUI.fxml"));		
			StackPane root = (StackPane) loader.load();
			this.controller = (MainUIController) loader.getController();			
			this.mainScene = new Scene(root, 420, 520);
			this.stage = primaryStage;
			mainScene.getStylesheets().add(getClass().getResource("view/application.css").toExternalForm());
			primaryStage.setScene(mainScene);
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);

		// GameEngine game = new GameEngine(4, null);
		//
		// game.print();
		// System.out.println();
		//
		// game.move(Direction.DOWN);
		//
		// game.print();
		// System.out.println();
		//
		// game.move(Direction.LEFT);
		//
		// game.print();
		//
		// System.exit(0);

		// ArrayList<GameStatistics> score = new ArrayList<GameStatistics>();
		//
		//
		// for(int i = 1; i<10 ;i++){
		// Player p = new Player();
		// p.setNickName("spieler"+i);
		// GameStatistics stat = new GameStatistics(p, 50*i, 3*i, 2*i, new
		// Date(), System.currentTimeMillis(),
		// System.currentTimeMillis()+5000*i, true);
		//
		//
		// score.add(stat);
		// }
		//
		//
		// GistUtil g = new GistUtil();
		//
		// String content = g.setHighScore(score);
		//
		// for(GameStatistics s : g.getHighScore(content)){
		// System.out.println(s.getPlayerNickname());
		// }
		// g.setHighScore("test?");

	}
}
