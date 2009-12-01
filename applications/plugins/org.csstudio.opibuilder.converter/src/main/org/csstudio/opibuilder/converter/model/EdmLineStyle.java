package org.csstudio.opibuilder.converter.model;

import org.apache.log4j.Logger;

/**
 * Specific class representing lineStyle property.
 * 
 * @author SSah
 *
 */
public class EdmLineStyle extends EdmAttribute {
	
	public static final int SOLID = 0;
	public static final int DASH = 1;

	private static Logger log = Logger.getLogger("org.csstudio.opibuilder.converter.parser.EdmLineStyle");

	private static final String solidString = "solid";
	private static final String dashString = "dash";
	
	private int val;

	/**
	 * Constructor, which parses lineStyle property from EdmAttribute general interface.
	 * 
	 * @param copy	EdmAttribute containing lineStyle string format data.
	 * @throws EdmException	if data from EdmAttribute of invalid format.
	 */
	public EdmLineStyle(EdmAttribute copy, boolean required) throws EdmException {
		super(copy);

		setRequired(required);

		val = SOLID;

		if (copy != null && getValueCount() > 0) {

			String valueString = getValue(0);
			if (solidString.equals(valueString)) {
				val = SOLID;
			} else if (dashString.equals(valueString)) {
				val = DASH;
			} else {
				throw new EdmException(EdmException.SPECIFIC_PARSING_ERROR,
				"Unrecognised line style '" + valueString + "'.");
			}
			setInitialized(true);
		
		} else {
			if (isRequired()) {
				throw new EdmException(EdmException.REQUIRED_ATTRIBUTE_MISSING,
						"Trying to initialize a required attribute from null object.");
			} else {
				log.warn("Missing optional property.");
			}
		}
	}

	/**
	 * @return	The int enum lineStyle value.
	 */
	public int get() {
		return val;
	}
}
