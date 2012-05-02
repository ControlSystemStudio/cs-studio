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
package org.csstudio.scan.log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.csstudio.scan.Preferences;
import org.csstudio.scan.data.ScanData;
import org.csstudio.scan.data.ScanSample;
import org.csstudio.scan.server.MemoryInfo;

/** {@link DataLog} that keeps all samples in memory
 *
 *  <p>Stops logging when memory threshold is reached.
 *
 *  @author Kay Kasemir
 */
public class MemoryDataLog implements DataLog
{
	/** Map from device name to list of samples for that device */
	final private Map<String, List<ScanSample>> device_logs =
			new HashMap<String, List<ScanSample>>();

	/** Serial of last logged sample */
    private long last_serial = -1;

    final private double threshold = Preferences.getOldScanRemovalMemoryThreshold();

    /** {@inheritDoc} */
	@Override
    public synchronized void log(final ScanSample sample)
    {
		// Check Memory usage
		final MemoryInfo mem = new MemoryInfo();
		if (mem.getMemoryPercentage() > threshold)
    		return;

		List<ScanSample> samples = device_logs.get(sample.getDeviceName());
		if (samples == null)
		{
			samples = new ArrayList<ScanSample>();
			device_logs.put(sample.getDeviceName(), samples);
		}
		samples.add(sample);
		last_serial  = sample.getSerial();
    }

    /** @return Serial of last sample in scan data */
	@Override
    public synchronized long getLastScanDataSerial()
	{
	    return last_serial;
	}

    /** @return {@link ScanData} with copy of currently logged data */
	@Override
    public synchronized ScanData getScanData()
	{
		return new ScanData(new HashMap<String, List<ScanSample>>(device_logs));
	}
}
