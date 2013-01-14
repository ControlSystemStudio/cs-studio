package org.csstudio.utility.nameSpaceSearch;

import org.csstudio.csdata.ProcessVariable;
import org.csstudio.utility.nameSpaceSearch.ui.ProcessVariableItem;
import org.eclipse.core.runtime.IAdapterFactory;

@SuppressWarnings("rawtypes")
public class AdapterFactory implements IAdapterFactory {

	@Override
	public Object getAdapter(Object adaptableObject, Class adapterType) {
	        if (adaptableObject instanceof ProcessVariableItem  &&  adapterType == ProcessVariable.class)
	            return new ProcessVariable(((ProcessVariableItem)adaptableObject).getName());
	        return null;
	    }

	@Override
	public Class[] getAdapterList() {
		return new Class[] {
				ProcessVariable.class
		};
	}

}
