package org.csstudio.dct.model;

import java.util.List;

/**
 * Represents a container for records.
 * 
 * @author   Sven Wende
 */
public interface IRecordContainer extends IElement {

	/**
	 * Returns all records.
	 * 
	 * @return all records
	 */
	List<IRecord> getRecords();

	/**
	 * Adds a record.
	 * 
	 * @param record the record
	 */
	void addRecord(IRecord record);

	/**
	 * Adds a record.
	 * @param index
	 *            the position index
	 * @param record the record
	 */
	void addRecord(int index, IRecord record);
	
	/**
	 * Removes a record.
	 * 
	 * @param record the record
	 */
	void removeRecord(IRecord record);
	
	List<IRecordContainer> getDependentRecordContainers();

	void setRecord(int index, IRecord record);
}
