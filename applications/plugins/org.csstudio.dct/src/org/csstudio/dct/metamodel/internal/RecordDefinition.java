package org.csstudio.dct.metamodel.internal;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.csstudio.dct.metamodel.IFieldDefinition;
import org.csstudio.dct.metamodel.IRecordDefinition;


/**
 * Standard implementation of {@link IRecordDefinition}.
 * 
 * @author Sven Wende
 * 
 */
public final class RecordDefinition implements IRecordDefinition {
	private String type;
	private Map<String, IFieldDefinition> fieldDefinitions;

	/**
	 * Constructor.
	 * 
	 * @param type the record type
	 */
	public RecordDefinition(String type) {
		assert type != null;
		this.type = type;
		fieldDefinitions = new HashMap<String, IFieldDefinition>();
	}

	/**
	 * {@inheritDoc}
	 */
	public IFieldDefinition getFieldDefinitions(String fieldName) {
		return fieldDefinitions.get(fieldName);
	}

	
	/**
	 * {@inheritDoc}
	 */
	public Collection<IFieldDefinition> getFieldDefinitions() {
		return fieldDefinitions.values();
	}


	/**
	 * {@inheritDoc}
	 */
	public void addFieldDefinition(IFieldDefinition fieldDefinition) {
		assert fieldDefinition != null;
		fieldDefinitions.put(fieldDefinition.getName(), fieldDefinition);
	}


	/**
	 * {@inheritDoc}
	 */
	public void removeFieldDefinition(IFieldDefinition fieldDefinition) {
		assert fieldDefinition != null;
		fieldDefinitions.remove(fieldDefinition.getName());
	}


	/**
	 * {@inheritDoc}
	 */
	public String getType() {
		return type;
	}


}
