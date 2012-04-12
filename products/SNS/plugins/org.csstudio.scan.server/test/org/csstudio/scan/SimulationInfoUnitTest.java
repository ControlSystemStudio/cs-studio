/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan;

import static org.junit.Assert.assertEquals;

import org.csstudio.scan.server.SimulationInfo;
import org.junit.Test;

/** JUnit test of the {@link SimulationInfo}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class SimulationInfoUnitTest
{
	@Test
	public void testSimulationInfoString() throws Exception
	{
        final SimulationInfo info =
				new SimulationInfo("../org.csstudio.scan/examples/simulation.xml");

		// Named PV
		assertEquals(7, info.getSlewRate("neutrons"), 0.001);

		// Aliased PV
		assertEquals(0.1, info.getSlewRate("xpos"), 0.001);

		// PV matches pattern ".pos", but it's not the specifically listed "xpos"
		assertEquals(0.2, info.getSlewRate("ypos"), 0.001);

		// Unknown PV
		assertEquals(SimulationInfo.DEFAULT_SLEW_RATE, info.getSlewRate("whatever"), 0.001);
	}
}
