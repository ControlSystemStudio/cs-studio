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

import java.io.Serializable;

/** Information about a device
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class DeviceInfo implements Serializable
{
	/** Serialization ID */
	final private static long serialVersionUID = 1L;

	final private String name;
	final private String alias;
	final private boolean scan;
	final private boolean log;

	/** Initialize
	 *  @param name Device name as understood by the control system
	 *  @param alias Alias for the device that is used in GUI and scans
	 */
	public DeviceInfo(final String name, final String alias, final boolean scan, final boolean log)
    {
	    this.name = name;
	    this.alias = alias;
	    this.scan = scan;
	    this.log = log;
    }

	/** @return Name of the device */
	public String getName()
	{
		return name;
	}

	/** @return Alias of the device */
    public String getAlias()
    {
        return alias;
    }

    /** @return <code>true</code> if device can be scanned */
	public boolean isScannable()
    {
        return scan;
    }

    /** @return <code>true</code> if device can be logged/monitored */
    public boolean isLoggable()
    {
        return log;
    }

    /** Hash on name
     *  {@inheritDoc}
     */
    @Override
    public int hashCode()
    {
        return name.hashCode();
    }

    /** Compare by name
     *  {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj)
    {
        if (! (obj instanceof DeviceInfo))
            return false;
        final DeviceInfo other = (DeviceInfo) obj;
        return name.equals(other.name);
    }

    /** @return Debug representation */
    @Override
	public String toString()
	{
        final StringBuilder buf = new StringBuilder();
        buf.append(alias);
        if (! alias.equals(name))
        	buf.append(" [").append(name).append("]");
        if (log)
            buf.append(" (loggable)");
        if (scan)
            buf.append(" (scannable)");
        return buf.toString();
	}
}
