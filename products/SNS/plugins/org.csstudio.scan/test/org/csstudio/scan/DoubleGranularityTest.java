/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/** Demo for granularity of double numbers
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class DoubleGranularityTest
{
    @Test(timeout=1000)
	public void findDoubleGranularityLimit()
	{
		double limit = 0.5;
		while (1.0 + limit != limit)
			limit *= 2;
		System.out.println("** Rought limit for +-1 **");
		System.out.println("OK:           1 + " + limit/2 + " != " + limit/2);
		System.out.println("Beyond Limit: 1 + " + limit + " == " + limit);

		assertEquals(limit, 1.0 + limit, 0.5);
		// Somewhere around 1E16
		assertEquals(1.0, limit/1E16, 1);
	}
}
