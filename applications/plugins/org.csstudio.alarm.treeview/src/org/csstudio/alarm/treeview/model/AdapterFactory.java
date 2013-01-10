package org.csstudio.alarm.treeview.model;

import org.csstudio.csdata.ProcessVariable;
import org.eclipse.core.runtime.IAdapterFactory;

@SuppressWarnings("rawtypes")
public class AdapterFactory implements IAdapterFactory {

	@Override
	public Object getAdapter(Object adaptableObject, Class adapterType) {
	        if (adaptableObject instanceof ProcessVariableNode  &&  adapterType == ProcessVariable.class)
	            return new ProcessVariable(((ProcessVariableNode)adaptableObject).getName());
	        return null;
	    }

	@Override
	public Class[] getAdapterList() {
		return new Class[] {
				ProcessVariable.class
		};
	}
}
