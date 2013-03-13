/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.pva.adapters;


import org.epics.pvdata.pv.ShortArrayData;
import org.epics.pvdata.pv.PVShortArray;
import org.epics.pvdata.pv.PVStructure;
import org.epics.pvdata.pv.ScalarType;
import org.epics.vtype.VShortArray;
import org.epics.vtype.VTypeToString;
import org.epics.util.array.ArrayInt;
import org.epics.util.array.ListInt;
import org.epics.util.array.ListShort;

/**
 * @author msekoranja
 *
 */
public class PVFieldToVShortArray extends AlarmTimeDisplayExtractor implements VShortArray {

	private final short[] array;
	private final ListShort list;
	
	/**
	 * @param pvField
	 * @param disconnected
	 */
	public PVFieldToVShortArray(PVStructure pvField, boolean disconnected) {
		super(pvField, disconnected);
		
		PVShortArray valueField =
			(PVShortArray)pvField.getScalarArrayField("value", ScalarType.pvShort);
		if (valueField != null)
		{
			ShortArrayData data = new ShortArrayData();
			valueField.get(0, valueField.getLength(), data);
			
			this.array = data.data;
			this.list = new ListShort() {
				
				@Override
				public int size() {
					return array.length;
				}
				
				@Override
				public short getShort(int index) {
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
	 * @see org.epics.pvmanager.data.VShortArray#getData()
	 */
	@Override
	public ListShort getData() {
		return list;
	}
    
    @Override
    public String toString() {
        return VTypeToString.toString(this);
    }

}
