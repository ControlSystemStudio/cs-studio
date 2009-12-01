package org.csstudio.opibuilder.converter.model;

import org.apache.log4j.Logger;

/**
 * Specific class representing EdmInt property.
 * 
 * @author Matevz
 *
 */
public class EdmInt extends EdmAttribute {

	private static Logger log = Logger.getLogger("org.csstudio.opibuilder.converter.parser.EdmInt");

	private int val;

	/**
	 * Constructor, which parses int property from EdmAttribute general interface.
	 * 
	 * @param copy	EdmAttribute containing int format data.
	 * @throws EdmException	if data from EdmAttribute of invalid format.
	 */
	public EdmInt(EdmAttribute copy, boolean required) throws EdmException {
		super(copy);

		setRequired(required);

		if (copy == null || getValueCount() == 0) {
			if (isRequired()) {
				throw new EdmException(EdmException.REQUIRED_ATTRIBUTE_MISSING,
						"Trying to initialize a required attribute from null object.");
			}
			else {
				log.warn("Missing optional property.");
				return;
			}
		}

		try {
			val = Integer.parseInt(getValue(0));
			setInitialized(true);
			log.debug("Parsed " + this.getClass().getName() + 
					" = " + val);
		}
		catch (Exception e) {
			throw new EdmException(EdmException.INTEGER_FORMAT_ERROR,
			"Invalid integer format.");
		}

	}

	/**
	 * Returns the integer value.
	 * @return	Value of EdmInt instance.
	 */
	public int get() {
		return val;
	}
}
