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
package org.csstudio.scan.server;

import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;

/** Memory usage info
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class MemoryInfo  implements Serializable
{
    /** Serialization ID */
    final private static long serialVersionUID = ScanServer.SERIAL_VERSION;

	final private static double MB = 1024*1024;

    final private long used_mem, max_mem;

    /** Initialize
     */
    public MemoryInfo()
    {
		final MemoryUsage heap = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
		used_mem = heap.getUsed();
	    max_mem = heap.getMax();
    }

	/** @return Used memory (kB) */
	public long getUsedMem()
    {
    	return used_mem;
    }

	/** @return Maximum memory that server will try to use (kB) */
	public long getMaxMem()
    {
    	return max_mem;
    }

	/** @return Memory usage in percent of max: 0..100 */
	public double getMemoryPercentage()
	{
		return used_mem * 100.0 / max_mem;
	}

	/** @return Memory usage in percent of max */
    public String getMemoryInfo()
	{
		return String.format("%.1f MB / %.1f MB (%.1f %%)",
				used_mem / MB, max_mem / MB, getMemoryPercentage());
	}

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        final StringBuilder buf = new StringBuilder();
        buf.append("Memory: ").append(getMemoryInfo()).append("\n");
        return buf.toString();
    }
}
