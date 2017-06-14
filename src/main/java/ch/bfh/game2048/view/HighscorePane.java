package ch.bfh.game2048.view;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import ch.bfh.game2048.persistence.Config;
import ch.bfh.game2048.view.model.BoardSizes;
import ch.bfh.game2048.view.model.HighscoreEntry;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 * 
 * Highscore-Pane displaying:
 * 
 * - Rank - Player-Name - Game-Score - Value of highest tile - Game-Duration -
 * Number of moves needed - Date/ time of play
 * 
 */

@SuppressWarnings("unchecked")
public class HighscorePane extends VBox {

	HBox titlePane;
	Label titleLabel;
	Text titleText;
	TextField filterField;

	TableView table;
	TableColumn<HighscoreEntry, Integer> tblRank;
	TableColumn<HighscoreEntry, String> tblName;
	TableColumn<HighscoreEntry, Integer> tblScore;
	TableColumn<HighscoreEntry, Integer> tblHighestTile;
	TableColumn<HighscoreEntry, String> tblDuration;
	TableColumn<HighscoreEntry, Integer> tblNumbOfMoves;
	TableColumn<HighscoreEntry, String> tblBoardSize;
	TableColumn<HighscoreEntry, String> tblDate;

	Config conf;
	EventHandler<Event> btnSolHandler;

	ComboBox<BoardSizes> boardSizeList;
	
	Button btnclearFilter;
	
	ObservableList<HighscoreEntry> masterList;
	FilteredList<HighscoreEntry> filteredList;
	
	
	
	/**
	 * 
	 * @param highscores
	 *            : contains a list with the score-objects
	 * @param boardSize
	 *            : number of rows resp. columns of the board
	 */

	public HighscorePane(List<HighscoreEntry> highscores) {
		conf = Config.getInstance();

		masterList = FXCollections.observableList(highscores);
		filteredList = new FilteredList<>(masterList);
		
		createTitlePane();
		createTable();

		/*
		 * add all components to the main-pane
		 */
		this.getChildren().addAll(titlePane, table);
		
		

	}

	private void createTitlePane() {
		/*
		 * Create and style the top-pane
		 */
		titlePane = new HBox();
		titlePane.setPrefSize(770, 100);
		titlePane.setPadding(new Insets(10, 10, 10, 10));
		titlePane.setSpacing(25);

		/*
		 * Create and style the title of highscore-screen
		 */
		titleLabel = new Label();
		titleText = new Text(conf.getPropertyAsString("highscoreListTitle"));
		titleText.setFont(Font.font(null, FontWeight.BOLD, 20));
		titleLabel.setGraphic(titleText);

		
	

		/*
		 * Include option "filter tableView by player-name"
		 */
		filterField = new TextField();
		filterField.setPromptText(conf.getPropertyAsString("highscoreListFilterText"));
		filterField.setOnKeyPressed(eh -> {
			filteredList = new FilteredList<>(masterList, highscoreEntry -> highscoreEntry.getNickname().toUpperCase().contains(filterField.getText().toUpperCase()));
			table.setItems(filteredList);
			table.refresh();
		});
		
		
		
		boardSizeList = new ComboBox<BoardSizes>();		
		boardSizeList.setPromptText(conf.getPropertyAsString("boardSizeListFilterText"));
		boardSizeList.getItems().addAll(BoardSizes.values());
		boardSizeList.setOnAction(ae -> {					
			BoardSizes size= boardSizeList.getSelectionModel().getSelectedItem();
			filteredList = new FilteredList<>(masterList, highscoreEntry -> highscoreEntry.getBoardsize() == size.getBoardSize());
			table.setItems(filteredList);
			table.refresh();
		});
		
		
		btnclearFilter = new Button(conf.getPropertyAsString("clearfilter.dialog"));
		btnclearFilter.setOnAction(ae -> {
			filterField.setText("");
			
			EventHandler<ActionEvent> handler = boardSizeList.getOnAction();
			boardSizeList.setOnAction(null);
			boardSizeList.getSelectionModel().clearSelection();			
			boardSizeList.setOnAction(handler);
			
			filteredList = new FilteredList<>(masterList);
			table.setItems(filteredList);
			table.refresh();
		});
		
		/*
		 * Add the components to the top-pane
		 */
		titlePane.getChildren().addAll(titleLabel, filterField, boardSizeList, btnclearFilter);
	}

