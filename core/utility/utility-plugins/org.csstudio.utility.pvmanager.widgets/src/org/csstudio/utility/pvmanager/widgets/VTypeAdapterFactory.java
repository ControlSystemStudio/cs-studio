package org.csstudio.utility.pvmanager.widgets;

import org.eclipse.core.runtime.IAdapterFactory;
import org.epics.vtype.VType;

/**
 * An adapter factory that transforms VTypeAdaptable object to VTypes.
 *
 * @author carcassi
 */
public class VTypeAdapterFactory implements IAdapterFactory {

    @Override
    public Object getAdapter(Object adaptableObject, @SuppressWarnings("rawtypes") Class adapterType) {
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
