package org.csstudio.dct.model;

import java.util.List;

public interface IRecordParent {
	
	/**
	 * Returns all records that inherit from this record.
	 * 
	 * @return all records that inherit from this record
	 */
	List<IRecord> getDependentRecords();

	/**
	 * Adds a record that inherits from this record.
	 * 
	 * @param record
	 *            a record that inherits from this record
	 */
	void addDependentRecord(IRecord record);

	/**
	 * Removes a record that inherits from this record
	 * 
	 * @param record
	 *            a record that inherits from this record
	 */
	void removeDependentRecord(IRecord record);
}
