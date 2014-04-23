/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.notifier.util;

import java.io.IOException;
import java.io.InputStream;

/** Thread that reads input stream and deletes the text.
 *
 *  The output of an external command must be read, otherwise
 *  the external command could block.
 *  This class can be used to read but ignore the output.
 *  
 *  @see StreamSwallowThread
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class StreamSwallowThread extends Thread {
	final private InputStream stream;

	public StreamSwallowThread(final InputStream stream) {
		super("StreamSwallower");
		this.stream = stream;
	}

	@Override
	public void run() {
		try {
			final byte buf[] = new byte[100];
			while (stream.read(buf) >= 0) {
				// Ignore bytes
			}
		} catch (IOException e) {
			// Ignore errors
		}
	}
}