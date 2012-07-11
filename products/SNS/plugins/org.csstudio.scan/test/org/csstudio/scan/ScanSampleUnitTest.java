/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
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
package org.csstudio.scan;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.csstudio.scan.data.NumberScanSample;
import org.csstudio.scan.data.ScanSample;
import org.csstudio.scan.data.ScanSampleFactory;
import org.csstudio.scan.data.ScanSampleFormatter;
import org.junit.Test;

/** JUnit Test of the {@link ScanSample} and related classes
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ScanSampleUnitTest
{
	@Test
	public void testScanSample() throws Exception
	{
	    ScanSample sample = new NumberScanSample(new Date(), 1, new Number[] { 3.14 });
	    System.out.println(sample);
	    assertTrue(sample.toString().endsWith("3.14"));

	    sample = new NumberScanSample(new Date(), 1, new Number[] { 3.14, 42.0 });
	    System.out.println(sample);
	    assertTrue(sample.toString().endsWith("42.0]"));

	    sample = ScanSampleFactory.createSample(new Date(), 0, 1, 2, 3, 4);
        System.out.println(sample);
        assertTrue(sample.toString().endsWith("3, 4]"));

        assertEquals(1, ScanSampleFormatter.asDouble(sample), 0.001);
	}
}
