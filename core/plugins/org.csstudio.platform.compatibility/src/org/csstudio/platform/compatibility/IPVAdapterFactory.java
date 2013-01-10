package org.csstudio.platform.compatibility;

import org.csstudio.csdata.ProcessVariable;
import org.csstudio.platform.model.IProcessVariable;
import org.eclipse.core.runtime.IAdapterFactory;

public class IPVAdapterFactory implements IAdapterFactory {

	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (ProcessVariable.class.equals(adapterType)) {
			return new ProcessVariable(((IProcessVariable) adaptableObject).getName());
		}
		return null;
	}

	@Override
	public Class<?>[] getAdapterList() {
		return new Class<?>[] {ProcessVariable.class};
	}

}
