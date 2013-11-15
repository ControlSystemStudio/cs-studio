/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.csstudio.opibuilder.util.ConsoleService;
import org.csstudio.opibuilder.util.ErrorHandlerUtil;

/**
 * Specific class representing EdmColor property.
 *
 * @author Matevz
 *
 */
public class EdmColor extends EdmAttribute {

	private static Logger log = Logger.getLogger("org.csstudio.opibuilder.converter.parser.EdmColor");

	private String name;

	private int red;
	private int green;
	private int blue;

	private boolean blinking;

	private int blinkRed;
	private int blinkGreen;
	private int blinkBlue;
	
	public EdmColor(String name, int red, int green, int blue){
		this.name= name;
		this.red = red<<8;
		this.green = green<<8;
		this.blue =  blue<<8;
	}

	/**
	 * Constructor which parses EdmColor from general EdmAttribute value.
	 *
	 * @param genericEntity EdmAttribute containing general EdmColor data.
	 * @param required false if this attribute is optional, else true
	 * @throws EdmException if EdmAttribute contains invalid data.
	 */
	public EdmColor(EdmAttribute genericEntity, boolean required) throws EdmException {
		super(genericEntity);

		setRequired(required);

		if (genericEntity == null || getValueCount() == 0) {
			if (isRequired()) {
				log.warn("Missing required property.");
			} else {
				log.warn("Missing optional property.");
				return;
			}
		}

		String firstVal = getValue(0);

		if (firstVal.startsWith("rgb ")) {
			parseRGBColor();
		} else if (firstVal.startsWith("index ")) {
			parseStaticColor(Integer.parseInt(firstVal.replace("index ", "")));
		} else {
			parseColorListDefinition();
		}

		setInitialized(true);
	}

	public EdmColor(int i) throws EdmException {
		parseStaticColor(i);
	}

	private void parseColorListDefinition() throws EdmException {
		//input
		name = getValue(0);
		//new EdmString(new EdmAttribute(getValue(0)), true);
		if (name == null || name.length() == 0) {
			throw new EdmException(EdmException.COLOR_FORMAT_ERROR, "Color name is empty", null);
		}

		try {
			String[] color = getValue(1).split(" ");

			red = Integer.valueOf(color[0]).intValue();
			green = Integer.valueOf(color[1]).intValue();
			blue = Integer.valueOf(color[2]).intValue();
			
			if (getValueCount() == 3) {
				blinking = true;
				color = getValue(2).split(" ");
				blinkRed = Integer.valueOf(color[0]).intValue();
				blinkGreen = Integer.valueOf(color[1]).intValue();
				blinkBlue = Integer.valueOf(color[2]).intValue();
			}
			else {
				blinking = false;
			}
			
		} catch (Exception exception) {
			throw new EdmException(EdmException.COLOR_FORMAT_ERROR, "", exception);
		}

		log.debug("Parsed colorsList definition.");
	}

	private void parseRGBColor() throws EdmException {

		Pattern p = Pattern.compile("(\\w*)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)");
		Matcher m = p.matcher(getValue(0));

		if (!m.find()) {
			throw new EdmException(EdmException.COLOR_FORMAT_ERROR, "Invalid RGB color format.", null);
		}

		try {
			name = null;
			red = Integer.valueOf(m.group(2)).intValue();
			green = Integer.valueOf(m.group(3)).intValue();
			blue = Integer.valueOf(m.group(4)).intValue();
			blinking = false;

			log.debug("Parsed RGB color.");
		}
		catch (Exception e) {
			throw new EdmException(EdmException.COLOR_FORMAT_ERROR, "Invalid RGB color format.",e);
		}
	}

	private void parseStaticColor(int i) throws EdmException {

		EdmColor color = EdmModel.getColorsList().getColor(i);
		if (color == null) {
			ConsoleService.getInstance().writeWarning(
					"Color index " + i + " is not in colors list file. Use black color instead.");
			color = new EdmColor(0);
		}
			
		name = color.getName();
		red = color.getRed();
		green = color.getGreen();
		blue = color.getBlue();
		blinking = color.isBlinking();
		blinkRed = color.getBlinkRed();
		blinkGreen = color.getBlinkGreen();
		blinkBlue = color.getBlinkBlue();
		
		log.debug("Parsed static color.");
	}

	public String getName() {
		return name;
	}

	public int getRed() {
		return red;
	}

	public int getGreen() {
		return green;
	}

	public int getBlue() {
		return blue;
	}

	public int getBlinkRed() {
		return blinkRed;
	}

	public int getBlinkGreen() {
		return blinkGreen;
	}

	public int getBlinkBlue() {
		return blinkBlue;
	}

	public boolean isBlinking() {
		return blinking;
	}
}
