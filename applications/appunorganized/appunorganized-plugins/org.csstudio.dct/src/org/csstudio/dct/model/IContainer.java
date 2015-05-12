package org.csstudio.dct.model;

import java.util.Map;
import java.util.Set;

/**
 * Represents an inheritable container that contains instances or records.
 *
 * Container can inherit from other containers.
 *
 * @author Sven Wende
 *
 */
public interface IContainer extends IFolderMember, IRecordContainer, IInstanceContainer, IPropertyContainer, IElement {
    /**
     * Returns the physical container.
     *
     * @return the physical container
     */
    IContainer getContainer();

    /**
     * Sets the physical container.
     *
     * @param instanceContainer
     *            the physical container
     */
    void setContainer(IContainer instanceContainer);

    /**
     *
     * Returns the parent. The parent is the super object, this instances
     * derives from. May be another instance or a prototype.
     *
     * @return the parent
     */
    IContainer getParent();

    /**
     * Returns all dependent containers.
     *
     * @return all inheriting elements
     */
    Set<IContainer> getDependentContainers();

    /**
     * Adds the specified dependent container.
     *
     * @param container
     *            the dependent container
     */
    void addDependentContainer(IContainer container);

    /**
     * Removes the specified dependent container.
     *
     * @param container
     *            the dependent container
     */
    void removeDependentContainer(IContainer container);

    /**
     * Resolves all inheritance relationships for this container and returns an
     * aggregate view on all parameter values.
     *
     * @return all parameter values
     */
    Map<String, String> getFinalParameterValues();

    /**
     * Sets a value for the specified parameter.
     *
     * @param key
     *            the parameter name
     * @param value
     *            the parameter value
     */
    void setParameterValue(String key, String value);

    /**
     * Returns a value for the specified parameter.
     *
     * @param key
     *            the parameter name
     * @return the value or null
     */
    String getParameterValue(String key);

    /**
     * Returns true, if a value for the specified parameter exists.
     *
     * @param key
     *            the parameter name
     * @return true, if a value exist
     */
    boolean hasParameterValue(String key);

    /**
     * Returns all locally defined parameter values.
     *
     * @return all local parameter value
     */
    Map<String, String> getParameterValues();
}
