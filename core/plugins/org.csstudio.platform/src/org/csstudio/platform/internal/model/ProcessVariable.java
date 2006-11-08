package org.csstudio.platform.internal.model;

import org.csstudio.platform.model.AbstractControlSystemItem;
import org.csstudio.platform.model.IProcessVariable;

/**
 * Implementation of the {@link IProcessVariable} interface.
 * 
 * This is internal API and should not be instantiated directly by clients.
 * 
 * @author Kay Kasemir, wende
 */
public class ProcessVariable extends AbstractControlSystemItem implements
		IProcessVariable {
	
	/**
	 * Constructor.
	 * 
	 * @param name
	 *            The name of the process variable.
	 */
	public ProcessVariable(final String name) {
		super(name);
	}

	/**
	 * {@inheritDoc}
	 */
	public final String getTypeId() {
		return TYPE_ID;
	}
}
