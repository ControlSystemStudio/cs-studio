package org.csstudio.dct.model;

import java.util.Map;
import java.util.Set;

import org.csstudio.dct.model.internal.Instance;

/**
 * Represents an inheritable container that contains instances or records.
 * 
 * Container can inherit from other containers.
 * 
 * @author Sven Wende
 * 
 */
public interface IContainer extends IFolderMember, IRecordContainer, IInstanceContainer, IPropertyContainer {
	/**
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
	 * aggregate view on all parameters value.
	 * 
	 * @return all parameter values
	 */
	Map<String, String> getFinalParameterValues();
	
	/**
	 * Returns all locally defined parameter values.
	 * 
	 * @return all local parameter value
	 */
	Map<String, String> getParameterValues();
}
