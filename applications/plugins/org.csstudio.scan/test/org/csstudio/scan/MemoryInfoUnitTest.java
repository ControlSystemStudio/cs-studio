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

import static org.junit.Assert.*;

import org.csstudio.scan.server.MemoryInfo;
import org.junit.Test;

/** JUnit test of the {@link MemoryInfo}
 *  @author Kay Kasemir
 */
public class MemoryInfoUnitTest
{
	@Test
	public void testGetMemoryInfo()
	{
		final MemoryInfo mem = new MemoryInfo();
		System.out.println(mem);

		final double perc = mem.getMemoryPercentage();
		assertTrue(perc > 0);
		assertTrue(perc < 100);
	}
}
