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

/** {@link ScanCommand} that sets a device to a value
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class SetCommand extends BaseCommand
{
	/** Serialization ID */
    final private static long serialVersionUID = ScanServer.SERIAL_VERSION;

    final private String device_name;
	final private Object value;

	/** Initialize
	 *  @param device_name Name of device
	 *  @param value Value to write to the device
	 */
	public SetCommand(final String device_name, Object value)
    {
		this.device_name = device_name;
		this.value = value;
    }

	/** @return Name of device to set */
	public String getDeviceName()
    {
        return device_name;
    }

	/** @return Value to write to device */
    public Object getValue()
    {
        return value;
    }

    /** {@inheritDoc} */
	@Override
	public String toString()
	{
	    return "Set '" + device_name + "' = " + value;
	}
}
