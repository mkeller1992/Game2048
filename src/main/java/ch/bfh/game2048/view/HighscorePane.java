package ch.bfh.game2048.view;

import java.io.FileNotFoundException;
import java.util.List;

import javax.xml.bind.JAXBException;

import ch.bfh.game2048.Main;
import ch.bfh.game2048.model.GameStatistics;
import ch.bfh.game2048.model.Highscore;
import ch.bfh.game2048.persistence.Config;
import ch.bfh.game2048.view.model.BoardSizes;
import ch.bfh.game2048.view.model.Scene;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 * 
 * Highscore-Pane displaying:
 * 
 * - Rank
 * - Player-Name
 * - Game-Score
 * - Value of highest tile
 * - Game-Duration
 * - Number of moves needed
 * - Date/ time of play
 * 
 */

@SuppressWarnings("unchecked")
public class HighscorePane extends VBox {

	HBox titlePane;
	Label titleLabel;
	Text titleText;
	TextField filterField;

	TableView table;
	TableColumn tblRank;
	TableColumn tblName;
	TableColumn tblScore;
	TableColumn tblHighestTile;
	TableColumn tblDuration;
	TableColumn tblNumbOfMoves;
	TableColumn tblDate;

	HBox bottomPanel;
	Button okayButton;

	Highscore highscores;
	Config conf;
	EventHandler<Event> btnSolHandler;

	ComboBox<BoardSizes> boardSizeList;
	ObservableList<GameStatistics> masterList;
	FilteredList<GameStatistics> filteredData;

	/**
	 * 
	 * @param highscores
	 *            : contains a list with the score-objects
	 * @param boardSize
	 *            : number of rows resp. columns of the board
	 */

	@SuppressWarnings({ "rawtypes" })
	public HighscorePane(Highscore highscores, int boardSize) {

		this.highscores = highscores;
		conf = Config.getInstance();

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
		 * Create and set the ComboBox with list of board-sizes:
		 */
		boardSizeList = new ComboBox<BoardSizes>();
		boardSizeList.getItems().setAll(BoardSizes.values());

		BoardSizes sizeToBeSelected = BoardSizes.findStateByBoardSize(boardSize);
		boardSizeList.getSelectionModel().select(sizeToBeSelected);

		boardSizeList.setOnAction((event) -> {
			setContentFromLists();
		});

		/*
		 * Include option "filter tableView by player-name"
		 */
		filterField = new TextField();
		filterField.setPromptText(conf.getPropertyAsString("highscoreListFilterText"));
		setUpNameFilter();

		/*
		 * Add the components to the top-pane
		 */
		titlePane.getChildren().addAll(titleLabel, boardSizeList, filterField);

		/*
		 * Create the tableView
		 */
		table = new TableView<>();
		table.setPrefHeight(500);
		/*
		 * Populate the tableView with the score-list entries
		 */
		setContentFromLists();

		/*
		 * Specify the column-titles
		 */
		tblRank = new TableColumn(conf.getPropertyAsString("colTitleRank.dialog"));
		tblName = new TableColumn(conf.getPropertyAsString("colTitleName.dialog"));
		tblScore = new TableColumn(conf.getPropertyAsString("colTitleScore.dialog"));
		tblHighestTile = new TableColumn(conf.getPropertyAsString("colTitleMaxTile.dialog"));
		tblDuration = new TableColumn(conf.getPropertyAsString("colTitleDuration.dialog"));
		tblNumbOfMoves = new TableColumn(conf.getPropertyAsString("colTitleNumbOfMoves.dialog"));
		tblDate = new TableColumn(conf.getPropertyAsString("colTitleDateTime.dialog"));

		/*
		 * Specify which column displays which property of GameStatistics
		 */
		tblRank.setCellValueFactory(new PropertyValueFactory<GameStatistics, Integer>("rankAsString"));
		tblName.setCellValueFactory(new PropertyValueFactory<GameStatistics, String>("PlayerName"));
		tblScore.setCellValueFactory(new PropertyValueFactory<GameStatistics, Integer>("score"));
		tblHighestTile.setCellValueFactory(new PropertyValueFactory<GameStatistics, Integer>("highestValue"));
		tblDuration.setCellValueFactory(new PropertyValueFactory<GameStatistics, Long>("formattedDuration"));
		tblNumbOfMoves.setCellValueFactory(new PropertyValueFactory<GameStatistics, Integer>("amountOfMoves"));
		tblDate.setCellValueFactory(new PropertyValueFactory<GameStatistics, String>("formattedDate"));

		/*
		 * Add all columns to the tableView
		 */
		table.getColumns().addAll(tblRank, tblName, tblScore, tblHighestTile, tblDuration, tblNumbOfMoves, tblDate);

		/*
		 * Specify the width and the alignment of the columns
		 */
		tblRank.setPrefWidth(50);
		tblName.setPrefWidth(150);
		tblScore.setPrefWidth(90);
		tblScore.setStyle("-fx-alignment: CENTER-RIGHT;");
		tblHighestTile.setPrefWidth(90);
		tblHighestTile.setStyle("-fx-alignment: CENTER-RIGHT;");
		tblDuration.setPrefWidth(90);
		tblDuration.setStyle("-fx-alignment: CENTER-RIGHT;");
		tblNumbOfMoves.setPrefWidth(90);
		tblNumbOfMoves.setStyle("-fx-alignment: CENTER-RIGHT;");
		tblDate.setPrefWidth(170);
		tblDate.setStyle("-fx-alignment: CENTER;");

		/*
		 * Assemble the bottom-panel with the "Back to Game"-Button
		 */
		bottomPanel = new HBox();
		bottomPanel.setAlignment(Pos.CENTER_RIGHT);
		bottomPanel.setPadding(new Insets(10, 10, 10, 10));
		okayButton = new Button(conf.getPropertyAsString("backToGame.button"));
		okayButton.addEventHandler(MouseEvent.MOUSE_CLICKED, createSolButtonHandler());

		/*
		 * add the components to the bottom-pane
		 */
		bottomPanel.getChildren().addAll(okayButton);

		/*
		 * add all components to the main-pane
		 */
		this.getChildren().addAll(titlePane, table, bottomPanel);
	}

