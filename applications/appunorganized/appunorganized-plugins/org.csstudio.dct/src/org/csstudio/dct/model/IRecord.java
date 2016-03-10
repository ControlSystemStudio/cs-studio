package org.csstudio.dct.model;

import java.util.List;
import java.util.Map;

import org.csstudio.dct.metamodel.IRecordDefinition;
import org.csstudio.dct.util.AliasResolutionUtil;

/**
 * Represents a record.
 *
 * @author Sven Wende
 *
 */
public interface IRecord extends IPropertyContainer, IElement {

    /**
     * Returns the EPICs name from the record hierarchy.
     *
     * @deprecated Use {@link AliasResolutionUtil#getEpicsNameFromHierarchy(IRecord)} instead.
     * @return the EPICs name from the record hierarchy
     */
    String getEpicsNameFromHierarchy();

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
     * Sets the physical container.
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
     * Returns true, if this record is disabled. Disabled record will not be
     * rendered in the DB output files.
     *
     * @return true, if this record is disabled
     */
    Boolean getDisabled();

    /**
     * Returns the EPICS name.
     *
     * @return the EPICS name
     */
    String getEpicsName();

    /**
     * Sets the EPICS name
     *
     * @param epicsName
     *            the EPICS name
     */
    void setEpicsName(String epicsName);

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
    @Override
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
    @Override
    void removeProperty(String name);

    /**
     * Resolves all inheritance relationships for this record and returns an
     * aggregate view on all properties.
     *
     * @return aggregated property information
     */
    @Override
    Map<String, String> getFinalProperties();

    /**
     * Returns all properties that are locally defined for this record.
     *
     * Note: Usually the records inherit most of their field information from
     * parents. This method returns only the local field information that have
     * been stored with this record. If you want an aggregate view you have to
     * use {@link #getFinalFields()}.
     *
     * @return the properties that are locally defined for this record
     */
    @Override
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
    void addField(String name, String value);

    /**
     * Returns the value for the specified field.
     *
     * @param name
     *            the field name
     *
     * @return the value
     */
    String getField(String name);

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
     * @return the fields that are locally defined for this record
     */
    Map<String, String> getFields();

    /**
     * Resolves all inheritance relationships for this record and returns an
     * aggregate view on all fields.
     *
     * @return aggregated field information
     */
    Map<String, String> getFinalFields();

    /**
     * Returns the default field values for this record as they are inherited
     * from the database definition (dbd).
     *
     * @return default field values
     */
    Map<String, String> getDefaultFields();

    /**
     * Returns the record definition which contains the informations stored in a
     * database definition file.
     *
     * @return the record definition
     */
    IRecordDefinition getRecordDefinition();

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
     * Removes a record that inherits from this record.
     *
     * @param record
     *            a record that inherits from this record
     */
    void removeDependentRecord(IRecord record);
}
