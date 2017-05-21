package ch.bfh.game2048;

import com.google.gson.Gson;
import com.guigarage.flatterfx.FlatterFX;

import ch.bfh.game2048.model.GameStatistics;
import ch.bfh.game2048.view.MainUIController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class Main extends Application {

	static Stage stage;
	static Scene mainScene;
	static MainUIController controller;

	@Override
	public void start(Stage primaryStage) {

		try {
			FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("view/MainUI.fxml"));
			BorderPane root = (BorderPane) loader.load();
//			this.controller = (GamePaneController) loader.getController();
			this.mainScene = new Scene(root, 420, 520);
			this.stage = primaryStage;
			stage.setTitle("2048 by M&M");						
			
			((MainUIController) loader.getController()).setStage(primaryStage);
			
			stage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("images/meteor.png")));
			mainScene.getStylesheets().add(getClass().getClassLoader().getResource("css/application.css").toExternalForm());
			primaryStage.setScene(mainScene);
			primaryStage.show();
			
//			FlatterFX.style();
			
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
		
		Tryout blah = new Tryout();
		blah.tryOut();

	}
	

	public static Stage getStage() {
		return stage;
	}

	/**
	 * Switching from one screen to the other --> method will be replaced
	 * 
	 */

//	public static void switchScene(Scenes nextScene) throws FileNotFoundException, JAXBException {
//
//		switch (nextScene) {
//		case MAINSCENE:
//			stage.setScene(mainScene);
//			return;
//		case HIGHSCORE:
//			HighscorePane highScorePane = controller.getHighScorePane();
//			Scene scene = new Scene(highScorePane, 770, 550);
//			stage.setScene(scene);
//			centerStage();
//			break;
//		case SETTINGS:
//			break;
//		default:
//			break;
//
//		}
//	}

	/**
	 * moves the stage to the center of the screen
	 * --> useful in case of switching between scenes of different sizes
	 * 
	 */

	public static void centerStage() {
		Stage stage = Main.getStage();
		Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
		stage.setX((screenBounds.getWidth() - stage.getWidth()) / 2);
		stage.setY((screenBounds.getHeight() - stage.getHeight()) / 2);

	}


		
	
}
