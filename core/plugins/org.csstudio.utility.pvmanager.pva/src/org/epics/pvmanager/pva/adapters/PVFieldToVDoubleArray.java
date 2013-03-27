/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.pva.adapters;


import org.epics.pvdata.pv.DoubleArrayData;
import org.epics.pvdata.pv.PVDoubleArray;
import org.epics.pvdata.pv.PVStructure;
import org.epics.pvdata.pv.ScalarType;
import org.epics.vtype.VDoubleArray;
import org.epics.vtype.VTypeToString;
import org.epics.util.array.ArrayDouble;
import org.epics.util.array.ArrayInt;
import org.epics.util.array.ListDouble;
import org.epics.util.array.ListInt;

/**
 * @author msekoranja
 *
 */
public class PVFieldToVDoubleArray extends AlarmTimeDisplayExtractor implements VDoubleArray {

	private final ListInt size;
	private final ListDouble list;
	
	/**
	 * @param pvField
	 * @param disconnected
	 */
	public PVFieldToVDoubleArray(PVStructure pvField, boolean disconnected) {
		super(pvField, disconnected);
		
		PVDoubleArray valueField =
			(PVDoubleArray)pvField.getScalarArrayField("value", ScalarType.pvDouble);
		if (valueField != null)
		{
			DoubleArrayData data = new DoubleArrayData();
			valueField.get(0, valueField.getLength(), data);
			
			this.size = new ArrayInt(data.data.length);
			this.list = new ArrayDouble(data.data);
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
	 * @see org.epics.pvmanager.data.VDoubleArray#getData()
	 */
	@Override
	public ListDouble getData() {
		return list;
	}
    
    @Override
    public String toString() {
        return VTypeToString.toString(this);
    }

}
