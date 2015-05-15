package org.csstudio.dct.nameresolution;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.util.AliasResolutionException;

/**
 * Represents a services that can be used to find and evaluate field functions
 * in a source String.
 *
 * @author Sven Wende
 *
 */
public interface IFieldFunctionService {
    /**
     * Resolves all variables in the specified source String.
     *
     * @param source
     *            the source String
     * @param vars
     *            the variables
     * @return a resolved String that contains no variables anymore
     *
     * @throws AliasResolutionException
     */
    String resolve(String source, Map<String, String> vars) throws AliasResolutionException;

    /**
     * Finds all variables in die specified source String.
     *
     * @param source
     *            the source String
     * @return the names of all variables in the String
     */
    Set<String> findRequiredVariables(final String source);

    /**
     * Evaluates all functions in the specified source String.
     *
     * @param source
     *            the source String
     * @param record
     *            the record which contains the field with this function
     * @param fieldName
     *            the name of the field that contains this function
     *
     * @return the final String
     */
    String evaluate(String source, IRecord record, String fieldName) throws Exception;

    /**
     * Returns extension descriptors for all registered field functions.
     *
     * @return extension descriptors for all registered field functions
     */
    List<FieldFunctionExtension> getFieldFunctionExtensions();
}
