package ch.bfh.game2048.view;

import javafx.geometry.Bounds;
import javafx.scene.control.Label;
import javafx.scene.text.Text;

public class SuperLabel extends Label {

	int tileValue;

	public SuperLabel(int tileValue) {

		this.tileValue = tileValue;
		setLabelText(tileValue);
		setLabelStyle();

	}

	public void setTileNumber(int tileValue) {

		this.tileValue = tileValue;
		setLabelText(tileValue);
		setLabelStyle();

	}

	private void setLabelText(int tileValue) {

		if (tileValue == 0) {
			this.setGraphic(new Text(""));
			return;
		}

		Text tileText = new Text("" + tileValue);
		tileText.setFill(UITheme.valueOf(tileValue).getFontColor());

		Bounds boundsOfText = tileText.getBoundsInLocal();
		Bounds boundsOfLabel = this.getBoundsInLocal();

		double multiplicator = UITheme.valueOf(tileValue).getMultiplicator();

		double scaleX = multiplicator * (boundsOfLabel.getWidth()) / boundsOfText.getWidth();
		double scaleY = multiplicator * (boundsOfLabel.getHeight()) / boundsOfText.getHeight();

		double finalScale = Math.min(scaleX, scaleY);

		tileText.setScaleX(finalScale);
		tileText.setScaleY(finalScale);

		this.setGraphic(tileText);

	}

	private void setLabelStyle() {

		String bgColor = UITheme.valueOf(tileValue).getBackgroundcolor();
		String borderColor = UITheme.valueOf(tileValue).getBorderColor();
		int borderWidth = UITheme.valueOf(tileValue).getBorderWidth();		
		
		this.setStyle("-fx-font-weight: bold; -fx-border-color: rgb(" + borderColor
				+ "); -fx-border-width: "+borderWidth+"; -fx-background-color: rgb(" + bgColor + ");");
	}

}
