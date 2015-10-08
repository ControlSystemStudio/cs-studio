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

import java.util.concurrent.TimeoutException;

import org.csstudio.scan.ScanSystemPreferences;
import org.csstudio.scan.command.Comparison;
import org.csstudio.scan.device.Device;
import org.csstudio.scan.device.DeviceListener;
import org.csstudio.scan.device.VTypeHelper;
import org.diirt.util.time.TimeDuration;

/** Condition that waits for a Device to reach a certain string value.
 *
 *  <p>The current value of the device is monitored,
 *  and not all {@link Comparison} types are supported.
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class TextValueCondition implements DeviceCondition, DeviceListener
{
    protected final static TimeDuration value_check_timeout = TimeDuration.ofSeconds(ScanSystemPreferences.getValueCheckTimeout());

    /** Device to monitor */
    final private Device device;

    /** Comparison */
    final private Comparison comparison;

    /** Desired value of device */
    private String desired_value;

    /** Timeout in seconds, <code>null</code> to "wait forever" */
    final private TimeDuration timeout;

    /** Updated by device listener */
    private volatile boolean is_condition_met;

    /** Updated by device listener */
    private volatile Exception error = null;

    /** Initialize
     *  @param device {@link Device} where values should be read
     *  @param comparison Comparison to use
     *  @param desired_value Desired numeric value of device
     *  @param timeout Time out in seconds, or <code>null</code> for "wait forever"
     */
    public TextValueCondition(final Device device, final Comparison comparison,
            final String desired_value,
            final TimeDuration timeout)
    {
        this.device = device;
        this.comparison = comparison;
        this.timeout = timeout;
        setDesiredValue(desired_value);
    }

    /** @param desired_value (New) desired value, replacing the one set at initialization time */
    public void setDesiredValue(final String desired_value)
    {
        this.desired_value = desired_value;
    }

    /** Wait for value of device to reach the desired value (within tolerance)
     *  @throws TimeoutException on timeout
     *  @throws Exception on interruption or device read error
     */
    @Override
    public void await() throws TimeoutException, Exception
    {
        final WaitWithTimeout timeout = new WaitWithTimeout(this.timeout);

        // Fetch initial value with get-callback
        // (will be obtained by device.read() in listener)
        device.read(value_check_timeout);

        device.addListener(this);
        try
        {
            // Synchronize to avoid the following situation:
            // 1. not at desired value
            // 2. device changes and we would be notified
            // 3. ... but that's before we call wait, so we wait forever
            synchronized (this)
            {
                is_condition_met = isConditionMet();
                while (! is_condition_met)
                {   // Wait for update from device
                    if (timeout.waitUntilTimeout(this))
                        throw new TimeoutException("Timeout while waiting for " + device
                                    + " " + comparison + " " + desired_value);
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

    /** Determine if the condition is currently met
     *  @return <code>true</code> if condition is met
     *  @throws Exception on error reading from the device
     */
    public boolean isConditionMet() throws Exception
    {
        final String value = VTypeHelper.toString(device.read());
        switch (comparison)
        {
        case EQUALS:
            return desired_value.equals(value);
        case AT_LEAST:
            return value.compareTo(desired_value) >= 0;
        case ABOVE:
            return value.compareTo(desired_value) > 0;
        case AT_MOST:
            return value.compareTo(desired_value) <= 0;
        case BELOW:
            return value.compareTo(desired_value) < 0;
        default:
            throw new Error("Condition not implemented for strings: " + comparison);
        }
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
                is_condition_met = isConditionMet();
            }
            catch (Exception ex)
            {
                is_condition_met = false;
                error = ex;
            }
            // Notify await() so it can check again.
            notifyAll();
        }
    }

    /** @return Debug representation */
    @Override
    public String toString()
    {
        return "Wait for '" + device + "' "
                + comparison + " '" + desired_value
                + "', timeout=" + timeout;
    }
}
