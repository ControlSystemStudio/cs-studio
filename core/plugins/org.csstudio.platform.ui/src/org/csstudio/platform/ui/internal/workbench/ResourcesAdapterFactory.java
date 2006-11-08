package org.csstudio.platform.ui.internal.workbench;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.model.IWorkbenchAdapter;

/**
 * An adapter factory for workspace resources.
 * 
 * @author swende
 * 
 */
public final class ResourcesAdapterFactory implements IAdapterFactory {

	/**
	 * {@inheritDoc}
	 */
	public Object getAdapter(final Object adaptableObject,
			final Class adapterType) {
		assert adaptableObject != null;
		assert adapterType != null;
		assert adaptableObject instanceof IResource;

		Object result = null;

		if (adapterType == IWorkbenchAdapter.class) {
			result = new ResourcesWorkbenchAdapter();
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
