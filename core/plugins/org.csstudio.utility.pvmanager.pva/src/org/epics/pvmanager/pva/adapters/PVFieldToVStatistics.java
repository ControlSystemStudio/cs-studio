/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.pva.adapters;

import org.epics.pvdata.pv.PVDouble;
import org.epics.pvdata.pv.PVLong;
import org.epics.pvdata.pv.PVStructure;
import org.epics.vtype.VStatistics;

/**
 * @author msekoranja
 *
 */
public class PVFieldToVStatistics extends AlarmTimeDisplayExtractor implements VStatistics {

	protected final Double average;
	protected final Double stdDev;
	protected final Double min;
	protected final Double max;
	protected final Integer n;
	
	/**
	 * @param pvField
	 * @param disconnected
	 */
	public PVFieldToVStatistics(PVStructure pvField, boolean disconnected) {
		super(pvField, disconnected);
		
		// note that the normativeType specification do not define this as average, e.g. can be mean too
		PVDouble df = pvField.getDoubleField("value");
		if (df != null)
			average = df.get();
		else
			average = Double.NaN;
		
		// note that the normativeType specification do not define this as stddev
		df = pvField.getDoubleField("dispersion");
		if (df != null)
			stdDev = df.get();
		else
			stdDev = Double.NaN;
			
		df = pvField.getDoubleField("min");
		if (df != null)
			min = df.get();
		else
			min = null;

		df = pvField.getDoubleField("max");
		if (df != null)
			max = df.get();
		else
			max = null;
		
		PVLong lf = pvField.getLongField("N");
		if (lf != null)
			n = (int)lf.get();
		else
			n = null;
		
	}

	@Override
	public Double getAverage() {
		return average;
	}

	@Override
	public Double getStdDev() {
		return stdDev;
	}

	@Override
	public Double getMin() {
		return min;
	}

	@Override
	public Double getMax() {
		return max;
	}

	@Override
	public Integer getNSamples() {
		return n;
	}


}
