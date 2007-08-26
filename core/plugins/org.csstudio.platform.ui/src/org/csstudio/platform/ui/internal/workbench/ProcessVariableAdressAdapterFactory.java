package org.csstudio.platform.ui.internal.workbench;

import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.model.IWorkbenchAdapter;

public class ProcessVariableAdressAdapterFactory implements IAdapterFactory {


	/**
	 * {@inheritDoc}
	 */
	public Object getAdapter(final Object adaptableObject,
			final Class adapterType) {
		assert adaptableObject != null;
		assert adapterType != null;
		assert adaptableObject instanceof IProcessVariableAddress;

		Object result = null;

		if (adapterType == IWorkbenchAdapter.class) {
			result = new ProcessVariableAdressWorkbenchAdapter();
		}

		return result;
	}


	public Class[] getAdapterList() {
		return new Class[] { IWorkbenchAdapter.class };
	}

}
