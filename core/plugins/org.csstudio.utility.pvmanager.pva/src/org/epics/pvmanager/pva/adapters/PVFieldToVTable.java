/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.pva.adapters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.epics.pvdata.pv.ByteArrayData;
import org.epics.pvdata.pv.DoubleArrayData;
import org.epics.pvdata.pv.FloatArrayData;
import org.epics.pvdata.pv.IntArrayData;
import org.epics.pvdata.pv.PVByteArray;
import org.epics.pvdata.pv.PVDoubleArray;
import org.epics.pvdata.pv.PVField;
import org.epics.pvdata.pv.PVFloatArray;
import org.epics.pvdata.pv.PVIntArray;
import org.epics.pvdata.pv.PVScalarArray;
import org.epics.pvdata.pv.PVShortArray;
import org.epics.pvdata.pv.PVStringArray;
import org.epics.pvdata.pv.PVStructure;
import org.epics.pvdata.pv.ScalarType;
import org.epics.pvdata.pv.ShortArrayData;
import org.epics.pvdata.pv.StringArrayData;
import org.epics.util.array.ArrayByte;
import org.epics.util.array.ArrayDouble;
import org.epics.util.array.ArrayFloat;
import org.epics.util.array.ArrayInt;
import org.epics.util.array.ArrayShort;
import org.epics.vtype.VTable;
import org.epics.vtype.VTypeToString;
import org.epics.vtype.ValueUtil;

/**
 * @author msekoranja
 *
 */
public class PVFieldToVTable implements VTable {

    private final List<Class<?>> types;
    private final List<String> names;
    private final List<Object> values;
    private final int rowCount;
	
	/**
	 * @param pvField
	 * @param disconnected
	 */
	public PVFieldToVTable(PVStructure pvField, boolean disconnected) {
		
		PVStringArray labelsField =
			(PVStringArray)pvField.getScalarArrayField("labels", ScalarType.pvString);
		String[] labels;
		if (labelsField != null)
		{
			StringArrayData data = new StringArrayData();
			labelsField.get(0, labelsField.getLength(), data);
			labels = data.data;
		}
		else
			labels = null;
		
		PVStructure valueField = pvField.getStructureField("value");
		if (valueField != null)
		{
			PVField[] cols = valueField.getPVFields();
			int numCols = cols.length;
			types = new ArrayList<Class<?>>(numCols);
			names = new ArrayList<String>(numCols);
			values = new ArrayList<Object>(numCols);

			int maxRowCount = 0;

			int nameIndex = 0;
	        for (PVField pvColumn : valueField.getPVFields())
	        {
	        	PVScalarArray scalarArray = (PVScalarArray)pvColumn;
	        	int len = scalarArray.getLength(); 
	        	
	        	boolean skipped = false;
	        	if (scalarArray instanceof PVDoubleArray)
	        	{
		        	types.add(double.class);
		        	
		        	DoubleArrayData data = new DoubleArrayData();
		        	((PVDoubleArray)scalarArray).get(0, len, data);
		        	values.add(new ArrayDouble(data.data));
	        	}
	        	else if (scalarArray instanceof PVIntArray)
	        	{
		        	types.add(int.class);
		        	
		        	IntArrayData data = new IntArrayData();
		        	((PVIntArray)scalarArray).get(0, len, data);
		        	values.add(new ArrayInt(data.data));
	        	}
	        	else if (scalarArray instanceof PVStringArray)
	        	{
		        	types.add(String.class);
		        	
		        	StringArrayData data = new StringArrayData();
		        	((PVStringArray)scalarArray).get(0, len, data);
		        	values.add(Arrays.asList(data.data));
	        	}
	        	else if (scalarArray instanceof PVFloatArray)
	        	{
		        	types.add(float.class);
		        	
		        	FloatArrayData data = new FloatArrayData();
		        	((PVFloatArray)scalarArray).get(0, len, data);
		        	values.add(new ArrayFloat(data.data));
	        	}
	        	else if (scalarArray instanceof PVByteArray)
	        	{
		        	types.add(byte.class);
		        	
		        	ByteArrayData data = new ByteArrayData();
		        	((PVByteArray)scalarArray).get(0, len, data);
		        	values.add(new ArrayByte(data.data));
	        	}
	        	else if (scalarArray instanceof PVShortArray)
	        	{
		        	types.add(byte.class);
		        	
		        	ShortArrayData data = new ShortArrayData();
		        	((PVShortArray)scalarArray).get(0, len, data);
		        	values.add(new ArrayShort(data.data));
	        	}
	        	else
	        		skipped = true;
	        	
	        	if (!skipped)
	        	{
		        	names.add(labels != null ? labels[nameIndex] : pvColumn.getFieldName());
	        		if (len > maxRowCount) maxRowCount = len;
	        	}
	        	
	        	nameIndex++;
	        }
	        
	        rowCount = maxRowCount;
		}
		else
		{
			names = null;
			types = null;
			values = null;
			rowCount = -1;
		}
	}

    @Override
    public int getColumnCount() {
        return names.size();
    }

    @Override
    public int getRowCount() {
        return rowCount;
    }

    @Override
    public Class<?> getColumnType(int column) {
        return types.get(column);
    }

    @Override
    public String getColumnName(int column) {
        return names.get(column);
    }

    @Override
    public Object getColumnData(int column) {
        return values.get(column);
    }

	@Override
    public String toString() {
	return VTypeToString.toString(this);
    }

}
