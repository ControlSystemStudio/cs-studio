package org.csstudio.dct.metamodel;

import java.util.List;

/**
 * Represents information found in a dbd file.
 *
 * @author Sven Wende
 *
 */
public interface IDatabaseDefinition {
    /**
     * Returns the dbd version represented by this definition.
     *
     * @return the dbd version
     */
    String getDbdVersion();

    /**
     * Returns a record definition for the specified record type.
     *
     * @param recordType
     *            the record type
     * @return a record definition or null if none was found for the specified
     *         type
     */
    IRecordDefinition getRecordDefinition(String recordType);

    /**
     * Returns all record definitions.
     *
     * @return all record definitions
     */
    List<IRecordDefinition> getRecordDefinitions();

    /**
     * Adds a record definition.
     *
     * @param recordDefinition
     *            the record definition
     */
    void addRecordDefinition(IRecordDefinition recordDefinition);

    /**
     * Removes a record definition.
     *
     * @param recordDefinition
     *            the record definition
     */
    void removeRecordDefinition(IRecordDefinition recordDefinition);
}
