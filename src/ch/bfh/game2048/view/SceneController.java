package ch.bfh.game2048.view;

import java.util.HashMap;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class SceneController {

	    private HashMap<String, Pane> screenMap = new HashMap<>();
	    private Scene main;
	    
	    public SceneController(){
	    	
	    }

	    public SceneController(Scene main) {
	        this.main = main;
	    }

	    protected void addScreen(String name, Pane pane){
	         screenMap.put(name, pane);
	    }

	    protected void removeScreen(String name){
	        screenMap.remove(name);
	    }

	    protected void show(String name){
	    	Stage stage = (Stage) main.getWindow();
	    	Scene scene = new Scene(screenMap.get(name),420,520);
	        stage.setScene(scene);
	    }
	    
	    protected void showMain(){
	    	Stage stage = (Stage) main.getWindow();
	    	stage.setScene(main);
	    }
	

}
