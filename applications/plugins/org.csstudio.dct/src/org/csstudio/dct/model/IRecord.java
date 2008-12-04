package org.csstudio.dct.model;

import java.util.List;
import java.util.Map;

/**
 * Represents a record.
 * 
 * @author Sven Wende
 * 
 */
public interface IRecord extends IPropertyContainer, IRecordParent {

	/**
	 * Returns the parent. The parent is the super object, this record derives
	 * from.
	 * 
	 * @return the parent
	 */
	IRecord getParentRecord();

	/**
	 * Returns the physical container.
	 * 
	 * @return the physical container
	 */
	IRecordContainer getContainer();

	/**
	 * Sets the physical container
	 * 
	 * @param container
	 *            the physical container
	 */
	void setContainer(IRecordContainer container);

	/**
	 * Returns true, if this record is inherited from a prototype.
	 * 
	 * @return true, if this record is inherited from a prototype
	 */
	boolean isInheritedFromPrototype();

	/**
	 * Returns the name for this record that arises from the record hierarchy.
	 * The delivered name can be defined on this record directly or is inherited
	 * from one of its parents.
	 * 
	 * Don´t mix this with #getName() which delivers the name that is locally
	 * defined (can be null).
	 * 
	 * @return the record name as it is inherited from the hierarchy
	 */
	String getNameFromHierarchy();

	/**
	 * Returns the record type.
	 * 
	 * @return the record type
	 */
	String getType();

	
	
	/**
	 * Adds the specified property.
	 * 
	 * @param name
	 *            the property name
	 * 
	 * @param value
	 *            the property value
	 */
	void addProperty(String name, Object value);

	/**
	 * Returns the value for the specified property.
	 * 
	 * @param name
	 *            the property name
	 * 
	 * @return the value
	 */
	Object getProperty(String name);

	/**
	 * Removes the specified property.
	 * 
	 * @param name
	 *            the property name
	 */
	void removeProperty(String name);

	/**
	 * Resolves all inheritance relationships for this record and returns an
	 * aggregate view on all properties.
	 * 
	 * @return aggregated property information
	 */
	Map<String, Object> getFinalProperties();

	/**
	 * Returns all properties that are locally defined for this record.
	 * 
	 * Note: Usually the records inherit most of their field information from
	 * parents. This method returns only the local field information that have
	 * been stored with this record. If you want an aggregate view you have to
	 * use {@link #getFinalFields()}.
	 * 
	 * @return
	 */
	Map<String, Object> getProperties();

	
	/**
	 * Adds the specified field.
	 * 
	 * @param name
	 *            the field name
	 * 
	 * @param value
	 *            the field value
	 */
	void addField(String name, Object value);

	/**
	 * Returns the value for the specified field.
	 * 
	 * @param name
	 *            the field name
	 * 
	 * @return the value
	 */
	Object getField(String name);

	/**
	 * Removes the specified field value.
	 * 
	 * @param name
	 *            the field name
	 */
	void removeField(String name);

	/**
	 * Returns all fields that are locally defined for this record.
	 * 
	 * Note: Usually the records inherit most of their field information from
	 * parents. This method returns only the local field information that have
	 * been stored with this record. If you want an aggregate view you have to
	 * use {@link #getFinalFields()}.
	 * 
	 * @return
	 */
	Map<String, Object> getFields();

	/**
	 * Resolves all inheritance relationships for this record and returns an
	 * aggregate view on all fields.
	 * 
	 * @return aggregated field information
	 */
	Map<String, Object> getFinalFields();

}