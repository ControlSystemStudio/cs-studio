/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.notifier.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/** Thread that reads input stream into a string.
 *  
 *  Can be used to capture the output of a background tasks
 *  @see StreamSwallowThread
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class StreamStringReaderThread extends Thread {
	final private InputStream stream;
	final private StringBuilder buf = new StringBuilder();

	public StreamStringReaderThread(final InputStream stream) {
		super("StreamStringReader");
		this.stream = stream;
	}

	/** @return Text that has been read so far */
	public String getText() {
		synchronized (buf) {
			return buf.toString();
		}
	}

	@Override
	public void run() {
		try {
			final InputStreamReader isr = new InputStreamReader(stream);
			// Buffer the reads, but use smaller buffer because
			// we hope to read very little from the external command
			final BufferedReader br = new BufferedReader(isr, 512);
			String line;
			while ((line = br.readLine()) != null) {
				synchronized (buf) {
					buf.append(line + "\n");
				}
			}
		} catch (IOException e) {
			synchronized (buf) {
				buf.append(e.getMessage());
			}
		}
	}
}