package org.csstudio.graphene;

import java.util.Collection;

import org.csstudio.csdata.ProcessVariable;
import org.eclipse.core.runtime.IAdapterFactory;
import org.epics.vtype.VType;

/**
 * 
 * @author carcassi
 * 
 */
public class VTypeAdapterFactory implements IAdapterFactory {

	@Override
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (adaptableObject instanceof VTypeAdaptable) {
			VTypeAdaptable vTypeAdaptable = (VTypeAdaptable) adaptableObject;
			if (adapterType == VType.class) {
				VType vType = vTypeAdaptable.toVType();
				return vType;
			}
		}
		return null;
	}

	@Override
	public Class<?>[] getAdapterList() {
		return new Class[] { VType.class };
	}

}
