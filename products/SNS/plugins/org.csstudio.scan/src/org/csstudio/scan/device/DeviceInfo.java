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

	/** Initialize
	 *  @param name Device name as understood by the control system
	 *  @param alias Alias, may be preferred for GUI
	 */
	public DeviceInfo(final String name, final String alias)
    {
	    this.name = name;
	    this.alias = alias;
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

	/** @return Debug representation */
    @Override
	public String toString()
	{
		return alias + " [" + name + "]";
	}
}
