/**
 * 
 */
package org.csstudio.utility.pvmanager.widgets;

import java.util.Collection;

import org.csstudio.csdata.ProcessVariable;
import org.eclipse.core.runtime.IAdapterFactory;

/**
 * Adapter factor for the ProcessVariableAdaptable.
 * 
 * @author shroffk
 * 
 */
public class ProcessVariableAdapterFactory implements IAdapterFactory {

	@Override
	public Object getAdapter(Object adaptableObject, @SuppressWarnings("rawtypes") Class adapterType) {
		if (adaptableObject instanceof ProcessVariableAdaptable) {
			ProcessVariableAdaptable processVariableAdaptable = (ProcessVariableAdaptable) adaptableObject;
			Collection<ProcessVariable> pvs = processVariableAdaptable
					.toProcessVariables();
			if (adapterType == ProcessVariable.class) {
				if (pvs != null && pvs.size() == 1)
					return pvs.iterator().next();
			} else if (adapterType == ProcessVariable[].class) {
				if (pvs != null && !pvs.isEmpty())
					return pvs.toArray(new ProcessVariable[pvs.size()]);
			}
		}
		return null;
	}

	@Override
	public Class<?>[] getAdapterList() {
		return new Class[] { ProcessVariable.class, ProcessVariable[].class };
	}

}
