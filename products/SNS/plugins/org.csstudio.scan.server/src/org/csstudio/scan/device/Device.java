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
package org.csstudio.scan.device;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.csstudio.data.values.IValue;
import org.csstudio.data.values.ValueUtil;

/** Base interface for all devices
 *
 *  <p>This is the base for an implementation
 *  of a device described by a {@link DeviceInfo}.
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Device
{
	final private DeviceInfo info;
	final private List<DeviceListener> listeners = new CopyOnWriteArrayList<DeviceListener>();

	/** Initialize
	 *  @param info {@link DeviceInfo}
	 */
	public Device(final DeviceInfo info)
	{
		this.info = info;
	}

	/** @return {@link DeviceInfo} for this device */
	public DeviceInfo getInfo()
    {
	    return info;
    }

	/** @param listener Listener to add */
	public void addListener(final DeviceListener listener)
	{
	    listeners.add(listener);
	}

	/** @param listener Listener to remove */
	public void removeListener(final DeviceListener listener)
    {
        listeners.remove(listener);
    }

	/** Notify listeners that this device changed */
	protected void fireDeviceUpdate()
	{
	    for (DeviceListener listener : listeners)
	        listener.deviceChanged(this);
    }

	/** Will be called by scan engine before the scan
	 *  so that device can start whatever it needs to start
	 *  @throws Exception in case of error that should prohibit the scan
	 */
	public void start() throws Exception
	{
		// NOP
	}

	/** Check if the device is ready.
	 *  <p>Usually this means it has been started
	 *  and is connected to whatever resources it needs
	 *  (network, ...)
	 *  @return <code>true</code> if ready
	 */
	public boolean isReady()
	{
	    return true;
	}

	/** Will be called by scan engine at end of the scan
	 *  so that device can perform whatever shutdown operation
	 *  it needs
	 */
	public void stop()
	{
		// NOP
	}

	/** Read a value from the device
	 *  @return Current value of the device
	 *  @throws Exception on error: Cannot read, ...
	 */
	public IValue read() throws Exception
    {
		throw new Exception("Device '" + info.getName() + "' does not support reading");
    }

	/** Read a value from the device
	 *  @return Current value of the device as double. <code>NaN</code> when undefined.
	 *  @throws Exception on error: Cannot read, ...
	 */
	public double readDouble() throws Exception
    {
		final IValue value = read();
		if (value == null)
			return Double.NaN;
		return ValueUtil.getDouble(value);
    }


	/** Write a value to the device
	 *  @param value Value to write (Double, String)
	 *  @throws Exception on error: Cannot write, ...
	 */
	public void write(final Object value) throws Exception
    {
		throw new Exception("Device '" + info.getName() + "' does not support writing");
    }

	/** @return Human-readable representation of this device */
	@Override
	public String toString()
	{
	    return info.toString();
	}
}
