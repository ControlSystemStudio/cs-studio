/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
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
package org.csstudio.scan.commandimpl;

import java.io.PrintStream;

import org.csstudio.scan.command.ScanCommand;
import org.csstudio.scan.command.ScanCommandProperty;
import org.csstudio.scan.command.SimpleScanCommandFactory;
import org.csstudio.scan.device.Device;
import org.csstudio.scan.server.ScanCommandImpl;
import org.w3c.dom.Element;

/** {@link ScanCommandImpl} that delays the scan until all {@link Device}s are 'ready'
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class WaitForDevicesCommand extends ScanCommand
{
    final private Device[] devices;

    public WaitForDevicesCommand(final Device[] devices)
    {
        this.devices = devices;
    }

    public Device[] getDevices()
    {
        return devices;
    }

    /** {@inheritDoc} */
    @Override
    public ScanCommandProperty[] getProperties()
    {
        return new ScanCommandProperty[0];
    }

    /** {@inheritDoc} */
    @Override
    public void writeXML(final PrintStream out, final int level)
    {
        throw new Error("Internal command");
    }

    /** {@inheritDoc} */
    @Override
    public void readXML(final SimpleScanCommandFactory factory, final Element element)
            throws Exception
    {
        throw new Error("Internal command");
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return "Wait for devices";
    }
}
