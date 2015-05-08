package org.csstudio.dct.model;

import java.util.List;

import org.csstudio.dct.model.internal.Parameter;

/**
 * Represents a prototype.
 *
 * @author Sven Wende
 *
 */
public interface IPrototype extends IContainer {

    /**
     * Returns all parameters defined by the prototype.
     *
     * @return all parameters
     */
    List<Parameter> getParameters();

    /**
     * Adds the specified parameter.
     *
     * @param parameter
     *            the parameter
     */
    void addParameter(Parameter parameter);

    /**
     * Adds a parameter at a specified list index.
     *
     * @param index
     *            the index
     *
     * @param parameter
     *            the parameter
     *
     */
    void addParameter(int index, Parameter parameter);

    /**
     * Removes the specified parameter.
     *
     * @param parameter
     *            the parameter
     */
    void removeParameter(Parameter parameter);

    /**
     * Removes the parameter at the specified list index.
     *
     * @param index
     *            the list index
     */
    void removeParameter(int index);

    /**
     * Returns true, if a parameter with the specified name exists.
     *
     * @param key
     *            the parameter name
     * @return true, if a parameter with the specified name exists
     */
    boolean hasParameter(String key);
}
