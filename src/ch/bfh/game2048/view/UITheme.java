package ch.bfh.game2048.view;

import java.util.HashMap;
import java.util.Map;

public enum UITheme {
	V0(0, "255, 255, 255", "204,192,180"),
	V2(2,  "119,110,101", "238,228,218"),
	V4(4, "119,110,101", "237,224,200"),
	V8(8, "249,246,242", "242,177,121"),
	V16(16, "249,246,242", "245,149,99"),
	V32(32, "249,246,242", "246,124,95"),
	V64(64, "249,246,242", "246,94,59"),
	V128(128, "249,246,242", "237,207,114"),
	V256(256, "249,246,242", "237,204,97"),
	V512(512, "249,246,242", "237,200,80"),
	V1024(1024, "249,246,242", "237,197,63"),
	V2048(2048, "249,246,242", "249,246,242");

	private int value;
	private String fontColor;
	private String backgroundcolor;
	
	private static Map<Integer, UITheme> map = new HashMap<Integer, UITheme>();

	    static {
	        for (UITheme theme : UITheme.values()) {
	            map.put(theme.value, theme);
	        }
	    }
	private UITheme(int value, String fontColor, String backgroundcolor) {
		this.value = value;
		this.fontColor = fontColor;
		this.backgroundcolor = backgroundcolor;
	}
	
    public static UITheme valueOf(int value) {
        return map.get(value);
    }

	public String getFontColor() {
		return fontColor;
	}

	public void setFontColor(String fontColor) {
		this.fontColor = fontColor;
	}

	public String getBackgroundcolor() {
		return backgroundcolor;
	}

	public void setBackgroundcolor(String backgroundcolor) {
		this.backgroundcolor = backgroundcolor;
	}

    
	
}



