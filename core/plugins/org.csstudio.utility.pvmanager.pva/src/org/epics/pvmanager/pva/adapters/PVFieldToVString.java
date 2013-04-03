/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.pva.adapters;

import org.epics.pvdata.pv.PVString;
import org.epics.pvdata.pv.PVStructure;
import org.epics.vtype.VString;
import org.epics.vtype.VTypeToString;

/**
 * @author msekoranja
 */
public class PVFieldToVString extends AlarmTimeDisplayExtractor implements VString {
	// NOTE: VString does not extend Display, it's OK (will be undefined)
	
	protected final String value;

	/**
	 * @param pvField
	 * @param disconnected
	 */
	public PVFieldToVString(PVStructure pvField, boolean disconnected) {
		super(pvField, disconnected);

		PVString stringField = pvField.getStringField("value");
		if (stringField != null)
			value = stringField.get();
		else
			value = null;
	}

	@Override
	public String getValue() {
		return value;
	}

    @Override
    public String toString() {
        return VTypeToString.toString(this);
    }
	
}
