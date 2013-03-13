/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.pva.adapters;


import org.epics.pvdata.pv.IntArrayData;
import org.epics.pvdata.pv.PVIntArray;
import org.epics.pvdata.pv.PVStructure;
import org.epics.pvdata.pv.ScalarType;
import org.epics.vtype.VIntArray;
import org.epics.vtype.VTypeToString;
import org.epics.util.array.ArrayInt;
import org.epics.util.array.ListInt;

/**
 * @author msekoranja
 *
 */
public class PVFieldToVIntArray extends AlarmTimeDisplayExtractor implements VIntArray {

	private final int[] array;
	private final ListInt list;
	
	/**
	 * @param pvField
	 * @param disconnected
	 */
	public PVFieldToVIntArray(PVStructure pvField, boolean disconnected) {
		super(pvField, disconnected);
		
		PVIntArray valueField =
			(PVIntArray)pvField.getScalarArrayField("value", ScalarType.pvInt);
		if (valueField != null)
		{
			IntArrayData data = new IntArrayData();
			valueField.get(0, valueField.getLength(), data);
			
			this.array = data.data;
			this.list = new ListInt() {
				
				@Override
				public int size() {
					return array.length;
				}
				
				@Override
				public int getInt(int index) {
					return array[index];
				}
			};
		}
		else
		{
			array = null;
			list = null;
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
	 * @see org.epics.pvmanager.data.VIntArray#getData()
	 */
	@Override
	public ListInt getData() {
		return list;
	}
    
    @Override
    public String toString() {
        return VTypeToString.toString(this);
    }

}
