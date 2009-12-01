package org.csstudio.opibuilder.converter.model;

import org.apache.log4j.Logger;

/**
 * Specific class representing EdmDouble property.
 * 
 * @author Matevz
 *
 */
public class EdmDouble extends EdmAttribute {

	private static Logger log = Logger.getLogger("org.csstudio.opibuilder.converter.parser.EdmBoolean");
	
	private double val;
	
	/**
	 * Constructor, which parses double property from EdmAttribute general interface.
	 * 
	 * @param copy	EdmAttribute containing double format data.
	 * @throws EdmException	if data from EdmAttribute of invalid format.
	 */
	public EdmDouble(EdmAttribute copy, boolean required) throws EdmException {
		super(copy);
		
		setRequired(required);
		
		if (copy == null || getValueCount() == 0) {
			if (isRequired())
				throw new EdmException(EdmException.REQUIRED_ATTRIBUTE_MISSING,
						"Trying to initialize a required attribute from null object.");
			else {
				log.warn("Missing optional property.");
				return;
			}
		}
		
		if (copy != null)
		{
			try {
				val = Double.parseDouble(copy.getValue(0));
				setInitialized(true);
				log.debug("Parsed " + this.getClass().getName() + 
						" = " + val);
			}
			catch (Exception e) {
				throw new EdmException(EdmException.DOUBLE_FORMAT_ERROR,
						"Invalid double format.");
			}
		}
	}

	/**
	 * Returns the double value.
	 * @return	Value of EdmDouble instance.
	 */
	public double get() {
		return val; 
	}
}
