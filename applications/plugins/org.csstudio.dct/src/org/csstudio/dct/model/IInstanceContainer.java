package org.csstudio.dct.model;

import java.util.List;

import org.csstudio.dct.model.internal.Instance;

/**
 * Represents a container for {@link IInstanceContainer}s.
 * 
 * @author Sven Wende
 */
public interface IInstanceContainer extends IElement {

	/**
	 * Returns all instances.
	 * 
	 * @return all instances
	 */
	List<IInstance> getInstances();

	/**
	 * Adds an instance at the specified position
	 * 
	 * @param index
	 *            the position index
	 */
	IInstance getInstance(int index);

	/**
	 * Adds an instance.
	 * 
	 * @param instance
	 *            the instance
	 */
	void addInstance(IInstance instance);

	/**
	 * Adds an instance.
	 * 
	 * @param index
	 *            the position index
	 * @param instance
	 *            the instance
	 */
	void addInstance(int index, IInstance instance);

	/**
	 * Removes an instance.
	 * 
	 * @param instance
	 *            the instance
	 */
	void removeInstance(IInstance instance);
	
	void setInstance(int index, IInstance instance);


}
