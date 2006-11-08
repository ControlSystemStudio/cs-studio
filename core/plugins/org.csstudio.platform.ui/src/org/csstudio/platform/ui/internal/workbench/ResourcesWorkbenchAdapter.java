package org.csstudio.platform.ui.internal.workbench;

import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.model.WorkbenchAdapter;

/**
 * A workbench adapter implementation for workspace resources.
 * 
 * @author swende
 *
 */
public final class ResourcesWorkbenchAdapter extends WorkbenchAdapter {
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object[] getChildren(final Object object) {
		Object[] result = new Object[0];

		if (object instanceof IWorkspaceRoot) {
			result = ((IWorkspaceRoot) object).getProjects();
		}
		if (object instanceof IContainer) {
			try {
				result = ((IContainer) object).members();
			} catch (CoreException e) {
				CentralLogger.getInstance().error(this, e);
			}
		}

		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getLabel(final Object object) {
		String label = "<unknown>"; //$NON-NLS-1$

		if (object instanceof IResource) {
			label = ((IResource) object).getName();
		}
		return label;
	}
}
