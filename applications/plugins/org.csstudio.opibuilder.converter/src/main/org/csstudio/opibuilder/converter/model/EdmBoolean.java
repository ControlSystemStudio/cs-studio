package org.csstudio.opibuilder.converter.model;

import org.apache.log4j.Logger;

/**
 * Specific class representing EdmBoolean property.
 * 
 * @author Matevz
 *
 */
public class EdmBoolean extends EdmAttribute {

	private static Logger log = Logger.getLogger("org.csstudio.opibuilder.converter.parser.EdmBoolean");
	
	private boolean val;
	
	/**
	 * Constructor, which parses boolean property from EdmAttribute general interface.
	 * 
	 * @param copy	EdmAttribute containing boolean data.
	 * @throws EdmException	Throws exception when data from EdmAttribute is not of valid format.
	 */
	public EdmBoolean(EdmAttribute copy) throws EdmException {
		super(copy);
		
		// If Edm attribute is present then it is true, else false.
		if (copy != null) {
			val = true;
		} else {
			val = false;
		}
		
		// TODO: Temporary fix for multiple specializations, remove when fixed.
		if (copy instanceof EdmBoolean && !((EdmBoolean)copy).is()) {
			val = false;
		}
		
		setInitialized(true);
		log.debug("Parsed " + this.getClass().getName() + " = " + val);
	}
	
	/**
	 * Returns the boolean value.
	 * @return	Value of EdmBoolean instance.
	 */
	public boolean is() {
		return val; 
	}
}