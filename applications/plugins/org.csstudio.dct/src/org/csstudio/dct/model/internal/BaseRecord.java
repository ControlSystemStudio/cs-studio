package org.csstudio.dct.model.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.csstudio.dct.metamodel.IFieldDefinition;
import org.csstudio.dct.metamodel.IRecordDefinition;
import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.model.IRecordContainer;
import org.csstudio.dct.model.IVisitor;

public class BaseRecord implements IRecord {
	private IRecordDefinition recordDefinition;

	private List<IRecord> inheritingRecords = new ArrayList<IRecord>();

	private Map<String, Object> fields;

	public BaseRecord(IRecordDefinition recordDefinition) {
		setRecordDefinition(recordDefinition);
	}

	public void setRecordDefinition(IRecordDefinition recordDefinition) {
		this.recordDefinition = recordDefinition;

		fields = new HashMap<String, Object>();

		if (recordDefinition != null) {
			for (IFieldDefinition fd : recordDefinition.getFieldDefinitions()) {
				fields.put(fd.getName(), fd.getInitial());
			}
		}
	}

	public void addField(String name, Object value) {
	}

	public void addProperty(String name, String value) {
	}

	public IRecordContainer getContainer() {
		return null;
	}

	public Object getField(String name) {
		return fields.get(name);
	}

	public Map<String, Object> getFields() {
		return fields;
	}

	public Map<String, Object> getFinalFields() {
		return getFields();
	}

	public Map<String, String> getFinalProperties() {
		return Collections.EMPTY_MAP;
	}

	public String getNameFromHierarchy() {
		return null;
	}

	public IRecord getParentRecord() {
		return null;
	}

	public Map<String, String> getProperties() {
		return Collections.EMPTY_MAP;
	}

	public String getProperty(String name) {
		throw null;
	}

	public String getType() {
		return recordDefinition.getType();
	}

	public boolean isInheritedFromPrototype() {
		return false;
	}

	public void removeField(String name) {
	}

	public void removeProperty(String name) {
	}

	public void setContainer(IRecordContainer container) {
	}

	public UUID getId() {
		return null;
	}

	public String getName() {
		return recordDefinition!=null?recordDefinition.getType():"??";
	}

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

	public IRecordDefinition getRecordDefinition() {
		return recordDefinition;
	}

	public boolean hasProperty(String name) {
		return false;
	}

	public void accept(IVisitor visitor) {
		
	}

}