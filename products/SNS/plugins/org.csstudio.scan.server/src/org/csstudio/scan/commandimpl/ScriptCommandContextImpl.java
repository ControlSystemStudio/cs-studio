/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.commandimpl;

import java.util.Date;

import org.csstudio.data.values.IStringValue;
import org.csstudio.data.values.IValue;
import org.csstudio.data.values.ValueUtil;
import org.csstudio.ndarray.NDArray;
import org.csstudio.scan.command.ScanScriptContext;
import org.csstudio.scan.data.ScanData;
import org.csstudio.scan.data.ScanSample;
import org.csstudio.scan.data.ScanSampleFactory;
import org.csstudio.scan.device.Device;
import org.csstudio.scan.server.ScanCommandUtil;
import org.csstudio.scan.server.ScanContext;
import org.epics.util.array.IteratorNumber;

/** Implementation of the {@link ScanScriptContext}
 *
 *  <p>Exposes what's needed for scripts from the {@link ScanContext}
 *
 *  @author Kay Kasemir
 */
public class ScriptCommandContextImpl extends ScanScriptContext
{
	final private ScanContext context;

	/** Initialize
	 *  @param context {@link ScanContext} of the command executing the script
	 */
	public ScriptCommandContextImpl(final ScanContext context)
	{
		this.context = context;
	}

	/** {@inheritDoc} */
	@Override
	public ScanData getScanData() throws Exception
	{
		return context.getDataLog().getScanData();
	}

    /** {@inheritDoc} */
    @SuppressWarnings("nls")
    @Override
	public void logData(final String device, final Object obj) throws Exception
    {
        // Check received data
        final NDArray data;
        if (obj instanceof NDArray)
            data = (NDArray) obj;
        else
        {
            try
            {
                data = NDArray.create(obj);
            }
            catch (IllegalArgumentException ex)
            {
                throw new Exception("Cannot log data of type " + obj.getClass().getName(), ex);
            }
        }
        // Log the data
        final Date timestamp = new Date();
        final IteratorNumber iter = data.getIterator();
        long serial = 0;
        while (iter.hasNext())
        {
            final ScanSample sample =
                ScanSampleFactory.createSample(timestamp , serial++, iter.nextDouble());
            context.getDataLog().log(device, sample);
        }
	}

    /** {@inheritDoc} */
    @Override
    public Object read(final String device_name) throws Exception
    {
        final Device device = context.getDevice(device_name);
        final IValue value = device.read();
        if (value instanceof IStringValue)
            return ValueUtil.getString(value);
        // If array-type, return double[]
        final double[] array = ValueUtil.getDoubleArray(value);
        if (array.length == 1)
        	return array[0];
		return array;
    }

    /** {@inheritDoc} */
	@Override
	public void write(final String device_name, final Object value, final String readback,
	        final boolean wait, final double tolerance, final double timeout) throws Exception
	{
	    ScanCommandUtil.write(context, device_name, value, readback, wait, tolerance, timeout);
	}
}
