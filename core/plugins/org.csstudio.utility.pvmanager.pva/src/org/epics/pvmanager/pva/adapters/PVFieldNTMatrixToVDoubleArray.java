/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.pva.adapters;


import org.epics.pvdata.pv.IntArrayData;
import org.epics.pvdata.pv.PVIntArray;
import org.epics.pvdata.pv.PVStructure;
import org.epics.pvdata.pv.ScalarType;
import org.epics.util.array.ArrayInt;
import org.epics.util.array.ListInt;

/**
 * @author msekoranja
 *
 */
public class PVFieldNTMatrixToVDoubleArray extends PVFieldToVDoubleArray {

	private final int rows;
	private final int cols;
	
	/**
	 * @param pvField
	 * @param disconnected
	 */
	public PVFieldNTMatrixToVDoubleArray(PVStructure pvField, boolean disconnected) {
		super(pvField, disconnected);
		
		PVIntArray dimField =
			(PVIntArray)pvField.getScalarArrayField("dim", ScalarType.pvInt);
		if (dimField != null)
		{
			int dims = dimField.getLength();
			IntArrayData data = new IntArrayData();
			dimField.get(0, dims, data);

			rows = data.data[0];
		    cols = (dims == 2) ? data.data[1] : 1;

			// check for if (rows <= 0 || cols <= 0)
		}
		else
		{
	    	// column vector
	    	rows = getData().size();
	    	cols = 1;
		}
	}

	/* (non-Javadoc)
	 * @see org.epics.pvmanager.data.Array#getSizes()
	 */
	@Override
	public ListInt getSizes() {
		return new ArrayInt(rows, cols);
	}

}
