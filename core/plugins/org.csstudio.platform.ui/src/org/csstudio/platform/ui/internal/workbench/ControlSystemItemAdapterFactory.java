package org.csstudio.platform.ui.internal.workbench;

import org.csstudio.platform.model.IControlSystemItem;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.model.IWorkbenchAdapter;

/**
 * An adapter factory for control system items.
 * 
 * @author swende
 * 
 */
public final class ControlSystemItemAdapterFactory implements IAdapterFactory {

	/**
	 * {@inheritDoc}
	 */
	public Object getAdapter(final Object adaptableObject,
			final Class adapterType) {
		assert adaptableObject != null;
		assert adapterType != null;
		assert adaptableObject instanceof IControlSystemItem;

		Object result = null;

		if (adapterType == IWorkbenchAdapter.class) {
			result = new ControlSystemItemWorkbenchAdapter();
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
