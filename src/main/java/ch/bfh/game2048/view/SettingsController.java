package ch.bfh.game2048.view;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import ch.bfh.game2048.ai.strategies.Strategy;
import ch.bfh.game2048.persistence.Config;
import ch.bfh.game2048.view.model.BoardSizes;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;


/**
 * Settings-Screen
 * 
 * Including following settings:
 * 
 * - Choice of board-size
 * - Choice of hint-strategy
 * - Player-Name
 *
 */


public class SettingsController {

	@FXML
	ChoiceBox<BoardSizes> chbBoardSize;

	@FXML
	ChoiceBox<Strategy> chbHintStrategy;
	
	@FXML
	ChoiceBox<Integer> chbAiSpeed;

	@FXML
	TextField txtPlayerName;

	@FXML
	private void initialize() {

		Config conf = Config.getInstance();

		// Sets up the Board-Size Choice-Box:

		chbBoardSize.getItems().setAll(BoardSizes.values());
		BoardSizes boardSize = BoardSizes.findStateByBoardSize(conf.getPropertyAsInt("boardSize"));
		chbBoardSize.getSelectionModel().select(boardSize);

		chbBoardSize.setOnAction(ae -> {
			int bs = chbBoardSize.getSelectionModel().getSelectedItem().getBoardSize();
			conf.setProperty("boardSize", bs);
		});

		// Sets up the Hint-Strategy Choice-Box:

		chbHintStrategy.getItems().setAll(Strategy.values());
		Strategy strategy = Strategy.findStateByDescription(conf.getPropertyAsString("strategy"));
		chbHintStrategy.getSelectionModel().select(strategy);

		chbHintStrategy.setOnAction(ae -> {
			String strat = chbHintStrategy.getSelectionModel().getSelectedItem().getDescription();
			conf.setProperty("strategy", strat);
		});

		// Sets the previously entered player name (which was stored in properties):	
		txtPlayerName.setText(conf.getPropertyAsString("playerName"));
		
		// Saves the player-name to the properties whenever the textfield-content is getting changed:
		
		txtPlayerName.textProperty().addListener(new ChangeListener<String>() {
		    @Override
		    public void changed(ObservableValue<? extends String> observable,
		            String oldValue, String newValue) {

		    	conf.setProperty("playerName", newValue);
		    }
		});
		
		// AI-Player speed
		int[] values = {25, 50, 75, 100, 150, 500};
		chbAiSpeed.getItems().setAll(Arrays.stream(values).boxed().collect(Collectors.toList()));
		chbAiSpeed.getSelectionModel().select(conf.getPropertyAsInt("aiSpeed"));
		chbAiSpeed.setOnAction(ae -> {
			Integer aiSpeed= chbAiSpeed.getSelectionModel().getSelectedItem();
			conf.setProperty("aiSpeed", aiSpeed);
		});

	}
}
