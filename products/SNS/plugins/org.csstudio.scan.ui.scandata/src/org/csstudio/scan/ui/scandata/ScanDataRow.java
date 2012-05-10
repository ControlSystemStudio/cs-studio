/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.scandata;

import java.util.Date;

import org.csstudio.scan.data.ScanSample;

/** One row of data for a table of scan samples
 *  @author Kay Kasemir
 */
public class ScanDataRow
{
	final private Date timestamp;

	final private ScanSample[] samples;

	/** Initialize
	 *  @param timestamp
	 *  @param samples
	 */
	public ScanDataRow(final Date timestamp, final ScanSample[] samples)
    {
	    this.timestamp = timestamp;
	    this.samples = samples;
    }

	/** @return Time stamp of this row */
	public Date getTimestamp()
    {
    	return timestamp;
    }

	/** @return samples in this row */
	public ScanSample[] getSamples()
    {
    	return samples;
    }

	/** @param index Index of sample within row
	 *  @return sample
	 */
	public ScanSample getSample(final int index)
    {
    	return samples[index];
    }
}
