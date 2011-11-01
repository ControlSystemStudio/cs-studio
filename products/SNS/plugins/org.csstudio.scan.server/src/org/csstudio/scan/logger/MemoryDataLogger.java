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
package org.csstudio.scan.logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.csstudio.scan.data.ScanSample;

/** {@link DataLogger} that keeps all samples in memory
 *  @author Kay Kasemir
 */
public class MemoryDataLogger implements DataLogger
{
	/** Map from device name to list of samples for that device */
	final private Map<String, List<ScanSample>> device_logs =
			new HashMap<String, List<ScanSample>>();

	/** {@inheritDoc} */
	@Override
    public void log(final ScanSample sample)
    {
		List<ScanSample> samples = device_logs.get(sample.getDeviceName());
		if (samples == null)
		{
			samples = new ArrayList<ScanSample>();
			device_logs.put(sample.getDeviceName(), samples);
		}
		samples.add(sample);
    }

	/** @return (Unmodifyable) Map of all device names with {@link ScanSample} for each device */
	public Map<String, List<ScanSample>> getDeviceLogs()
	{
		return Collections.unmodifiableMap(device_logs);
	}
}
