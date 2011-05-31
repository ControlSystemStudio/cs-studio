/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.scriptUtil;

import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;

/**Utility class to facilitate Javascript programming
 * for color operation. 
 * @author Xihui Chen
 *
 */
public class ColorFontUtil {

	/** the color of black */
	final static public RGB BLACK = new RGB(0, 0, 0);

	/** the color of blue */
	final static public RGB BLUE = new RGB(0, 0, 255);

	/** the color of cyan */
	final static public RGB CYAN = new RGB(0, 255, 255);

	/** the color of dark gray */
	final static public RGB DARK_GRAY = new RGB(150, 150, 150);	

	/** the color of gray */
	final static public RGB GRAY = new RGB(200, 200, 200);

	/** the color of green */
	final static public RGB GREEN = new RGB(0, 255, 0);

	/** the color of light blue */
	final static public RGB LIGHT_BLUE = new RGB(153, 186, 243);

	/** the color of orange */
	final static public RGB ORANGE = new RGB(255, 128, 0);

	/** the color of pink */
	final static public RGB PINK = new RGB(255, 0, 255);

	/** the color of orange */
	final static public RGB PURPLE = new RGB(128, 0, 255);

	/** the color of red */
	final static public RGB RED = new RGB(255, 0, 0);

	/** the color of white */
	final static public RGB WHITE = new RGB(255, 255, 255);

	/** the color of yellow */
	final static public RGB YELLOW = new RGB(255, 255, 0);
		
	/**
	 * Get a color with the given
	 * red, green and blue values.
	 *
	 * @param red the red component of the new instance
	 * @param green the green component of the new instance
	 * @param blue the blue component of the new instance
	 *
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_INVALID_ARGUMENT - if the red, green or blue argument is not between 0 and 255</li>
	 * </ul>
	 */
	public final static RGB getColorFromRGB(int red, int green, int blue){
		return new RGB(red, green, blue);
	}
	
	/**
	* Get a color with the given
	* hue, saturation, and brightness.
	*
	* @param hue the hue value for the HSB color (from 0 to 360)
	* @param saturation the saturation value for the HSB color (from 0 to 1)
	* @param brightness the brightness value for the HSB color (from 0 to 1)
	*
	* @exception IllegalArgumentException <ul>
	*    <li>ERROR_INVALID_ARGUMENT - if the hue is not between 0 and 360 or
	*    the saturation or brightness is not between 0 and 1</li>
	* </ul>
	* 
	*/
	public final static RGB getColorFromHSB(float hue, float saturation, float brightness){
		return new RGB(hue, saturation, brightness);
	}
	
	/**	 
	 * Get a new font data given a font name,
	 * the height of the desired font in points, 
	 * and a font style.
	 *
	 * @param name the name of the font (must not be null)
	 * @param height the font height in points
	 * @param style A bitwise combination of NORMAL(0), BOLD(1) and ITALIC(2).
	 *
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_NULL_ARGUMENT - when the font name is null</li>
	 *    <li>ERROR_INVALID_ARGUMENT - if the height is negative</li>
	 * </ul>
	 */
	public final static FontData getFont(String name, int height, int style){
		return new FontData(name, height, style);
	}
	
}