	private void createTable() {
		/*
		 * Create the tableView
		 */
		table = new TableView<>();
		table.setPrefHeight(450);
		table.setMaxHeight(450);

		/*
		 * Specify the column-titles
		 */
		tblRank = new TableColumn<HighscoreEntry, Integer>(conf.getPropertyAsString("colTitleRank.dialog"));
		tblName = new TableColumn<HighscoreEntry, String>(conf.getPropertyAsString("colTitleName.dialog"));
		tblScore = new TableColumn<HighscoreEntry, Integer>(conf.getPropertyAsString("colTitleScore.dialog"));
		tblHighestTile = new TableColumn<HighscoreEntry, Integer>(conf.getPropertyAsString("colTitleMaxTile.dialog"));
		tblDuration = new TableColumn<HighscoreEntry, String>(conf.getPropertyAsString("colTitleDuration.dialog"));
		tblNumbOfMoves = new TableColumn<HighscoreEntry, Integer>(conf.getPropertyAsString("colTitleNumbOfMoves.dialog"));
		tblBoardSize = new TableColumn<HighscoreEntry, String>(conf.getPropertyAsString("colTitleBoardSize.dialog"));
		tblDate = new TableColumn<HighscoreEntry, String>(conf.getPropertyAsString("colTitleDateTime.dialog"));

		/*
		 * Specify which column displays which property of GameStatistics
		 */
		tblRank.setCellValueFactory(new PropertyValueFactory<HighscoreEntry, Integer>("Rank"));
		tblName.setCellValueFactory(new PropertyValueFactory<HighscoreEntry, String>("Nickname"));
		tblScore.setCellValueFactory(new PropertyValueFactory<HighscoreEntry, Integer>("score"));
		tblHighestTile.setCellValueFactory(new PropertyValueFactory<HighscoreEntry, Integer>("highestValue"));
		tblDuration.setCellValueFactory(cellData ->{
			SimpleStringProperty property = new SimpleStringProperty();
			long duration = cellData.getValue().getDuration();
			long h =TimeUnit.MILLISECONDS.toHours(duration);
			long m =TimeUnit.MILLISECONDS.toMinutes(duration);
			long s =TimeUnit.MILLISECONDS.toSeconds(duration);
			property.setValue(String.format("%02d:%02d:%02d", h,m,s));
			return property;
		});
		tblNumbOfMoves.setCellValueFactory(new PropertyValueFactory<HighscoreEntry, Integer>("numOfMoves"));
		tblDate.setCellValueFactory(cellData ->{
			SimpleStringProperty property = new SimpleStringProperty();

			DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
			formatter.format(new Date(cellData.getValue().getTimestamp()));
			property.setValue(formatter.format(new Date(cellData.getValue().getTimestamp())));
			return property;
		});
		tblBoardSize.setCellValueFactory(cellData ->{
			SimpleStringProperty property = new SimpleStringProperty();

			String boardSize = BoardSizes.findStateByBoardSize(cellData.getValue().getBoardsize()).toString();
			property.setValue(boardSize);
			return property;
		});

		/*
		 * Populate the tableView with the score-list entries
		 */	
	    table.setItems(filteredList);	    
	 	
		/*
		 * Add all columns to the tableView
		 */
		table.getColumns().addAll(tblRank, tblName, tblScore, tblHighestTile, tblDuration, tblNumbOfMoves, tblBoardSize, tblDate);

		/*
		 * Specify the width and the alignment of the columns
		 */
		tblRank.setPrefWidth(50);
		tblName.setPrefWidth(150);
		tblScore.setPrefWidth(80);
		tblScore.setStyle("-fx-alignment: CENTER-RIGHT;");
		tblHighestTile.setPrefWidth(80);
		tblHighestTile.setStyle("-fx-alignment: CENTER-RIGHT;");
		tblDuration.setPrefWidth(80);
		tblDuration.setStyle("-fx-alignment: CENTER-RIGHT;");
		tblNumbOfMoves.setPrefWidth(80);
		tblNumbOfMoves.setStyle("-fx-alignment: CENTER-RIGHT;");
		tblDate.setPrefWidth(140);
		tblDate.setStyle("-fx-alignment: CENTER;");
	}




}
