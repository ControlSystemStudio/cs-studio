package org.csstudio.trends.databrowser2.persistence;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

/**
 * XML DTO for {@link Color}.
 * 
 * @author Fred Arnaud (Sopra Group) - ITER
 */
public class ColorSettings {

	private int red;
	private int green;
	private int blue;

	public static ColorSettings fromSWT(Color swtColor) {
		ColorSettings settings = new ColorSettings();
		if (swtColor == null) {
			settings.setRed(0);
			settings.setGreen(0);
			settings.setBlue(0);
		} else {
			settings.setRed(swtColor.getRed());
			settings.setGreen(swtColor.getGreen());
			settings.setBlue(swtColor.getBlue());
		}
		return settings;
	}

	public Color toSWT() {
		return new Color(Display.getDefault(), red, green, blue);
	}

	public int getRed() {
		return red;
	}

	public void setRed(int red) {
		this.red = red;
	}

	public int getGreen() {
		return green;
	}

	public void setGreen(int green) {
		this.green = green;
	}

	public int getBlue() {
		return blue;
	}

	public void setBlue(int blue) {
		this.blue = blue;
	}

}
