/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.pva.adapters;


import java.util.List;

import org.epics.pvdata.pv.LongArrayData;
import org.epics.pvdata.pv.PVLongArray;
import org.epics.pvdata.pv.PVStructure;
import org.epics.pvdata.pv.ScalarType;
import org.epics.util.array.ArrayInt;
import org.epics.util.array.ArrayLong;
import org.epics.util.array.ListInt;
import org.epics.util.array.ListLong;
import org.epics.vtype.ArrayDimensionDisplay;
import org.epics.vtype.VLongArray;
import org.epics.vtype.VTypeToString;
import org.epics.vtype.ValueUtil;

/**
 * @author msekoranja
 *
 */
public class PVFieldToVLongArray extends AlarmTimeDisplayExtractor implements VLongArray {

	private final ListInt size;
	private final ListLong list;
	
	/**
	 * @param pvField
	 * @param disconnected
	 */
	public PVFieldToVLongArray(PVStructure pvField, String fieldName, boolean disconnected) {
		super(pvField, disconnected);
		
		PVLongArray valueField =
			(PVLongArray)pvField.getScalarArrayField(fieldName, ScalarType.pvLong);
		if (valueField != null)
		{
			LongArrayData data = new LongArrayData();
			valueField.get(0, valueField.getLength(), data);
			
			this.size = new ArrayInt(data.data.length);
			this.list = new ArrayLong(data.data);
		}
		else
		{
			size = null;
			list = null;
		}
	}

	public PVFieldToVLongArray(PVStructure pvField, boolean disconnected) {
		this(pvField, "value", disconnected);
	}
	
	/* (non-Javadoc)
	 * @see org.epics.pvmanager.data.Array#getSizes()
	 */
	@Override
	public ListInt getSizes() {
		return size;
	}

	/* (non-Javadoc)
	 * @see org.epics.pvmanager.data.VIntArray#getData()
	 */
	@Override
	public ListLong getData() {
		return list;
	}
    
    @Override
    public String toString() {
        return VTypeToString.toString(this);
    }

    @Override
    public List<ArrayDimensionDisplay> getDimensionDisplay() {
        return ValueUtil.defaultArrayDisplay(this);
    }

}
