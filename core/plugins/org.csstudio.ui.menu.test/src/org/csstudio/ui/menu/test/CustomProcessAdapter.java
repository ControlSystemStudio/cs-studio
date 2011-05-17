package org.csstudio.ui.menu.test;

import org.csstudio.csdata.ProcessVariable;
import org.eclipse.core.runtime.IAdapterFactory;

public class CustomProcessAdapter implements IAdapterFactory {

	@Override
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (ProcessVariable.class == adapterType) {
			return new ProcessVariable(((CustomProcessVariable) adaptableObject).getName());
		}
		return null;
	}

	@Override
	public Class[] getAdapterList() {
		return new Class[] {ProcessVariable.class};
	}

}
