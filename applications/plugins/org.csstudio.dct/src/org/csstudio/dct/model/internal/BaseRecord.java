package org.csstudio.dct.model.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.csstudio.dct.metamodel.IFieldDefinition;
import org.csstudio.dct.metamodel.IRecordDefinition;
import org.csstudio.dct.model.IContainer;
import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.model.IVisitor;

/**
 * Represents an implicit base record that contains all standard field
 * definitions for a record definition. Each real record in a model will inherit
 * from a base record.
 * 
 * @author Sven Wende
 * 
 */
public class BaseRecord implements IRecord {
	private IRecordDefinition recordDefinition;
	private List<IRecord> inheritingRecords = new ArrayList<IRecord>();
	private Map<String, Object> fields;

	/**
	 * Constructor.
	 * 
	 * @param recordDefinition
	 *            the record definition
	 */
	public BaseRecord(IRecordDefinition recordDefinition) {
		setRecordDefinition(recordDefinition);
	}

	/**
	 * Sets the record definition.
	 * 
	 * @param recordDefinition the record definition
	 */
	public void setRecordDefinition(IRecordDefinition recordDefinition) {
		this.recordDefinition = recordDefinition;

		fields = new HashMap<String, Object>();

		if (recordDefinition != null) {
			for (IFieldDefinition fd : recordDefinition.getFieldDefinitions()) {
				fields.put(fd.getName(), fd.getInitial());
			}
		}
	}

	/**
	 *{@inheritDoc}
	 */
	public void addField(String name, Object value) {
	}

	/**
	 *{@inheritDoc}
	 */
	public void addProperty(String name, String value) {
	}

	/**
	 *{@inheritDoc}
	 */
	public IContainer getContainer() {
		return null;
	}

	/**
	 *{@inheritDoc}
	 */
	public Object getField(String name) {
		return fields.get(name);
	}

	/**
	 *{@inheritDoc}
	 */
	public Map<String, Object> getFields() {
		return fields;
	}

	/**
	 *{@inheritDoc}
	 */
	public Map<String, Object> getFinalFields() {
		return getFields();
	}

	/**
	 *{@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public Map<String, String> getFinalProperties() {
		return Collections.EMPTY_MAP;
	}

	/**
	 *{@inheritDoc}
	 */
	public String getNameFromHierarchy() {
		return null;
	}

	/**
	 *{@inheritDoc}
	 */
	public IRecord getParentRecord() {
		return null;
	}

	/**
	 *{@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public Map<String, String> getProperties() {
		return Collections.EMPTY_MAP;
	}

	/**
	 *{@inheritDoc}
	 */
	public String getProperty(String name) {
		throw null;
	}

	/**
	 *{@inheritDoc}
	 */
	public String getType() {
		return recordDefinition.getType();
	}

	/**
	 *{@inheritDoc}
	 */
	public boolean isInherited() {
		return false;
	}

	/**
	 *{@inheritDoc}
	 */
	public void removeField(String name) {
	}

	/**
	 *{@inheritDoc}
	 */
	public void removeProperty(String name) {
	}

	/**
	 *{@inheritDoc}
	 */
	public void setContainer(IContainer container) {
	}

	/**
	 *{@inheritDoc}
	 */
	public UUID getId() {
		return null;
	}

	/**
	 *{@inheritDoc}
	 */
	public String getName() {
		return recordDefinition != null ? recordDefinition.getType() : "??";
	}

	/**
	 *{@inheritDoc}
	 */
	public void setName(String name) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void addDependentRecord(IRecord record) {
		assert record != null;
		assert record.getParentRecord() == this : "Record must inherit from here.";
		inheritingRecords.add(record);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<IRecord> getDependentRecords() {
		return inheritingRecords;
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeDependentRecord(IRecord record) {
		assert record != null;
		assert record.getParentRecord() == this : "Record must inherit from here.";
		inheritingRecords.remove(record);
	}

	/**
	 *{@inheritDoc}
	 */
	public IRecordDefinition getRecordDefinition() {
		return recordDefinition;
	}

	/**
	 *{@inheritDoc}
	 */
	public boolean hasProperty(String name) {
		return false;
	}

	/**
	 *{@inheritDoc}
	 */
	public void accept(IVisitor visitor) {

	}

	/**
	 *{@inheritDoc}
	 */
	public Map<String, String> getFinalParameterValues() {
		return new HashMap<String, String>();
	}

	public boolean isAbstract() {
		return true;
	}

}