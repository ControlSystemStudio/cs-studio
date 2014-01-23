/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.pva.adapters;

import org.epics.pvdata.pv.PVStructure;
import org.epics.vtype.VDouble;
import org.epics.vtype.VTypeToString;

/**
 * @author msekoranja
 *
 */
public class PVFieldToVDouble extends AlarmTimeDisplayExtractor implements VDouble {

	protected final Double value;
	
	/**
	 * @param pvField
	 * @param disconnected
	 */
	public PVFieldToVDouble(PVStructure pvField, boolean disconnected) {
		super(pvField, disconnected);
		
		value = getDoubleValue(pvField, "value", null);
	}

    /* (non-Javadoc)
     * @see org.epics.pvmanager.pva.adapters.PVFieldToVNumber#getValue()
     */
	@Override
    public Double getValue()
    {
    	return value;
    }
    
    @Override
    public String toString() {
        return VTypeToString.toString(this);
    }

}
