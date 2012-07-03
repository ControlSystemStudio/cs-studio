/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.log;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.csstudio.scan.server.Scan;
import org.junit.Test;

/** [Headless] JUnit Plug-in test of the {@link DataLogFactory}
 *
 *  <p>When run as plain JUnit test, it will use the
 *  {@link MemoryDataLog}.
 *  As a headless test, it uses the DerbyDataLog via extension point.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class DataLogFactoryHeadlessTest
{
    @Test
	public void testDataLogFactory() throws Exception
	{
    	final Scan scan = DataLogFactory.createDataLog("test");
		assertTrue(scan.getId() > 0);
		assertEquals("test", scan.getName());
		final DataLog log = DataLogFactory.getDataLog(scan);
		assertNotNull(log);
		System.out.println("Got DataLog " + log.getClass().getName());

		boolean found = false;
		final Scan[] scans = DataLogFactory.getScans();
		for (Scan s : scans)
		{
		    if (scan.equals(s))
		        found = true;
		    System.out.println(s);
		}
		assertTrue(scans.length > 0);
        assertTrue(found);

        DataLogFactory.deleteDataLog(scan);
        final DataLog log2 = DataLogFactory.getDataLog(scan);
        assertNull(log2);
	}
}
