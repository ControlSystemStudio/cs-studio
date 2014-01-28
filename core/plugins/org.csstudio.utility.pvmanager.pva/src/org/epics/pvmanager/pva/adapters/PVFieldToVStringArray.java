/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
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

	private final ListInt size;
	private final List<String> array;
	
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
			
			this.size = new ArrayInt(data.data.length);
			this.array = Collections.unmodifiableList(Arrays.asList(data.data));
		}
		else
		{
			this.size = null;
			this.array = null;
		}
	}

	/* (non-Javadoc)
	 * @see org.epics.pvmanager.data.Array#getSizes()
	 */
	@Override
	public ListInt getSizes() {
		return size;
	}

	/* (non-Javadoc)
	 * @see org.epics.pvmanager.data.VStringArray#getArray()
	 */
	@Override
	public List<String> getData() {
		return array;
	}
    
    @Override
    public String toString() {
        return VTypeToString.toString(this);
    }

}
