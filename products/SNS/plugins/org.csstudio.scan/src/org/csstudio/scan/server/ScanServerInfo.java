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
import java.util.Date;

import org.csstudio.scan.data.DataFormatter;

/** Scan server info
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ScanServerInfo  implements Serializable
{
    /** Serialization ID */
    final private static long serialVersionUID = ScanServer.SERIAL_VERSION;

	final private static double MB = 1024*1024;

    final private String version;
    final private Date start_time;
    final private String beamline_config;
    final private long used_mem, max_mem;

    /** Initialize
     *  @param version
     *  @param start_time
     *  @param beamline_config
     *  @param used_mem
     *  @param max_mem
     */
    public ScanServerInfo(final String version, final Date start_time,
    		final String beamline_config)
    {
	    this.version = version;
	    this.start_time = start_time;
	    this.beamline_config = beamline_config;

		final MemoryUsage heap = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
		used_mem = heap.getUsed();
	    max_mem = heap.getMax();
    }

    /** @return Version number */
	public String getVersion()
    {
    	return version;
    }

	/** @return Start time */
	public Date getStartTime()
    {
    	return start_time;
    }

	/** @return Beam line configuration path */
	public String getBeamlineConfig()
    {
    	return beamline_config;
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

	/** @return Memory usage in percent of max */
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
        buf.append("Scan Server ").append(version).append("\n");
        buf.append("Started: ").append(DataFormatter.format(start_time)).append("\n");
        buf.append("Beamline Configuration: ").append(beamline_config).append("\n");
        buf.append("Memory: ").append(getMemoryInfo()).append("\n");
        return buf.toString();
    }
}
