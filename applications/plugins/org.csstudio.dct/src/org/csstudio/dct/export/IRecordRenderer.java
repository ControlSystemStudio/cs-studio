package org.csstudio.dct.export;

import org.csstudio.dct.model.IRecord;

/**
 * Represents a renderer that renders a single record as String.
 * 
 * @author Sven Wende
 * 
 */
public interface IRecordRenderer {
	/**
	 * Renders the specified record as String.
	 * 
	 * @param record
	 *            the record
	 * @return a String representation of the record
	 */
	String render(IRecord record);
}
