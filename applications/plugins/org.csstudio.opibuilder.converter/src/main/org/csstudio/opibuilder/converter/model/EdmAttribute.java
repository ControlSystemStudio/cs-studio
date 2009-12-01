package org.csstudio.opibuilder.converter.model;

import java.util.Iterator;
import java.util.Vector;

/**
 * Generic data container for Edm object's properties and its values.
 * Base class for all specific Edm properties.
 * 
 * @author Matevz
 *
 */
public class EdmAttribute {

	private Vector<String>	values;
	
	private boolean required;
	private boolean initialized;
	
	private void initDefaultValues() {
		values = new Vector<String>();
		required = true;
		initialized = false;
	}
	
	/**
	 * Empty constructor. Does not assign any values.
	 */
	public EdmAttribute() {
		initDefaultValues();
	}

	/**
	 * Constructor, which appends one value.
	 * @param firstValue Value to append.
	 */
	public EdmAttribute(String firstValue) {
		initDefaultValues();
		appendValue(firstValue);
	}
	
	/**
	 * Constructs an instance of EdmAttribute from data of another EdmAttribute instance.
	 * @param copy EdmAttribute to copy.
	 */
	public EdmAttribute(EdmAttribute copy) {
		initDefaultValues();
		
		if (copy != null) {
			int valCount = copy.getValueCount();
			for (int i = 0; i < valCount; i++)
				appendValue(copy.getValue(i));
		}
	}

	/**
	 * Returns the value of attribute at specified index.
	 * @param index		Index of desired value.
	 * @return			Value of attribute.
	 */
	public String getValue(int index) {
		return values.get(index);
	}
	
	/**
	 * Returns the number of values in attribute.
	 * @return	The number of values.
	 */
	public int getValueCount() {
		return values.size();
	}
	
	/**
	 * Appends the value at the end of the attribute.
	 * Omits all quotation marks and returns this value back
	 * (used for logging).
	 * 
	 * @param value	Value to append.
	 * @return		Actual value appended (without quotations).
	 */
	public String appendValue(String value) {
		value = value.replaceAll("\"", "");
		values.add(value);
		return value;
	}

	public boolean isRequired() {
		return required;
	}

	public boolean isInitialized() {
		return initialized;
	}
	
	@Override
	public String toString() {
		StringBuffer concatenatedValues = new StringBuffer();

		Iterator<String> iterator = values.iterator();
		while (iterator.hasNext()) {
			String value = iterator.next();
			
			concatenatedValues.append(value);
			if (iterator.hasNext()) {
				concatenatedValues.append(" ");
			}
		}
		return concatenatedValues.toString();
	}

	protected void setRequired(boolean optional) {
		this.required = optional;
	}

	protected void setInitialized(boolean initialized) {
		this.initialized = initialized;
	}

}
