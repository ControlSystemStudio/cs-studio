package org.csstudio.alarm.table.dataModel;

import org.csstudio.data.exchange.IProcessVariableName;
import org.csstudio.data.exchange.ProcessVariableName;
import org.eclipse.core.runtime.IAdapterFactory;

public class JMSMessageAdapterFactory implements IAdapterFactory {

	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (IProcessVariableName.class.equals(adapterType)) {
			IProcessVariableName ipvn = new ProcessVariableName("hallo jan");
			return ipvn;
		}
		return null;
	}

	public Class[] getAdapterList() {
		return new Class[] {IProcessVariableName.class};
	}

}
