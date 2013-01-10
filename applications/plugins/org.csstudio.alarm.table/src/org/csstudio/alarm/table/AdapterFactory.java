package org.csstudio.alarm.table;

import org.csstudio.alarm.table.dataModel.BasicMessage;
import org.csstudio.csdata.ProcessVariable;
import org.eclipse.core.runtime.IAdapterFactory;

/**
 * Convert BasicMessage to PV
 * 
 * @author jhatje
 */
@SuppressWarnings("rawtypes")
public class AdapterFactory implements IAdapterFactory {
	@Override
	public Class[] getAdapterList() {
		return new Class[] { ProcessVariable.class };
	}

	@Override
	public Object getAdapter(final Object adaptableObject,
			final Class adapterType) {
		if (adaptableObject instanceof BasicMessage
				&& adapterType == ProcessVariable.class)
			return new ProcessVariable(
					((BasicMessage) adaptableObject).getName());
		return null;
	}
}
