/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.log.derby;

import static org.junit.Assert.assertEquals;

import java.io.PrintWriter;

import org.apache.derby.tools.sysinfo;
import org.junit.Test;

/** JUnit test of basic Derby library access
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class SysInfoUnitTest
{
    @Test
	public void testDerbySysInfo()
	{
		final PrintWriter printer = new PrintWriter(System.out);
		sysinfo.getInfo(printer);
		printer.flush();
		// Keep System.out available, don't printer.close();

		System.out.println(sysinfo.getProductName() + " " + sysinfo.getVersionString());
		assertEquals(10, sysinfo.getMajorVersion());
		assertEquals(8, sysinfo.getMinorVersion());
	}
}
