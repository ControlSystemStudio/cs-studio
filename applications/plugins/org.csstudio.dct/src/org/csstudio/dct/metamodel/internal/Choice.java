package org.csstudio.dct.metamodel.internal;

import org.csstudio.dct.metamodel.IChoice;


/**
 * Standard implementation of {@link IChoice}.
 * 
 * @author Sven Wende
 * 
 */
public final class Choice implements IChoice {
	private String description;
	private String id;

	/**
	 * Constructor.
	 * @param id a non-empty id
	 * @param description a non-empty description
	 */
	public Choice(String id, String description) {
		assert id != null;
		assert description != null;
		this.description = description;
		this.id = id;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getId() {
		return id;
	}

}
