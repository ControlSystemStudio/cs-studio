package org.csstudio.platform.ui.internal.workbench;

import org.csstudio.platform.model.IProcessVariable;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.model.IWorkbenchAdapter;

/**
 * An adapter factory for process variables.
 * 
 * @author swende
 * 
 */
public final class ProcessVariableAdapterFactory implements IAdapterFactory {

	/**
	 * {@inheritDoc}
	 */
	public Object getAdapter(final Object adaptableObject,
			final Class adapterType) {
		assert adaptableObject != null;
		assert adapterType != null;
		assert adaptableObject instanceof IProcessVariable;

		Object result = null;

		if (adapterType == IWorkbenchAdapter.class) {
			result = new ProcessVariableWorkbenchAdapter();
		}

		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	public Class[] getAdapterList() {
		return new Class[] { IWorkbenchAdapter.class };
	}

}
