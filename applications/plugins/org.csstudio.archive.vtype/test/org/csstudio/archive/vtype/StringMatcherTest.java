/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.vtype;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.csstudio.archive.vtype.StringMatcher.*;

import org.junit.Test;

/** JUnit test of the {@link StringMatcher}
 *  @author Kay Kasemir
 */
public class StringMatcherTest
{
	@Test
	public void testContains()
	{
		assertThat("Hello world", contains("world"));
		assertThat("Hello world", not(contains("underground")));

		try
		{
			assertThat("Hello world", contains("Freddy"));
			fail("Did not detect missing text");
		}
		catch (AssertionError ex)
		{
			assertThat(ex.getMessage(), contains("Freddy"));
		}
	}
}
