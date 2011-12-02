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
package org.csstudio.scan.command;

import org.csstudio.scan.server.ScanServer;

/** {@link CommandImpl} that reads data from devices and logs it
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class LogCommand extends BaseCommand
{
    /** Serialization ID */
    final private static long serialVersionUID = ScanServer.SERIAL_VERSION;

    final private String[] device_names;

	/** Initialize
	 *  @param device_names List of device names
	 */
	public LogCommand(final String... device_names)
    {
		this.device_names = device_names;
    }

	/** Initialize
     *  @param device_name Single device name
     */
    public LogCommand(final String device_name)
    {
        this(new String[] { device_name });
    }

	/** @return Names of devices to read and log */
    public String[] getDeviceNames()
    {
        return device_names;
    }

    /** {@inheritDoc} */
	@Override
	public String toString()
	{
		final StringBuilder buf = new StringBuilder();
		buf.append("Log ");
		for (int i=0; i<device_names.length; ++i)
		{
			final String device_name = device_names[i];
			if (i > 0)
				buf.append(", ");
			buf.append("'" + device_name + "'");
		}
	    return buf.toString();
	}
}
