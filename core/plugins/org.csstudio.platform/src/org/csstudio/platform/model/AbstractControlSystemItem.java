package org.csstudio.platform.model;

import org.eclipse.core.runtime.PlatformObject;

/**
 * An abstract superclass for CSS specific model items. The preferred way to
 * introduce new model items to the platform is to inherit from this class.
 *  
 * Central control system items (e.g. ProcessVariables) are already defined and
 * can be created using {@link ControlSystemItemFactory}.
 * 
 * @author swende
 * 
 */
public abstract class AbstractControlSystemItem extends PlatformObject implements
		IControlSystemItem {
	/**
	 * The name of the control system item.
	 */
	private String _name;

	/**
	 * Constructor.
	 * 
	 * @param name
	 *            The name of the control system item.
	 */
	public AbstractControlSystemItem(final String name) {
		assert name != null;
		_name = name;
	}

	/**
	 * {@inheritDoc}
	 */
	public final String getName() {
		return _name;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return _name;
	}
}
