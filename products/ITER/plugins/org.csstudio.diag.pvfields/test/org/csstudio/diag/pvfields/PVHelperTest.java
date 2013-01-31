/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.diag.pvfields;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;

/** JUnit test of the {@link PVHelper}
 *  @author Kay Kasemir
 */
public class PVHelperTest
{
	@Test
	public void testGetField()
	{
		assertThat("INP", equalTo(PVHelper.getField("SomePV.INP")));
		assertThat("VAL", equalTo(PVHelper.getField("SomePV")));
	}

	@Test
	public void testGetPV()
	{
		assertThat("SomePV", equalTo(PVHelper.getPV("SomePV.INP")));
	}
}
