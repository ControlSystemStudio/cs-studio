package org.csstudio.dct.model;

import java.util.UUID;

/**
 * Represents an arbitrary model element.
 * 
 * @author Sven Wende
 * 
 */
public interface IElement {

	/**
	 * Returns the id for this model element. The id is globally unique.
	 * 
	 * @return a globally unique id
	 */
	UUID getId();

	/**
	 * Returns the name of the element.
	 * 
	 * @return the name
	 */
	String getName();

	/**
	 * Sets the name of the element.
	 * 
	 * @param name
	 *            the name of the element
	 */
	void setName(String name);
}
