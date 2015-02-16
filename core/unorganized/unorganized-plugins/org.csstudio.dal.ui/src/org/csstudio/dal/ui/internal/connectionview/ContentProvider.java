package org.csstudio.dal.ui.internal.connectionview;

import org.csstudio.platform.simpledal.IProcessVariableConnectionService;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * Content provider for the input statistic table.
 * 
 * @author Sven Wende
 * 
 */
class ContentProvider implements
		IStructuredContentProvider {
	/**
	 * {@inheritDoc}
	 */
	public void inputChanged(final Viewer viewer, final Object oldInput,
			final Object newInput) {

	}

	/**
	 * {@inheritDoc}
	 */
	public Object[] getElements(final Object parent) {
		return ((IProcessVariableConnectionService) parent)
				.getConnectors().toArray();
	}

	/**
	 * {@inheritDoc}
	 */
	public void dispose() {

	}
}