	/*
	 * Setup "filter list by player-name" -functionality
	 */
	private void setUpNameFilter() {

		// Set the filter-predicate whenever the filter changes.
		filterField.textProperty().addListener((observable, oldValue, newValue) -> {
			filteredData.setPredicate(player -> {

				// If filter text is empty, display all score-entries.
				if (newValue == null || newValue.isEmpty()) {
					return true;
				}

				// Compare player-nickname with the nickname-entries in the tableview
				String lowerCaseFilter = newValue.toLowerCase();

				if (player.getPlayerName().toLowerCase().contains(lowerCaseFilter)) {
					return true; // Filter matches first name.
				}
				return false; // Does not match.
			});
		});
	}

	/*
	 * Populate the tableView with the items from the score-list
	 */

	private void setContentFromLists() {

		BoardSizes selectedEntry = boardSizeList.getSelectionModel().getSelectedItem();
		List<GameStatistics> baseScoreList = highscores.getFilteredScoreList(selectedEntry.getBoardSize());

		// Get number of ranks to be displayed
		int numberOfScoreEntriesToShow = conf.getPropertyAsInt("maxNumberOfScores");

		// Sort, set ranks and resize score-list
		highscores.sortSetRanksAndResizeList(baseScoreList, numberOfScoreEntriesToShow);

		// convert list to a format which can be filtered and added to tableView
		masterList = FXCollections.observableArrayList(baseScoreList);
		filteredData = new FilteredList<>(masterList, p -> true);
		table.setItems(filteredData);
		table.setEditable(true);
	}

	/*
	 * Switch back to main-scene upon button-click
	 */

	private EventHandler<Event> createSolButtonHandler() {
		btnSolHandler = new EventHandler<Event>() {

			@Override
			public void handle(Event event) {

//				try {
//					Main.switchScene(Scenes.MAINSCENE);
//				} catch (FileNotFoundException | JAXBException e) {
//					e.printStackTrace();
//				}

			}
		};
		return btnSolHandler;
	}
}
