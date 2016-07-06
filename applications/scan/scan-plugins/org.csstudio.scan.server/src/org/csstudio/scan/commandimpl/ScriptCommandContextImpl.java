/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.commandimpl;

import java.time.Duration;
import java.time.Instant;

import org.csstudio.ndarray.NDArray;
import org.csstudio.scan.ScanSystemPreferences;
import org.csstudio.scan.command.ScanScriptContext;
import org.csstudio.scan.data.ScanData;
import org.csstudio.scan.data.ScanSample;
import org.csstudio.scan.data.ScanSampleFactory;
import org.csstudio.scan.device.Device;
import org.csstudio.scan.device.VTypeHelper;
import org.csstudio.scan.server.ScanCommandUtil;
import org.csstudio.scan.server.ScanContext;
import org.diirt.util.array.IteratorNumber;
import org.diirt.util.time.TimeDuration;
import org.diirt.vtype.VNumber;
import org.diirt.vtype.VNumberArray;
import org.diirt.vtype.VType;
import org.diirt.vtype.ValueUtil;

/** Implementation of the {@link ScanScriptContext}
 *
 *  <p>Exposes what's needed for scripts from the {@link ScanContext}
 *
 *  @author Kay Kasemir
 */
public class ScriptCommandContextImpl extends ScanScriptContext
{
    protected final static Duration value_check_timeout = TimeDuration.ofSeconds(ScanSystemPreferences.getValueCheckTimeout());
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
        return context.getDataLog().get().getScanData();
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
        final Instant timestamp = Instant.now();
        final IteratorNumber iter = data.getIterator();
        long serial = 0;
        while (iter.hasNext())
        {
            final ScanSample sample =
                ScanSampleFactory.createSample(timestamp , serial++, iter.nextDouble());
            context.getDataLog().get().log(device, sample);
        }
    }

    /** {@inheritDoc} */
    @Override
    public Object read(final String device_name) throws Exception
    {
        final Device device = context.getDevice(context.getMacros().resolveMacros(device_name));
        // Active read of current value
        final VType value = device.read(value_check_timeout);
        if (value instanceof VNumber)
            return ValueUtil.numericValueOf(value);
        if (value instanceof VNumberArray)
            return VTypeHelper.toDoubles(value);
        return VTypeHelper.toString(value);
    }

    /** {@inheritDoc} */
    @Override
    public void write(final String device_name, final Object value,
            final boolean completion,
            final String readback, final double tolerance,
            final Duration timeout) throws Exception
    {
        ScanCommandUtil.write(context, device_name, value, completion, readback != null, readback, tolerance, timeout);
    }
}
