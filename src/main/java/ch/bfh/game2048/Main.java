package ch.bfh.game2048;

import ch.bfh.game2048.persistence.Config;
import ch.bfh.game2048.view.MainUIController;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application {

	@Override
	public void start(Stage primaryStage) {

		try {
			FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("view/MainUI.fxml"));
			BorderPane root = (BorderPane) loader.load();
			Scene mainScene = new Scene(root, 420, 520);
			Stage stage = primaryStage;
			stage.setTitle("2048 by M&M");

			((MainUIController) loader.getController()).setStage(primaryStage);
			
			primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			    @Override
			    public void handle(WindowEvent event) {
			        Config.getInstance().write();
			    }
			});

			stage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("images/meteor.png")));
			mainScene.getStylesheets().add(getClass().getClassLoader().getResource("css/application.css").toExternalForm());
			primaryStage.setScene(mainScene);
			primaryStage.show();

			((MainUIController) loader.getController()).switchScene(ch.bfh.game2048.view.model.Scene.MAINSCENE);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	public static void main(String[] args) {
		launch(args);

		// Tryout blah = new Tryout();
		// blah.tryOut();

	}

}
