package org.csstudio.dct;

import java.util.Map;

import org.csstudio.dct.model.IRecord;

/**
 * Represents a record function. Corresponding extensions (extension point
 * org.csstudio.dct.recordfunctions) can be started from the editor´s menu. On
 * start, the run() method will be applied to all records.
 * 
 * The specified attributes will appear in the properties table for each record
 * and can be changed by by the user.
 * 
 * @author Sven Wende
 * 
 */
public interface IRecordFunction {
	/**
	 * Runs the function for the specified record.
	 * 
	 * @param record
	 *            the record
	 * @param attributes
	 *            the current attributes of the record
	 */
	void run(IRecord record, Map<String, String> attributes);

	/**
	 * Returns the attributes that should be displayed in the properties table
	 * of all records, when this extension is loaded.
	 * 
	 * @return a map with attributes as key and a default value as value
	 */
	Map<String, String> getAttributes();
}
