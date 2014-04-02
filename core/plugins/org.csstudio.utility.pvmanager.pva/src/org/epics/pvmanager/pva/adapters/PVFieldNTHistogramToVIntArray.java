/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.pva.adapters;


import java.util.ArrayList;
import java.util.List;

import org.epics.pvdata.pv.DoubleArrayData;
import org.epics.pvdata.pv.IntArrayData;
import org.epics.pvdata.pv.PVDoubleArray;
import org.epics.pvdata.pv.PVIntArray;
import org.epics.pvdata.pv.PVStructure;
import org.epics.pvdata.pv.ScalarType;
import org.epics.util.array.ArrayDouble;
import org.epics.util.array.ArrayInt;
import org.epics.util.array.ListInt;
import org.epics.vtype.ArrayDimensionDisplay;
import org.epics.vtype.VIntArray;
import org.epics.vtype.VTypeToString;
import org.epics.vtype.ValueFactory;

/**
 * @author msekoranja
 *
 */
public class PVFieldNTHistogramToVIntArray extends AlarmTimeDisplayExtractor implements VIntArray {

	private static final String RANGES_UNIT = "";
	
	private final ListInt size;
	private final ListInt list;
	
	private final List<ArrayDimensionDisplay> displays = new ArrayList<ArrayDimensionDisplay>(1);
	
	/**
	 * @param pvField
	 * @param disconnected
	 */
	public PVFieldNTHistogramToVIntArray(PVStructure pvField, boolean disconnected) {
		super(pvField, disconnected);
		
		PVIntArray valueField =
			(PVIntArray)pvField.getScalarArrayField("value", ScalarType.pvInt);
		if (valueField != null)
		{
			IntArrayData data = new IntArrayData();
			valueField.get(0, valueField.getLength(), data);
			
			this.size = new ArrayInt(data.data.length);
			this.list = new ArrayInt(data.data);
		}
		else
		{
			size = null;
			list = null;
		}

		PVDoubleArray rangesField =
				(PVDoubleArray)pvField.getScalarArrayField("ranges", ScalarType.pvDouble);
		if (rangesField != null)
		{
			DoubleArrayData data = new DoubleArrayData();
			rangesField.get(0, rangesField.getLength(), data);
			
			ArrayDimensionDisplay display = ValueFactory.newDisplay(new ArrayDouble(data.data), RANGES_UNIT);
			displays.add(display);
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

    @Override
    public List<ArrayDimensionDisplay> getDimensionDisplay() {
        return displays;
    }

}
