package org.csstudio.platform.ui.internal.workbench;

import org.csstudio.platform.model.pvs.IProcessVariableAdress;
import org.csstudio.platform.model.pvs.IProcessVariableAdressProvider;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.IActionFilter;

public class ProcessVariableAdressProviderAdapterFactory implements
		IAdapterFactory {

	public Object getAdapter(Object adaptableObject, Class adapterType) {
		assert adaptableObject != null;
		assert adapterType != null;
		assert adaptableObject instanceof IProcessVariableAdressProvider : "adaptableObject instanceof IProcessVariableAdressProvider";

		if (adapterType == IActionFilter.class) {
			IActionFilter adapter = new IActionFilter() {

				public boolean testAttribute(Object target, String name,
						String value) {
					IProcessVariableAdressProvider provider = (IProcessVariableAdressProvider) target;
					
					if(name.equals("empty")) {
						boolean isEmpty = provider.getProcessVariableAdresses().isEmpty();
						
						if(value.equals("false")) {
							return !isEmpty;
						} else {
							return isEmpty;
						}
					}
					
					return false;
				}

			};
			
			return adapter;
		}
		return null;
	}

	public Class[] getAdapterList() {
		return new Class[] { IActionFilter.class };
	}
}
