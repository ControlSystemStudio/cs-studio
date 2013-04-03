/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.pva.adapters;


import org.epics.pvdata.pv.ByteArrayData;
import org.epics.pvdata.pv.PVByteArray;
import org.epics.pvdata.pv.PVStructure;
import org.epics.pvdata.pv.ScalarType;
import org.epics.vtype.VByteArray;
import org.epics.vtype.VTypeToString;
import org.epics.util.array.ArrayByte;
import org.epics.util.array.ArrayInt;
import org.epics.util.array.ListByte;
import org.epics.util.array.ListInt;

/**
 * @author msekoranja
 *
 */
public class PVFieldToVByteArray extends AlarmTimeDisplayExtractor implements VByteArray {

	private final ListInt size;
	private final ListByte list;
	
	/**
	 * @param pvField
	 * @param disconnected
	 */
	public PVFieldToVByteArray(PVStructure pvField, boolean disconnected) {
		super(pvField, disconnected);
		
		PVByteArray valueField =
			(PVByteArray)pvField.getScalarArrayField("value", ScalarType.pvByte);
		if (valueField != null)
		{
			ByteArrayData data = new ByteArrayData();
			valueField.get(0, valueField.getLength(), data);
			
			this.size = new ArrayInt(data.data.length);
			this.list = new ArrayByte(data.data);
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
	 * @see org.epics.pvmanager.data.VByteArray#getData()
	 */
	@Override
	public ListByte getData() {
		return list;
	}
    
    @Override
    public String toString() {
        return VTypeToString.toString(this);
    }

}
