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

import org.csstudio.scan.data.DataFormatter;
import org.csstudio.scan.data.ScanData;
import org.csstudio.scan.data.ScanSample;

/** {@link DataLogger} that prints all samples right away.
 * 
 *  <p>"write only"
 *  
 *  @author Kay Kasemir
 */
public class PrintDataLogger implements DataLogger
{
	final private PrintStream stream;

	/** Initialize for standard output */
	public PrintDataLogger()
	{
		this(System.out);
	}

	/** Initialize
	 *  @param stream Output stream
	 */
	public PrintDataLogger(final PrintStream stream)
	{
		this.stream = stream;
	}

    /** {@inheritDoc} */
	@Override
    public void log(final ScanSample sample)
    {
		stream.print(DataFormatter.format(sample.getTimestamp()));
		stream.print(' ');
		stream.print(sample.getDeviceName());
		stream.print('=');
		stream.print(sample.getValue().toString());
		stream.println();
    }

    /** {@inheritDoc} */
    @Override
    public long getLastScanDataSerial()
    {
        return -1;
    }

    /** {@inheritDoc} */
    @Override
    public ScanData getScanData()
    {
        return null;
    }
}
