/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.server;

import org.csstudio.scan.command.Comparison;
import org.csstudio.scan.condition.DeviceCondition;
import org.csstudio.scan.condition.NumericValueCondition;
import org.csstudio.scan.condition.TextValueCondition;
import org.csstudio.scan.device.Device;
import org.csstudio.scan.device.VTypeHelper;
import org.csstudio.scan.log.DataLog;
import org.epics.util.time.TimeDuration;
import org.epics.vtype.VType;

/** Utilities for command implementations
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ScanCommandUtil
{
    /** Write to device with readback, waiting forever, logging if the context
     *  was configured to auto-log
     *
     *  @param device_name Name of device
     *  @param value Value to write to the device
     *  @param tolerance Numeric tolerance when checking value
     *  @param timeout Timeout in seconds, <code>null</code> as "forever"
     *  @throws Exception on error
     */
    public static void write(final ScanContext context,
            final String device_name, final Object value,
            final double tolerance, final TimeDuration timeout) throws Exception
    {
    	write(context, device_name, value, device_name, true, tolerance, timeout);
    }
	
	/** Write to device with optional readback, logging if the context
     *  was configured to auto-log
     *
     *  @param device_name Name of device
     *  @param value Value to write to the device
     *  @param readback Readback device
     *  @param wait Wait for readback to match?
     *  @param tolerance Numeric tolerance when checking value
     *  @param timeout Timeout in seconds, <code>null</code> as "forever"
     *  @throws Exception on error
     */
    public static void write(final ScanContext context,
            final String device_name, final Object value, final String readback_name,
            final boolean wait, final double tolerance, final TimeDuration timeout) throws Exception
    {
        final Device device = context.getDevice(context.resolveMacros(device_name));

        // Separate read-back device, or use 'set' device?
        final Device readback;
        if (readback_name.isEmpty())
            readback = device;
        else
            readback = context.getDevice(context.resolveMacros(readback_name));

        //  Wait for the device to reach the value?
        final DeviceCondition condition;
        if (wait)
        {
            if (value instanceof Number)
            {
                final double desired = ((Number)value).doubleValue();
                condition = new NumericValueCondition(readback, Comparison.EQUALS, desired,
                        tolerance, timeout);
            }
            else
            {
                final String desired = value.toString();
                condition = new TextValueCondition(readback, Comparison.EQUALS, desired, timeout);
            }
        }
        else
            condition = null;

        // Perform write
        device.write(value);

        // Wait?
        if (condition != null)
            condition.await();

        // Log the value?
        if (context.isAutomaticLogMode())
        {
            final VType log_value = readback.read();
            final DataLog log = context.getDataLog();
            final long serial = log.getNextScanDataSerial();
            log.log(readback.getAlias(), VTypeHelper.createSample(serial, log_value));
        }
    }
}
