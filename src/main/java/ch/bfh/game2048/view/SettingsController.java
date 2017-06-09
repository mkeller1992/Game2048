package ch.bfh.game2048.view;

import ch.bfh.game2048.ai.strategies.Strategy;
import ch.bfh.game2048.persistence.Config;
import ch.bfh.game2048.view.model.BoardSizes;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

public class SettingsController {

	@FXML	
	ChoiceBox<BoardSizes> chbBoardSize;
	
	@FXML	
	ChoiceBox<String> chbUiTheme;
	
	@FXML	
	ChoiceBox<Strategy> chbHintStrategy;
	
	@FXML	
	ChoiceBox<String> chbOnlineScore;
	
	@FXML
	TextField txtPlayerName;
	
	
	
	@FXML
	private void initialize(){
		Config conf = Config.getInstance();
									
		chbBoardSize.getItems().setAll(BoardSizes.values());
		BoardSizes boardSize = BoardSizes.findStateByBoardSize(conf.getPropertyAsInt("boardSize"));
		chbBoardSize.getSelectionModel().select(boardSize);
	
		chbBoardSize.setOnAction(ae -> { 
			int bs = chbBoardSize.getSelectionModel().getSelectedItem().getBoardSize();								
			conf.setProperty("boardSize", bs);			
		});
		
		
		chbHintStrategy.getItems().setAll(Strategy.values());
		Strategy strategy = Strategy.findStateByDescription(conf.getPropertyAsString("strategy"));
		chbHintStrategy.getSelectionModel().select(strategy);
	
		chbHintStrategy.setOnAction(ae -> { 
			String strat = chbHintStrategy.getSelectionModel().getSelectedItem().getDescription();								
			conf.setProperty("strategy", strat);			
		});
		
		
		
		
	}
	
	
	
	
	
}
