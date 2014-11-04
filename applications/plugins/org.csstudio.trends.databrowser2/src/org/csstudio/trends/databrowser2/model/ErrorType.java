package org.csstudio.trends.databrowser2.model;

import org.csstudio.trends.databrowser2.Messages;

/**
 *  Error bar value types
 * 
 * @author Friederike Johlinger
 *
 */
public enum ErrorType {
/**To determine whether the range or the stdDev should be used for the error
 * bars.
 */
	MIN_MAX(Messages.ErrorType_MinMax),
	
	STD_DEV(Messages.ErrorType_StdDev);
	
	final private String name;
	
	private ErrorType(String name){
		this.name = name;
	}
	
    /** @return Localized name of this error type */
    @Override
    public String toString()
    {
        return name;
    }
}
