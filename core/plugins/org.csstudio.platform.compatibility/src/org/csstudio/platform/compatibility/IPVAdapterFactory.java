package org.csstudio.platform.compatibility;

import org.csstudio.csdata.ProcessVariableName;
import org.csstudio.platform.model.IProcessVariable;
import org.eclipse.core.runtime.IAdapterFactory;

public class IPVAdapterFactory implements IAdapterFactory {

	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (ProcessVariableName.class.equals(adapterType)) {
			return new ProcessVariableName(((IProcessVariable) adaptableObject).getName());
		}
		return null;
	}

	@Override
	public Class<?>[] getAdapterList() {
		return new Class<?>[] {ProcessVariableName.class};
	}

}
