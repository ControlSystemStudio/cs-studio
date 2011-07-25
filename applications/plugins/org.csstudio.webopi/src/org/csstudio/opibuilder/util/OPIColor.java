/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.util;

import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

/**
 * The dedicated color type which supports predefined color name in OPI builder
 * color file. If the color name doesn't exist in the color file, the color
 * value is null.
 * 
 * @author Xihui Chen
 * 
 */
public class OPIColor {

	private String colorName;

	private RGB colorValue;

	private boolean preDefined = false;

	public OPIColor(String colorName) {
		this.colorName = colorName;
		this.colorValue = MediaService.getInstance().getColor(colorName);
		preDefined = true;
	}

	public OPIColor(RGB rgb) {
		setColorValue(rgb);
	}

	public OPIColor(int red, int green, int blue) {
		this(new RGB(red, green, blue));
	}

	public OPIColor(String name, RGB rgb, boolean predefined) {
		this.colorName = name;
		this.colorValue = rgb;
		this.preDefined = predefined;
	}

	/**
	 * @return the name of color if it is a predefined color macro; otherwise,
	 *         it is a string of the RGB values.
	 */
	public String getColorName() {
		return colorName;
	}

	/**
	 * @return the rgb value of the color. null if the predefined color does not
	 *         exist.
	 */
	public RGB getRGBValue() {
		return colorValue;
	}

	/**
	 * @return the swt color. No dispose is needed, the system will handle the
	 *         dispose.
	 */
	public Color getSWTColor() {
		return CustomMediaFactory.getInstance().getColor(colorValue);
	}

	/**
	 * @return true if this color is predefined in color file, false otherwise.
	 */
	public boolean isPreDefined() {
		return preDefined;
	}

	public void setColorName(String colorName) {
		this.colorName = colorName;
		this.colorValue = MediaService.getInstance().getColor(colorName);
		preDefined = true;
	}

	public void setColorValue(RGB rgb) {
		this.colorName = "(" + rgb.red + "," + rgb.green + "," + rgb.blue + ")";
		this.colorValue = rgb;
		preDefined = false;
	}

	@Override
	public String toString() {
		return getColorName();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OPIColor other = (OPIColor) obj;
		if (colorName == null) {
			if (other.colorName != null)
				return false;
		} else if (!colorName.equals(other.colorName))
			return false;
		if (colorValue == null) {
			if (other.colorValue != null)
				return false;
		} else if (!colorValue.equals(other.colorValue))
			return false;
		return true;
	}

	// @Override
	// public boolean equals(Object obj) {
	// if(obj instanceof OPIColor){
	// OPIColor input = (OPIColor)obj;
	// return colorName.equals(input.getColorName()) &&
	// colorValue.equals(input.getRGBValue());
	// }
	// return false;
	// }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((colorName == null) ? 0 : colorName.hashCode());
		result = prime * result
				+ ((colorValue == null) ? 0 : colorValue.hashCode());
		return result;
	}

	public OPIColor getCopy() {
		return new OPIColor(colorName, new RGB(colorValue.red,
				colorValue.green, colorValue.blue), preDefined);
	}

}
