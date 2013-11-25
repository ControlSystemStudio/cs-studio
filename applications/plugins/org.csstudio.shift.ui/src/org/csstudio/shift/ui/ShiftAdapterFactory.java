package org.csstudio.shift.ui;

import gov.bnl.shiftClient.Shift;

import org.eclipse.core.runtime.IAdapterFactory;
import org.csstudio.shift.ShiftBuilder;

public class ShiftAdapterFactory implements IAdapterFactory {

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.core.runtime.IAdapterFactory#getAdapter(java.lang.Object,
     * java.lang.Class)
     */
    @Override
    public Object getAdapter(final Object adaptableObject, final Class adapterType) {
        final Shift shift = ((Shift) adaptableObject);
        if (adapterType == ShiftBuilder.class) {
            return ShiftBuilder.shift(shift);           
        } else {
            return null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.core.runtime.IAdapterFactory#getAdapterList()
     */
    @Override
    public Class[] getAdapterList() {
	    return new Class[] { ShiftBuilder.class };
    }

}
