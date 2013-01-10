/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.apputil.time;

import static org.junit.Assert.*;

import java.util.concurrent.TimeUnit;

import org.csstudio.apputil.time.DelayCheck;
import org.junit.Test;

/** JUnit test of {@link DelayCheck}
 *  
 *  TODO Move to apputil
 *  @author Kay Kasemir
 */
public class DelayCheckUnitTest
{
	@Test
	public void testDelay() throws Exception
	{
		final DelayCheck delay = new DelayCheck(1, TimeUnit.SECONDS);
		// Fresh check has not expired
		assertFalse(delay.expired());
		
		// After 1.5 times the configured delay, it should have expired
		Thread.sleep(1500);
		assertTrue(delay.expired());
		
		// Then again not until another 1.5 times of the delay
		assertFalse(delay.expired());
		Thread.sleep(1500);
		assertTrue(delay.expired());
	}
}
