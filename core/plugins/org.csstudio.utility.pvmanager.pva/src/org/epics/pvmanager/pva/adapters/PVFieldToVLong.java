/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.pva.adapters;

import org.epics.pvdata.pv.PVField;
import org.epics.pvdata.pv.PVScalar;
import org.epics.pvdata.pv.PVStructure;
import org.epics.vtype.VLong;
import org.epics.vtype.VTypeToString;

/**
 * @author msekoranja
 *
 */
public class PVFieldToVLong extends AlarmTimeDisplayExtractor implements VLong {

	protected final Long value;
	
	/**
	 * @param pvField
	 * @param disconnected
	 */
	public PVFieldToVLong(PVStructure pvField, boolean disconnected) {
		super(pvField, disconnected);
		
		PVField field = pvField.getSubField("value");
		if (field instanceof PVScalar)
			value = convert.toLong((PVScalar)field);
		else
			value = null;
	}

	/* (non-Javadoc)
	 * @see org.epics.pvmanager.pva.adapters.PVFieldToVNumber#getValue()
	 */
	@Override
    public Long getValue()
    {
    	return value;
    }
    
    @Override
    public String toString() {
        return VTypeToString.toString(this);
    }

}
