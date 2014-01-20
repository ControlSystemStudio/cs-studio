/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.pva.adapters;


import java.util.List;
import org.epics.pvdata.pv.FloatArrayData;
import org.epics.pvdata.pv.PVFloatArray;
import org.epics.pvdata.pv.PVStructure;
import org.epics.pvdata.pv.ScalarType;
import org.epics.vtype.VFloatArray;
import org.epics.vtype.VTypeToString;
import org.epics.util.array.ArrayFloat;
import org.epics.util.array.ArrayInt;
import org.epics.util.array.ListFloat;
import org.epics.util.array.ListInt;
import org.epics.vtype.ArrayDimensionDisplay;
import org.epics.vtype.ValueUtil;

/**
 * @author msekoranja
 *
 */
public class PVFieldToVFloatArray extends AlarmTimeDisplayExtractor implements VFloatArray {

	private final ListInt size;
	private final ListFloat list;
	
	/**
	 * @param pvField
	 * @param disconnected
	 */
	public PVFieldToVFloatArray(PVStructure pvField, boolean disconnected) {
		super(pvField, disconnected);
		
		PVFloatArray valueField =
			(PVFloatArray)pvField.getScalarArrayField("value", ScalarType.pvFloat);
		if (valueField != null)
		{
			FloatArrayData data = new FloatArrayData();
			valueField.get(0, valueField.getLength(), data);
			
			this.size = new ArrayInt(data.data.length);
			this.list = new ArrayFloat(data.data);
		}
		else
		{
			size = null;
			list = null;
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
	 * @see org.epics.pvmanager.data.VFloatArray#getData()
	 */
	@Override
	public ListFloat getData() {
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
