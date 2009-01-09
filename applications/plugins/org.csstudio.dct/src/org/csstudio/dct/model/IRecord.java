package org.csstudio.dct.model;

import java.util.Map;

import org.csstudio.dct.metamodel.IRecordDefinition;

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
	IContainer getContainer();

	/**
	 * Sets the physical container
	 * 
	 * @param container
	 *            the physical container
	 */
	void setContainer(IContainer container);

	/**
	 * Returns true, if this record is abstract. A record is abstract, when it
	 * is part of a prototype.
	 * 
	 * @return true, if this record is abstract
	 */
	boolean isAbstract();

	/**
	 * Returns true, if this record is inherited. A record is inherited, when it
	 * is backed by record in a prototype.
	 * 
	 * @return true, if this record is inherited
	 */
	boolean isInherited();

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
	void addProperty(String name, String value);

	/**
	 * Returns the value for the specified property.
	 * 
	 * @param name
	 *            the property name
	 * 
	 * @return the value
	 */
	String getProperty(String name);

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
	Map<String, String> getFinalProperties();

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
	Map<String, String> getProperties();

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

	/**
	 * Returns the record definition which contains the informations stored in a
	 * database definition file.
	 * 
	 * @return the record definition
	 */
	IRecordDefinition getRecordDefinition();

	Map<String, String> getFinalParameterValues();

}