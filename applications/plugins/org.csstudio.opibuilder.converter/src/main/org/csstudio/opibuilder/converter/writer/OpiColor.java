/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter.writer;

import org.apache.log4j.Logger;
import org.csstudio.opibuilder.converter.model.EdmColor;
import org.w3c.dom.Element;

/**
 * XML output class for EdmColor type.
 * @author Matevz
 */
public class OpiColor extends OpiAttribute {

	private static Logger log = Logger.getLogger("org.csstudio.opibuilder.converter.writer.OpiColor");

	/**
	 * If EdmColor name is defined, it creates an element 
	 * <tag>
	 *   	<color name="colorName" />
	 * </tag>
	 * 
	 * otherwise creates an element
	 *  
	 * <tag>
	 *   	<color blue="blueValue" green="greenValue" red="redValue" />
	 * </tag>
	 */
	public OpiColor(Context con, String tag, EdmColor c) {
		super(con, tag);

		Element colorElement = context.getDocument().createElement("color");
		context.getElement().appendChild(colorElement);

		String colorName = c.getName();

		if (colorName != null && colorName.length() > 0) {
			colorElement.setAttribute("name", colorName);
			log.debug("Written color: " + colorName);
		}
		else {
			String red = String.valueOf(colorComponentTo8Bits(c.getRed()));
			String green = String.valueOf(colorComponentTo8Bits(c .getGreen()));
			String blue = String.valueOf(colorComponentTo8Bits(c.getBlue()));

			colorElement.setAttribute("red", red);
			colorElement.setAttribute("green", green);
			colorElement.setAttribute("blue", blue);

			log.debug("Written color property with attributes: " + red + ", " + green + ", " + blue);
		}
	}

	/**
	 * Converts the 16 bit color component value to 8 bit and returns it.
	 */
	public static int colorComponentTo8Bits(int colorComponent) {
		return colorComponent / 0x100;
	}
}
