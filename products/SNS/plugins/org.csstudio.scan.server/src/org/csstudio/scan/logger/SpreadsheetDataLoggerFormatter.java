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

import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.csstudio.scan.data.DataFormatter;
import org.csstudio.scan.data.ScanSample;

/** Format data as spreadsheet
 *  @author Kay Kasemir
 */
public class SpreadsheetDataLoggerFormatter
{
	public void format(final PrintStream out, final MemoryDataLogger data_logger)
	{
		final Map<String, List<ScanSample>> device_logs = data_logger.getDeviceLogs();

		// Determine for which devices we have samples
		final int N = device_logs.keySet().size();
		final String[] device_names = device_logs.keySet().toArray(new String[N]);
		Arrays.sort(device_names);

		// Fetch data for each device
		@SuppressWarnings("unchecked")
        final List<ScanSample>[] data = new List[N];
		final ScanSample[] sample = new ScanSample[N];
		final ScanSample[] value = new ScanSample[N];
		final int[] index = new int[N];
		for (int i=0; i<N; ++i)
		{
			data[i] = device_logs.get(device_names[i]);
			index[i] = 0;
			if (data[i].size() > 0)
				sample[i] = data[i].get(0);
			else
				sample[i] = null;
			value[i] = null;
		}

		// Print Header
		out.print("Time");
		for (int i=0; i<N; ++i)
		{
			out.print('\t');
			out.print(device_names[i]);
		}
		out.println();

		// Iterate over device data in 'spreadsheet' manner
		while (true)
		{
	        // Find oldest serial
			long oldest = Long.MAX_VALUE;
			String time_string = null;
			for (int i=0; i<N; ++i)
			{
				if (sample[i] == null)
					continue;
				if (sample[i].getSerial() < oldest)
				{
					oldest = sample[i].getSerial();
					time_string = DataFormatter.format(sample[i].getTimestamp());
				}
			}
			if (time_string == null)
				break;

	        // 'time' now defines the current spreadsheet line.
			// Determine value for that line
			for (int i=0; i<N; ++i)
			{
				if (sample[i] == null)
				{	// No more data for device #i
					continue;
				}
				// Device #i has data
				if (sample[i].getSerial() <= oldest)
				{
					value[i] = sample[i];
					++index[i];
					if (index[i] < data[i].size())
						sample[i] = data[i].get(index[i]);
					else
						sample[i] = null;
				}
				// else: sample[i] already points to a sample
				// _after_ the current line, so leave value[i] as is
			}

			// Print current line
			out.print(time_string);
			for (int i=0; i<N; ++i)
			{
				out.print('\t');
				if (value[i] == null)
					out.print("#N/A");
				else
					out.print(value[i].getValue());
			}
			out.println();
		}
	}
}
