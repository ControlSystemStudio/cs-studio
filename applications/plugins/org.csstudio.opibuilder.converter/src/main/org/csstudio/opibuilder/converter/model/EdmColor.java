package org.csstudio.opibuilder.converter.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

/**
 * Specific class representing EdmColor property.
 *
 * @author Matevz
 *
 */
public class EdmColor extends EdmAttribute {

	private static Logger log = Logger.getLogger("org.csstudio.opibuilder.converter.parser.EdmColor");

	@EdmAttributeAn @EdmOptionalAn private EdmString name;

	@EdmAttributeAn private EdmInt	red;
	@EdmAttributeAn private EdmInt	green;
	@EdmAttributeAn private EdmInt	blue;

	private EdmBoolean isBlinking;

	@EdmAttributeAn @EdmOptionalAn private EdmInt	blinkRed;
	@EdmAttributeAn @EdmOptionalAn private EdmInt	blinkGreen;
	@EdmAttributeAn @EdmOptionalAn private EdmInt	blinkBlue;

	/**
	 * Constructor which parses EdmColor from general EdmAttribute value.
	 *
	 * @param genericEntity EdmAttribute containing general EdmColor data.
	 * @throws EdmException if EdmAttribute contains invalid data.
	 */
	public EdmColor(EdmAttribute genericEntity, boolean required) throws EdmException {
		super(genericEntity);

		setRequired(required);

		if (genericEntity == null || getValueCount() == 0) {
			if (isRequired())
				throw new EdmException(EdmException.REQUIRED_ATTRIBUTE_MISSING,
						"Trying to initialize a required attribute from null object.");
			else {
				log.warn("Missing optional property.");
				return;
			}
		}

		String firstVal = getValue(0);

		if (firstVal.startsWith("rgb "))
			parseRGBColor();
		else if (firstVal.startsWith("index "))
			parseStaticColor(Integer.parseInt(firstVal.replace("index ", "")));
		else
			parseColorListDefinition();

		setInitialized(true);
	}

	public EdmColor(int i) throws EdmException {
		parseStaticColor(i);
	}

	private void parseColorListDefinition() throws EdmException {
		//input
		name = new EdmString(new EdmAttribute(getValue(0)), true);

		String r,g,b;
		String[] color;
		color = getValue(1).split(" ");
		r = color[0];
		g = color[1];
		b = color[2];

		red = new EdmInt(new EdmAttribute(r), true);
		green = new EdmInt(new EdmAttribute(g), true);
		blue = new EdmInt(new EdmAttribute(b), true);

		if (getValueCount() == 3) {
			isBlinking = new EdmBoolean(new EdmAttribute());
			color = getValue(2).split(" ");
			r = color[0];
			g = color[1];
			b = color[2];

			blinkRed = new EdmInt(new EdmAttribute(r), false);
			blinkGreen = new EdmInt(new EdmAttribute(g), false);
			blinkBlue = new EdmInt(new EdmAttribute(b), false);
		}
		else
			isBlinking = new EdmBoolean(null);

		log.debug("Parsed colorsList definition.");
	}

	private void parseRGBColor() throws EdmException {

		Pattern p = Pattern.compile("(\\w*)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)");
		Matcher m = p.matcher(getValue(0));

		if (m.find()) {

			try {
				String r = m.group(2);
				String g = m.group(3);
				String b = m.group(4);

				this.name = new EdmString(new EdmAttribute(""), false);
				this.red = new EdmInt(new EdmAttribute(r), true);
				this.green = new EdmInt(new EdmAttribute(g), true);
				this.blue = new EdmInt(new EdmAttribute(b), true);
				this.isBlinking = new EdmBoolean(null);

				log.debug("Parsed RGB color.");
			}
			catch (Exception e) {
				throw new EdmException(EdmException.COLOR_FORMAT_ERROR, "Invalid RGB color format.");
			}
		}
		else
			throw new EdmException(EdmException.COLOR_FORMAT_ERROR, "Invalid RGB color format.");

	}

	private void parseStaticColor(int i) throws EdmException {

		try {

			EdmColor c = EdmModel.getColorsList().getColor(i);

			name = new EdmString(new EdmAttribute(c.getName()), true);

			red = new EdmInt(new EdmAttribute(Integer.toString(c.getRed())), true);
			green = new EdmInt(new EdmAttribute(Integer.toString(c.getGreen())), true);
			blue = new EdmInt(new EdmAttribute(Integer.toString(c.getBlue())), true);

			isBlinking = new EdmBoolean(null);
			if (c.blinkRed != null) {
				blinkRed = new EdmInt(new EdmAttribute(Integer.toString(c.getBlinkRed())), false);
				if (c.blinkGreen != null) {
					blinkGreen = new EdmInt(new EdmAttribute(Integer.toString(c.getBlinkGreen())), false);
					if (c.blinkBlue != null) {
						blinkBlue = new EdmInt(new EdmAttribute(Integer.toString(c.getBlinkBlue())), false);
						isBlinking = new EdmBoolean(new EdmAttribute());
					}
				}
			}
			setInitialized(true);
		}
		catch (Exception e) {
			throw new EdmException(EdmException.COLOR_FORMAT_ERROR,
					"Color index " + i + " is not in given EdmColorsList instance.");
		}

		log.debug("Parsed static color.");
	}

	public String getName() {
		return name.get();
	}

	public int getRed() {
		return red.get();
	}

	public int getGreen() {
		return green.get();
	}

	public int getBlue() {
		return blue.get();
	}

	public int getBlinkRed() {
		return blinkRed.get();
	}

	public int getBlinkGreen() {
		return blinkGreen.get();
	}

	public int getBlinkBlue() {
		return blinkBlue.get();
	}

	public boolean isBlinking() {
		return isBlinking.is();
	}
}
