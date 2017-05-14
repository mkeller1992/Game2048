package ch.bfh.game2048.view;

import java.util.HashMap;
import java.util.Map;

import javafx.scene.paint.Color;

public enum UITheme {
	V0(0, Color.rgb(255, 255, 255), "204,192,180"),
	V2(2,  Color.rgb(119,110,101), "238,228,218"),
	V4(4, Color.rgb(119,110,101), "237,224,200"),
	V8(8, Color.rgb(249,246,242), "242,177,121"),
	V16(16, Color.rgb(249,246,242), "245,149,99"),
	V32(32, Color.rgb(249,246,242), "246,124,95"),
	V64(64, Color.rgb(249,246,242), "246,94,59"),
	V128(128, Color.rgb(249,246,242), "237,207,114"),
	V256(256, Color.rgb(249,246,242), "237,204,97"),
	V512(512, Color.rgb(249,246,242), "237,200,80"),
	V1024(1024, Color.rgb(249,246,242), "237,197,63"),	
	V2048(2048, Color.rgb(249,246,242), "237,197,63"),
	DEFAULT(-1, Color.rgb(249,246,242), "247,217,83");
	

	private int value;
	private Color fontColor;
	private String backgroundColor;
	
	private static Map<Integer, UITheme> map = new HashMap<Integer, UITheme>();

	static {
		for (UITheme theme : UITheme.values()) {
			map.put(theme.value, theme);
		}
	}
	    
	private UITheme(int value, Color fontColor, String backgroundColor) {
		this.value = value;
		this.fontColor = fontColor;
		this.backgroundColor = backgroundColor;

	}
	
    public static UITheme valueOf(int value) {
        return (map.get(value) == null ? DEFAULT : map.get(value));
    }

	public Color getFontColor() {
		return fontColor;
	}

	public void setFontColor(Color fontColor) {
		this.fontColor = fontColor;
	}

	public String getBackgroundcolor() {
		return backgroundColor;
	}

	public void setBackgroundcolor(String backgroundcolor) {
		this.backgroundColor = backgroundcolor;
	}

    
	
}



