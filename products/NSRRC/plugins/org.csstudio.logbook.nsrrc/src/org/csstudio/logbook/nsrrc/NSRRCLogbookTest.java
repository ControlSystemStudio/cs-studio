/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.logbook.nsrrc;

import static org.junit.Assert.*;

import java.io.File;

import org.csstudio.logbook.ILogbook;
import org.junit.Test;

/** JUnit test of the logbook
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class NSRRCLogbookTest
{
    @Test
	public void testCreateEntryStringStringStringArray() throws Exception
	{
		final File file = new File(NSRRCLogbook.LOGBOOK_FILE);
		
		final long orig = file.exists() ? file.length() : 0;
		
		final ILogbook logbook = new NSRRCLogbook("Test");
		logbook.createEntry("Test Title", "This is a test",
				"/tmp/dummy/file.png");
		logbook.close();

		// Check if test entry resulted in a 'longer' logbook file
		assertTrue(file.length() > orig);
	}
}
