package ch.bfh.game2048.view;

import ch.bfh.game2048.persistence.Config;
import javafx.geometry.Bounds;
import javafx.scene.control.Label;
import javafx.scene.text.Text;

public class SuperLabel extends Label {

	int tileValue;
	double labelLength;
	String borderColor;
	int borderWidth;

	public SuperLabel(int tileValue, double labelLength) {

		this.tileValue = tileValue;
		this.labelLength = labelLength;
		this.borderColor = Config.getInstance().getPropertyAsString("colorOfTileBorder");
		this.borderWidth = Config.getInstance().getPropertyAsInt("widthOfTileBorder");
		scaleLabelText(scaleLabelText(new Text("" + tileValue)));
		setLabelStyle();
	}

	public void setTileNumber(int tileValue) {

		this.tileValue = tileValue;
		
		if (tileValue == 0) {
			this.setGraphic(new Text(""));
		} else {
			this.setGraphic(scaleLabelText(new Text("" + tileValue)));
		}
		
		setLabelStyle();
	}

	private Text scaleLabelText(Text tileText) {

		tileText.setFill(UITheme.valueOf(tileValue).getFontColor());

		Bounds boundsOfText = tileText.getBoundsInLocal();

		double multiplicator = UITheme.valueOf(tileValue).getMultiplicator();

		double scaleX = multiplicator * (labelLength / boundsOfText.getWidth());
		double scaleY = multiplicator * (labelLength / boundsOfText.getHeight());

		double finalScale = Math.min(scaleX, scaleY);

		tileText.setScaleX(finalScale);
		tileText.setScaleY(finalScale);

		return tileText;
	}

	private void setLabelStyle() {

		String bgColor = UITheme.valueOf(tileValue).getBackgroundcolor();

		this.setStyle("-fx-font-weight: bold; -fx-border-color: rgb(" + borderColor + "); -fx-border-width: "
				+ borderWidth + "; -fx-background-color: rgb(" + bgColor + ");");
	}

}
