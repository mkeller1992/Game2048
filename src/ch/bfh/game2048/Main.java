package ch.bfh.game2048;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			BorderPane root = (BorderPane) FXMLLoader.load(getClass().getResource("view/MainUI.fxml"));
			Scene scene = new Scene(root, 420, 520);
			scene.getStylesheets().add(getClass().getResource("view/application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);

//		GameEngine game = new GameEngine(4, null);
//
//		game.print();
//		System.out.println();
//
//		game.move(Direction.DOWN);
//
//		game.print();
//		System.out.println();
//
//		game.move(Direction.LEFT);
//
//		game.print();
//
//		System.exit(0);
		

		
	
//		ArrayList<GameStatistics> score = new ArrayList<GameStatistics>();
//		
//		
//		for(int i = 1; i<10 ;i++){
//			Player p = new Player();
//			p.setNickName("spieler"+i);
//			GameStatistics	stat = new GameStatistics(p, 50*i, 3*i, 2*i, new Date(), System.currentTimeMillis(), System.currentTimeMillis()+5000*i, true);
//			
//					
//			score.add(stat);
//		}
//		
//
//		GistUtil g = new GistUtil();
//		
//		String content = g.setHighScore(score);
//		
//		for(GameStatistics s : g.getHighScore(content)){
//			System.out.println(s.getPlayerNickname());
//		}
//		g.setHighScore("test?");
		
	}
}
