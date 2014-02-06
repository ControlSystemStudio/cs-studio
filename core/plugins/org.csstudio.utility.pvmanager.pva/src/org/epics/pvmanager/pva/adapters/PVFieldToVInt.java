/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.pva.adapters;

import org.epics.pvdata.pv.PVField;
import org.epics.pvdata.pv.PVScalar;
import org.epics.pvdata.pv.PVStructure;
import org.epics.vtype.VInt;
import org.epics.vtype.VTypeToString;

/**
 * @author msekoranja
 *
 */
public class PVFieldToVInt extends AlarmTimeDisplayExtractor implements VInt {

	protected final Integer value;
	
	/**
	 * @param pvField
	 * @param disconnected
	 */
	public PVFieldToVInt(PVStructure pvField, boolean disconnected) {
		super(pvField, disconnected);
		
		PVField field = pvField.getSubField("value");
		if (field instanceof PVScalar)
			value = convert.toInt((PVScalar)field);
		else
			value = null;
	}

	/* (non-Javadoc)
	 * @see org.epics.pvmanager.pva.adapters.PVFieldToVNumber#getValue()
	 */
	@Override
    public Integer getValue()
    {
    	return value;
    }
    
    @Override
    public String toString() {
        return VTypeToString.toString(this);
    }

}
