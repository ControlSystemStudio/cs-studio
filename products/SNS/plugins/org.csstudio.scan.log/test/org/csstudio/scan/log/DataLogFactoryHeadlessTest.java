/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.log;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/** [Headless] JUnit Plug-in test of the {@link DataLogFactory}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class DataLogFactoryHeadlessTest
{
    @Test
	public void testDataLogFactory() throws Exception
	{
    	final long scan_id =	DataLogFactory.createDataLog("test");
		assertTrue(scan_id > 0);
		final DataLog log = DataLogFactory.getDataLog(scan_id);
		assertNotNull(log);
		System.out.println("Got DataLog " + log.getClass().getName());
	}
}
