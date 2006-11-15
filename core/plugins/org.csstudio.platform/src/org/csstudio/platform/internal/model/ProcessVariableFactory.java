package org.csstudio.platform.internal.model;

import org.csstudio.platform.model.AbstractControlSystemItemFactory;
import org.csstudio.platform.model.IProcessVariable;

/**
 * Implementation of {@link AbstractControlSystemItemFactory} for process
 * variables.
 * 
 * @author Sven Wende
 * 
 */
public final class ProcessVariableFactory extends
		AbstractControlSystemItemFactory<IProcessVariable> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String createStringRepresentationFromItem(final IProcessVariable item) {
		return item.getName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IProcessVariable createItemFromStringRepresentation(final String s) {
		IProcessVariable result = new ProcessVariable(s);
		return result;
	}
}
