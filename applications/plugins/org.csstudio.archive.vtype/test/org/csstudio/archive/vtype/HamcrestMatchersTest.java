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
import static org.csstudio.archive.vtype.HamcrestMatchers.*;

import org.junit.Test;

/** JUnit test of the {@link HamcrestMatchers}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class HamcrestMatchersTest
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
    
    
   @Test
    public void testEqualTo()
    {
        assertThat(2.0, equalTo(2.0, 0.0));
        assertThat(2, equalTo(1, 1));
        assertThat(2.0, equalTo(3.0, 1.0));
        assertThat(2.0, equalTo(1.0, 1.0));
        assertThat(2.0, not(equalTo(1.0, 0.5)));
        
        try
        {
            assertThat(2.0, equalTo(1.0, 0.5));
        }
        catch (AssertionError ex)
        {
            assertThat(ex.getMessage(), contains("1.0 +- 0.5"));
        }
    }
}
