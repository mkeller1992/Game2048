package ch.bfh.game2048;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			GridPane root = (GridPane) FXMLLoader.load(getClass().getResource("view/MainUI.fxml"));
			Scene scene = new Scene(root, 400, 400);
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

	}
}
