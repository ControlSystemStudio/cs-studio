package org.csstudio.dct.model;

import java.util.Map;

/**
 * Represents a record.
 *
 * @author Sven Wende
 *
 */
public interface IPropertyContainer extends IElement {

    /**
     * Returns true when this container has a certain property.
     *
     * @param name
     *            the property name
     * @return true, when the property exists
     */
    boolean hasProperty(String name);

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
     * @return the properties that are locally defined for this record
     */
    Map<String, String> getProperties();
}
