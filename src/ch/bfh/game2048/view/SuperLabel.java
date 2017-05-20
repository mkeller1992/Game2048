package ch.bfh.game2048.view;

import ch.bfh.game2048.persistence.Config;
import javafx.geometry.Bounds;
import javafx.scene.control.Label;
import javafx.scene.text.Text;

/**
 * SuperLabel depicts a tile on the game-board
 *
 */

public class SuperLabel extends Label {

	int tileValue;
	double labelLength;
	String borderColor;
	int borderWidth;
	

	/**
	 * @param tileValue
	 *            number to be shown on the tile-label
	 * @param labelLength
	 *            side length of tile-label in pixel
	 */

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
	
	/**
	 * 
	 * Scale label-text based on:
	 * - label-size
	 * - tile-value's amount of digits
	 * 
	 * @param tileText
	 * @return
	 */

	private Text scaleLabelText(Text tileText) {

		/*
		 * set the font-color
		 */		
		tileText.setFill(UITheme.valueOf(tileValue).getFontColor());

		/*
		 * get text-objects height and width
		 */
		Bounds boundsOfText = tileText.getBoundsInLocal();

		/*
		 * multiplicator ( < 1) is based on the tile-number's amount of digits
		 */		
		double multiplicator = UITheme.valueOf(tileValue).getMultiplicator();

		/*
		 * factorX = temporary factor to scale text-width
		 * factorY = temporary factor to scale text-height
		 */
		
		double factorX = multiplicator * (labelLength / boundsOfText.getWidth());
		double factorY = multiplicator * (labelLength / boundsOfText.getHeight());

		/*
		 * Use the smaller factor to scale text-height AND text-width:
		 * - prevents text from exceeding tile-border
		 * - maintains the height-to-width ratio
		 */
		
		double finalScale = Math.min(factorX, factorY);
		tileText.setScaleX(finalScale);
		tileText.setScaleY(finalScale);

		return tileText;
	}
	
	/**
	 * Set a label's...
	 * - Font-weight
	 * - Background-color
	 * - Border-color
	 * - Border-width
	 */

	private void setLabelStyle() {

		String bgColor = UITheme.valueOf(tileValue).getBackgroundcolor();

		this.setStyle("-fx-font-weight: bold; -fx-border-color: rgb(" + borderColor + "); -fx-border-width: " + borderWidth + "; -fx-background-color: rgb(" + bgColor + ");");
	}

}
