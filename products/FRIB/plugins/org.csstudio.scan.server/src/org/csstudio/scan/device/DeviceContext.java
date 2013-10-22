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

import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.csstudio.scan.ScanSystemPreferences;
import org.csstudio.scan.server.internal.PathStreamTool;

/** Context that maintains {@link Device}s: Create, start, get, stop.
 *
 *  <p>This is currently handling PV devices.
 *  Fundamentally, another type of device might
 *  be added in the future.
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class DeviceContext
{
    /** Map of device names to {@link Device} */
    final private Map<String, Device> devices = new HashMap<String, Device>();

    /** @return Default {@link DeviceContext}, initialized from preferences */
    public static DeviceContext getDefault() throws Exception
    {
    	final String path = ScanSystemPreferences.getBeamlineConfigPath();
        final InputStream config_stream = PathStreamTool.openStream(path);
        final DeviceInfo[] infos = BeamlineDeviceInfoReader.read(config_stream);

        // Create context with those devices
        final DeviceContext context = new DeviceContext();
        for (DeviceInfo info : infos)
            context.addPVDevice(info);
		return context;
    }

    /** Initialize empty device context
     *  @see #getDefault()
     */
    public DeviceContext()
    {
        // NOP
    }

    /** Add a PV-based {@link Device} to the context.
     *  When adding a device with a name
     *  that is already in the context,
     *  the original device will be replaced.
     *
     *  @param info {@link DeviceInfo}
     */
    public synchronized void addPVDevice(final DeviceInfo info) throws Exception
    {
        devices.put(info.getAlias(), new PVDevice(info));
    }

    /** Get a device by alias
     *  @param alias
     *  @return {@link Device} with that name
     *  @throws Exception when device name not known
     */
    public synchronized Device getDeviceByAlias(final String alias) throws Exception
    {
        final Device device = devices.get(alias);
        if (device == null)
            throw new Exception("Unknown device '" + alias + "'");
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
