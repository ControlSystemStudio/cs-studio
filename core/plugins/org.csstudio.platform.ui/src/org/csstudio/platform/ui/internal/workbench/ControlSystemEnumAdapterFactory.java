package org.csstudio.platform.ui.internal.workbench;

import org.csstudio.platform.model.IControlSystemItem;
import org.csstudio.platform.model.pvs.ControlSystemEnum;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.model.IWorkbenchAdapter;

public final class ControlSystemEnumAdapterFactory implements IAdapterFactory {

	/**
	 * {@inheritDoc}
	 */
	public Object getAdapter(final Object adaptableObject,
			final Class adapterType) {
		assert adaptableObject != null;
		assert adapterType != null;
		assert adaptableObject instanceof ControlSystemEnum;

		Object result = null;

		if (adapterType == IWorkbenchAdapter.class) {
			result = new ControlSystemEnumWorkbenchAdapter();
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
