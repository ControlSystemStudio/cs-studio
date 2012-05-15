/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.command;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.scan.data.DataFormatter;
import org.csstudio.scan.data.ScanData;
import org.csstudio.scan.data.ScanSample;
import org.csstudio.scan.data.SpreadsheetScanDataIterator;

/** Context in which a {@link ScriptCommand}'s script is executed
 *
 *  <p>Script can access the scan data that has been accumulated to far,
 *  and write to devices.
 *
 *  TODO Script needs to define
 *  <code>execute(ScriptCommandContext context)</code>
 *  TODO Have Java interface? Need jython class, or just invoke a <code>def execute(context)</code>?
 *
 *  @author Kay Kasemir
 */
abstract public class ScriptCommandContext
{
    /** @return {@link ScanData} of currently logged data or <code>null</code>
	 *  @throws Exception on error
     */
    abstract public ScanData getScanData() throws Exception;

    /** Get data for devices.
     *
     *  <p>Data will be interpolated onto matching time stamps
     *  via 'staircase' interpolation, holding a value
     *  until it changes.
	 *  @throws Exception on error
     */
    public double[][] getData(final String... devices) throws Exception
    {
    	SpreadsheetScanDataIterator sheet = new SpreadsheetScanDataIterator(getScanData(), devices);
    	final List<List<Double>> data = new ArrayList<List<Double>>(devices.length);
    	for (int i=0; i<devices.length; ++i)
    		data.add(new ArrayList<Double>());
    	while (sheet.hasNext())
    	{
    		final ScanSample[] samples = sheet.getSamples();
        	for (int i=0; i<devices.length; ++i)
        		data.get(i).add(DataFormatter.asDouble(samples[i]));
    	}
    	sheet = null;

    	// Convert to plain array
    	final double[][] result = new double[devices.length][];
    	for (int i=0; i<devices.length; ++i)
    	{
    		final List<Double> list = data.get(i);
    		result[i] = new double[list.size()];
    		for (int j=list.size()-1; j>=0; --j)
    			result[i][j] = list.get(j);
    	}
    	return result;
    }

    /** Write to device, waiting for readback with default tolerance and timeout
     *  @param device_name Name of device and readback
     *  @param value Value to write to the device
     *  @throws Exception on error
     */
	public void write(final String device_name, final Object value) throws Exception
    {
		write(device_name, value, true);
    }

    /** Write to device, with or without waiting for readback
     *  @param device_name Name of device and readback (if used)
     *  @param value Value to write to the device
     *  @param wait Wait for readback to match?
     *  @throws Exception on error
     */
    public void write(final String device_name, final Object value, final boolean wait) throws Exception
    {
    	write(device_name, value, device_name, wait, 0.1, 0.0);
    }

    /** Write to device, using alternate readback device
     *  @param device_name Name of device
     *  @param value Value to write to the device
     *  @param readback Readback device
     *  @throws Exception on error
     */
    public void write(final String device_name, final Object value,
            final String readback) throws Exception
    {
    	write(device_name, value, readback, true, 0.1, 0.0);
    }

    /** Write to device
	 *  @param device_name Name of device
	 *  @param value Value to write to the device
	 *  @param readback Readback device
	 *  @param wait Wait for readback to match?
     *  @param tolerance Numeric tolerance when checking value
     *  @param timeout Timeout in seconds, 0 as "forever"
     *  @throws Exception on error
	 */
	abstract public void write(final String device_name, final Object value,
	        final String readback, final boolean wait,
            final double tolerance, final double timeout) throws Exception;
}
