/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.pva.adapters;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.epics.pvdata.pv.StringArrayData;
import org.epics.pvdata.pv.PVStringArray;
import org.epics.pvdata.pv.PVStructure;
import org.epics.pvdata.pv.ScalarType;
import org.epics.vtype.VStringArray;
import org.epics.vtype.VTypeToString;
import org.epics.util.array.ArrayInt;
import org.epics.util.array.ListInt;

/**
 * @author msekoranja
 *
 */
public class PVFieldToVStringArray extends AlarmTimeDisplayExtractor implements VStringArray {

	private final String[] array;
	private final List<String> data;
	
	/**
	 * @param pvField
	 * @param disconnected
	 */
	public PVFieldToVStringArray(PVStructure pvField, boolean disconnected) {
		super(pvField, disconnected);
		
		PVStringArray valueField =
			(PVStringArray)pvField.getScalarArrayField("value", ScalarType.pvString);
		if (valueField != null)
		{
			StringArrayData data = new StringArrayData();
			valueField.get(0, valueField.getLength(), data);
			
			this.array = data.data;
			this.data = Collections.unmodifiableList(Arrays.asList(array));
		}
		else
		{
			this.array = null;
			this.data = null;
		}
	}

	/* (non-Javadoc)
	 * @see org.epics.pvmanager.data.Array#getSizes()
	 */
	@Override
	public ListInt getSizes() {
		return new ArrayInt(array.length);
	}

	/* (non-Javadoc)
	 * @see org.epics.pvmanager.data.VStringArray#getArray()
	 */
	@Override
	public List<String> getData() {
		return data;
	}
    
    @Override
    public String toString() {
        return VTypeToString.toString(this);
    }

}
