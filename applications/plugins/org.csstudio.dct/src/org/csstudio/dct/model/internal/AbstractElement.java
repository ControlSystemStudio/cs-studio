package org.csstudio.dct.model.internal;

import java.util.UUID;

import org.csstudio.dct.model.IElement;
import org.csstudio.dct.util.CompareUtil;

/**
 * Standard implementation of {@link IElement}.
 * 
 * @author Sven Wende
 * 
 */
public abstract class AbstractElement implements IElement {
	private String name;
	private UUID id;

	/**
	 * Constructor.
	 * 
	 * @param name
	 *            the name
	 */
	public AbstractElement(String name) {
		this.name = name;
		id = UUID.randomUUID();
	}

	public AbstractElement(String name, UUID id) {
		assert id != null;
		this.name = name;
		this.id = id;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getName() {
		return name;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setName(String name) {
		this.name = name;
	}

	public UUID getId() {
		return id;
	}

	/**
	 *{@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
//		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	/**
	 *{@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		boolean result = true;

		if (obj instanceof AbstractElement) {
			AbstractElement element = (AbstractElement) obj;

			if (CompareUtil.equals(getName(), element.getName())) {
				if (CompareUtil.equals(getId(), element.getId())) {
					result = true;
				}
			}
		}

		return result;
	}

}
