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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/** Factory for creating {@link Device}s
 * 
 *  <p>This is currently handling PV devices.
 *  Fundamentally, another type of device might
 *  be added in the future.
 *  
 *  @author Kay Kasemir
 */
public class DeviceContext
{
    /** Map of device names to {@link Device} */
    final private Map<String, Device> devices = new HashMap<String, Device>();

    /** Add a PV-based {@link Device} to the context.
     *  When adding a device with a name
     *  that is already in the context,
     *  the original device will be replaced.
     *
     *  @param name Name of the device, used to access it in scan commands
     *  @param path Path to the device, i.e. the control system PV
     */
    public synchronized void addPVDevice(final String name, final String path) throws Exception
    {
        devices.put(name, new PVDevice(name, path));
    }

    /** Get a device by name
     *  @param name
     *  @return {@link Device} with that name
     *  @throws Exception when device name not known
     */
    public synchronized Device getDevice(final String name) throws Exception
    {
        final Device device = devices.get(name);
        if (device == null)
            throw new Exception("Unknown device '" + name + "'");
        return device;
    }

    /** @return All Devices */
    public synchronized Device[] getDevices()
    {
        final Collection<Device> devs = devices.values();
        return devs.toArray(new Device[devs.size()]);
    }

    /** Start all devices
     *  @throws Exception on error with a device
     */
    public synchronized void startDevices() throws Exception
    {
        for (Device device : devices.values())
            device.start();
    }

    /** Stop all devices */
    public synchronized void stopDevices()
    {
        for (Device device : devices.values())
            device.stop();
    }
}
