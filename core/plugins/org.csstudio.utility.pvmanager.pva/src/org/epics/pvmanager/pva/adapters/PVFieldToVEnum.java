/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.pva.adapters;

import java.util.Arrays;
import java.util.List;

import org.epics.pvdata.pv.PVInt;
import org.epics.pvdata.pv.PVStringArray;
import org.epics.pvdata.pv.PVStructure;
import org.epics.pvdata.pv.ScalarType;
import org.epics.pvdata.pv.StringArrayData;
import org.epics.vtype.VEnum;
import org.epics.vtype.VTypeToString;

public class PVFieldToVEnum extends AlarmTimeDisplayExtractor implements VEnum {
	
	protected final int index;
	protected final List<String> labels;
	
	public PVFieldToVEnum(PVStructure pvField, boolean disconnected)
	{
		super(pvField, disconnected);
	
		PVStructure enumField = pvField.getStructureField("value");
		if (enumField != null)
		{
			PVStringArray labelsField =
				(PVStringArray)enumField.getScalarArrayField("choices", ScalarType.pvString);
			if (labelsField != null)
			{
				StringArrayData data = new StringArrayData();
				labelsField.get(0, labelsField.getLength(), data);
				labels = Arrays.asList(data.data);
				
				PVInt indexField = enumField.getIntField("index");
				if (indexField != null)
				{
					index = indexField.get();
				}
				else
				{
					index = -1;
				}
				
				return;
			}
		}
		
		// error
		index = -1;
		labels = null;
	}
	
	/* (non-Javadoc)
	 * @see org.epics.pvmanager.data.Enum#getLabels()
	 */
	@Override
	public List<String> getLabels() {
		return labels;
	}

	/* (non-Javadoc)
	 * @see org.epics.pvmanager.data.VEnum#getValue()
	 */
	@Override
	public String getValue() {
		if (labels != null && index >= 0 && index < labels.size())
			return labels.get(index);
		else
			return Integer.toString(index);		// return integer as string as fallback
	}

	/* (non-Javadoc)
	 * @see org.epics.pvmanager.data.VEnum#getIndex()
	 */
	@Override
	public int getIndex() {
		return index;
	}

    @Override
    public String toString() {
        return VTypeToString.toString(this);
    }

}
