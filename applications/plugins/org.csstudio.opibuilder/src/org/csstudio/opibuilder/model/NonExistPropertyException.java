package org.csstudio.opibuilder.model;

/**
 * The exception shows that the property doesn't exist.
 * @author Xihui Chen
 *
 */
public class NonExistPropertyException extends RuntimeException {		
	private static final long serialVersionUID = 1L;
	private String propID;
	private String widgetName;
	public NonExistPropertyException(String widgetName, String propID) {
		this.propID = propID;
		this.widgetName = widgetName;
	}
	
	@Override
	public String getMessage() {
		return widgetName + " does not have property: " + propID;
	}
}