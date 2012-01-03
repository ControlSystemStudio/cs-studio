/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * The scan engine idea is based on the "ScanEngine" developed
 * by the Software Services Group (SSG),  Advanced Photon Source,
 * Argonne National Laboratory,
 * Copyright (c) 2011 , UChicago Argonne, LLC.
 * 
 * This implementation, however, contains no SSG "ScanEngine" source code
 * and is not endorsed by the SSG authors.
 ******************************************************************************/
package org.csstudio.scan.condition;

import org.csstudio.data.values.ValueUtil;
import org.csstudio.scan.device.Device;
import org.csstudio.scan.device.DeviceListener;

/** Condition that waits for a Device to reach a certain value
 *  @author Kay Kasemir
 */
public class DeviceValueCondition implements DeviceListener
{
    final private Device device;
    private double desired_value;
    final private double tolerance;
    private volatile boolean at_desired_value;
    private volatile Exception error = null;

    /** Initialize
     *  @param device {@link Device} where values should be read
     *  @param desired_value Desired numeric value of device
     *  @param tolerance Tolerance, e.g. 0.1
     */
    public DeviceValueCondition(final Device device, final double desired_value, final double tolerance)
    {
        this.device = device;
        this.tolerance = Math.abs(tolerance);
        setDesiredValue(desired_value);
    }

    /** @param desired_value (New) desired value, replacing the one set at initialization time */
    public void setDesiredValue(final double desired_value)
    {
        this.desired_value = desired_value;
    }

    /** Wait for value of device to reach the desired value (within tolerance)
     *  @throws Exception on interruption or device read error
     */
    public void await() throws Exception
    {
        device.addListener(this);
        try
        {
            // Synchronize to avoid the following situation:
            // 1. not at desired value
            // 2. device changes and we would be notified
            // 3. ... but that's before we call wait, so we wait forever
            synchronized (this)
            {
                at_desired_value = check();
                while (! at_desired_value)
                {   // Wait for update from device
                    wait();
                    if (error != null)
                        throw error;
                }
            }
        }
        finally
        {
            device.removeListener(this);
        }
    }

    private boolean check() throws Exception
    {
        final double value = ValueUtil.getDouble(device.read());
        return Math.abs(desired_value - value) <= tolerance;
    }

    /** Trigger another check of device's value
     *  {@inheritDoc}
     */
    @Override
    public void deviceChanged(final Device device)
    {
        synchronized (this)
        {
            try
            {
                at_desired_value = check();
            }
            catch (Exception ex)
            {
                at_desired_value = false;
                error = ex;
            }
            // Notify await() so it can check again.
            notifyAll();
        }
    }
}
