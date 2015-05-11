package org.csstudio.dct.metamodel;

import java.util.Collection;

/**
 * Represents a record description in a dbd file.
 *
 * @author Sven Wende
 *
 */
public interface IRecordDefinition {
    /**
     * Returns the type of the record.
     *
     * @return the type
     */
    String getType();

    /**
     * Returns the field definition for the specified field.
     *
     * @param fieldName
     *            the field name
     * @return the field definition or null
     */
    IFieldDefinition getFieldDefinitions(String fieldName);

    /**
     * Returns all field definitions.
     *
     * @return all field definitions
     */
    Collection<IFieldDefinition> getFieldDefinitions();

    /**
     * Adds a field definition.
     *
     * @param fieldDefinition
     *            the field definition
     */
    void addFieldDefinition(IFieldDefinition fieldDefinition);

    /**
     * Removes a field definition.
     *
     * @param fieldDefinition
     *            the field definition
     */
    void removeFieldDefinition(IFieldDefinition fieldDefinition);

}
