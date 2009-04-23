package org.csstudio.platform.ui.security;

import org.csstudio.platform.security.IActivationAdapter;
import org.eclipse.jface.action.IAction;

public class EnableActionAdapter implements IActivationAdapter {

	/**
	 * {@inheritDoc}
	 */
	public void activate(final Object o, final boolean activate) {
		if (o instanceof IAction) {
			((IAction)o).setEnabled(activate);
		}
	}

}
