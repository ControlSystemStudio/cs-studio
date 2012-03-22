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
package org.csstudio.scan.data;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/** Iterate over {@link ScanData} as spreadsheet
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class SpreadsheetScanDataIterator
{
    /** Device names, i.e. columns in spreadsheet */
    final private List<String> device_names;

    /** Raw data for each device */
    final private List<ScanSample>[] data;

    /** Index within data for each device */
    final private int[] index;

    /** Timestamp of current spreadsheet line */
    private Date timestamp;

    /** Values for current spreadsheet line */
    final private ScanSample[] value;

    /** Initialize for all devices in the {@link ScanData}
     *  @param scan_data Scan data
     */
    public SpreadsheetScanDataIterator(final ScanData scan_data)
    {
        // Determine for which devices we have samples
        this(scan_data, scan_data.getDevices());
    }

    /** Initialize for specific devices
     *  @param scan_data Scan data
     *  @param device_names Devices that must be in the scan data
     */
    public SpreadsheetScanDataIterator(final ScanData scan_data, final String... device_names)
    {
        this(scan_data, Arrays.asList(device_names));
    }

    /** Initialize for specific devices
     *  @param scan_data Scan data
     *  @param device_names Devices that must be in the scan data
     */
	@SuppressWarnings("unchecked")
    public SpreadsheetScanDataIterator(final ScanData scan_data,
	        final List<String> device_names)
    {
	    this.device_names = device_names;
        final int N = device_names.size();

        data = new List[N];
        value = new ScanSample[N];
        index = new int[N];
        for (int i=0; i<N; ++i)
        {
            data[i] = scan_data.getSamples(device_names.get(i));
            if (data[i] == null)
                data[i] = Collections.emptyList();
            index[i] = 0;
            value[i] = null;
        }
    }

	/** @param i Device index
	 *  @return data[index[i]]
	 */
	private ScanSample getCurrentSample(final int i)
	{
	    if (data[i].size() > index[i])
	        return data[i].get(index[i]);
	    return null;
	}

	/** @return Device names, i.e. spreadsheet columns */
	public List<String> getDevices()
	{
	    return device_names;
	}

    /** @return <code>true</code> if there is another line in the spreadsheet */
    public boolean hasNext()
	{
        // Find oldest serial
        final int N = device_names.size();
		long oldest = Long.MAX_VALUE;
		timestamp = null;
		for (int i=0; i<N; ++i)
		{
		    final ScanSample sample = getCurrentSample(i);
			if (sample == null)
				continue;
			if (sample.getSerial() < oldest)
			{
				oldest = sample.getSerial();
				timestamp = sample.getTimestamp();
			}
		}
		if (timestamp == null)
			return false;

        // 'oldest' now defines the current spreadsheet line.
		// Determine value for that line.
		for (int i=0; i<N; ++i)
		{
            final ScanSample sample = getCurrentSample(i);
			if (sample == null)
			{	// No more data for device #i
			    // Leave value[i] as is, "still valid"
				continue;
			}
			// Device #i has data
			if (sample.getSerial() <= oldest)
			{
				value[i] = sample;
				++index[i];
			}
			// else: sample[i] already points to a sample
			// _after_ the current line, so leave value[i] as is
		}

		return true;
	}

    /** @return Time stamp of the current spreadsheet line */
    public Date getTimestamp()
    {
        return timestamp;
    }

    /** @return Samples on the current spreadsheet line */
    public List<ScanSample> getSamples()
    {
        return Arrays.asList(value);
    }

    /** Write spreadsheet to stream
     *  @param out {@link PrintStream}
     */
    public void dump(final PrintStream out)
    {
        dump(new PrintWriter(out, true));
    }

    /** Write spreadsheet to writer
     *  @param writer {@link Writer}
     */
    public void dump(final Writer writer)
    {
        final PrintWriter out = new PrintWriter(writer, true);
        // Print Header
        out.print("Time");
        for (String device : getDevices())
        {
            out.print('\t');
            out.print(device);
        }
        out.println();

        // Iterate over device data in 'spreadsheet' manner
        while (hasNext())
        {
            out.print(DataFormatter.format(getTimestamp()));
            // Print current line
            for (ScanSample sample : getSamples())
            {
                out.print('\t');
                if (sample == null)
                    out.print("#N/A");
                else
                    out.print(sample.getValue());
            }
            out.println();
        }
    }
}
