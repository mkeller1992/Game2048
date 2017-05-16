package ch.bfh.game2048.view;

import java.io.FileNotFoundException;
import java.util.List;

import javax.xml.bind.JAXBException;

import ch.bfh.game2048.Main;
import ch.bfh.game2048.model.GameStatistics;
import ch.bfh.game2048.persistence.Config;
import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class HighScoreDialogTest extends VBox {

	EventHandler<Event> btnSolHandler;
	Button okayButton;
	Config conf;

	@SuppressWarnings("rawtypes")
	public HighScoreDialogTest(String title, List<GameStatistics> highScores) {

		conf = Config.getInstance();

		// this.setTitle(title);
		// this.setHeaderText(null);
		//
		// this.getDialogPane().setPrefSize(770, 500);

		TableView table = new TableView<>();

		TableColumn tblRank = new TableColumn(conf.getPropertyAsString("colTitleRank.dialog"));
		TableColumn tblName = new TableColumn(conf.getPropertyAsString("colTitleName.dialog"));
		TableColumn tblScore = new TableColumn(conf.getPropertyAsString("colTitleScore.dialog"));
		TableColumn tblHighestTile = new TableColumn(conf.getPropertyAsString("colTitleMaxTile.dialog"));
		TableColumn tblDuration = new TableColumn(conf.getPropertyAsString("colTitleDuration.dialog"));
		TableColumn tblNumbOfMoves = new TableColumn(conf.getPropertyAsString("colTitleNumbOfMoves.dialog"));
		TableColumn tblDate = new TableColumn(conf.getPropertyAsString("colTitleDateTime.dialog"));

		tblRank.setCellValueFactory(new PropertyValueFactory<GameStatistics, Integer>("rankAsString"));
		tblName.setCellValueFactory(new PropertyValueFactory<GameStatistics, String>("playerNickname"));
		tblScore.setCellValueFactory(new PropertyValueFactory<GameStatistics, Integer>("score"));
		tblHighestTile.setCellValueFactory(new PropertyValueFactory<GameStatistics, Integer>("highestValue"));
		tblDuration.setCellValueFactory(new PropertyValueFactory<GameStatistics, Long>("formattedDuration"));
		tblNumbOfMoves.setCellValueFactory(new PropertyValueFactory<GameStatistics, Integer>("amountOfMoves"));
		tblDate.setCellValueFactory(new PropertyValueFactory<GameStatistics, String>("formattedDate"));

		table.getColumns().addAll(tblRank, tblName, tblScore, tblHighestTile, tblDuration, tblNumbOfMoves, tblDate);

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

		tblScore.setSortType(TableColumn.SortType.DESCENDING);

		table.setItems(FXCollections.observableArrayList(highScores));
		table.setEditable(true);

		// this.getDialogPane().getButtonTypes().addAll(ButtonType.OK);
		// this.getDialogPane().setContent(table);
		table.getSortOrder().add(tblScore);

		okayButton = new Button("OK");
		okayButton.addEventHandler(MouseEvent.MOUSE_CLICKED, createSolButtonHandler());

		this.setSpacing(50);
		this.setPadding(new Insets(30, 30, 30, 30));
		this.getChildren().addAll(table, okayButton);

	}

	public EventHandler<Event> createSolButtonHandler() {
		btnSolHandler = new EventHandler<Event>() {

			@Override
			public void handle(Event event) {

				try {
					Main.switchScene(Scenes.MAINSCENE);
				} catch (FileNotFoundException | JAXBException e) {
					e.printStackTrace();
				}

			}
		};
		return btnSolHandler;
	}

}
