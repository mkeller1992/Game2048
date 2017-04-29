package ch.bfh.game2048;

import java.io.IOException;
import java.util.List;

import org.eclipse.egit.github.core.Gist;
import org.eclipse.egit.github.core.GistFile;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.GistService;

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
			Scene scene = new Scene(root, 400, 450);
			scene.getStylesheets().add(getClass().getResource("view/application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
//		launch(args);

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
		
		GitHubClient client = new GitHubClient().setOAuth2Token("76725398b1da39e06604a3d6e49497a414a6661b");
		GistService gistService = new GistService(client);
		try {						
			List<Gist> gists = gistService.getGists("Longomir");
			for(Gist gist : gists){
				System.out.println(gist.getId());		
			}
			
			Gist g = gistService.getGist("cc5c464caba2742d2194c971b5330251");
			GistFile f = g.getFiles().get("Highscore");
			
			f.setContent("HAHAHAHAHAHAHHAHA");
			
			g.getFiles().put("Highscore", f);
			gistService.updateGist(g);
			System.out.println(g.getUpdatedAt());
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		
		
		
//		"https://gist.github.com/Longomir/cc5c464caba2742d2194c971b5330251.js?FILENAME=highscore"

	}
}
