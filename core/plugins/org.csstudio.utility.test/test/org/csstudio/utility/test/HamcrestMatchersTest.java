/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.test;

import static org.csstudio.utility.test.HamcrestMatchers.closeTo;
import static org.csstudio.utility.test.HamcrestMatchers.greaterThanOrEqualTo;
import static org.csstudio.utility.test.HamcrestMatchers.notANumber;
import static org.csstudio.utility.test.HamcrestMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.junit.Test;

/** JUnit test of the {@link HamcrestMatchers}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class HamcrestMatchersTest
{
    @Test
    public void testContainsString()
    {
        assertThat("Hello world", containsString("world"));
        assertThat("Hello world", not(containsString("underground")));

        try
        {
            assertThat("Hello world", containsString("Freddy"));
            fail("Did not detect missing text");
        }
        catch (AssertionError ex)
        {
            assertThat(ex.getMessage(), containsString("Freddy"));
        }
    }


   @Test
    public void testEqualTo()
    {
        assertThat(2.0, closeTo(2.0, 0.0));
        assertThat(2, closeTo(1, 1));
        assertThat(2.0, closeTo(3.0, 1.0));
        assertThat(2.0, closeTo(1.0, 1.0));
        assertThat(2.0, not(closeTo(1.0, 0.5)));
        
        try
        {
            assertThat(2.0, closeTo(1.0, 0.5));
        }
        catch (AssertionError ex)
        {
            assertThat(ex.getMessage(), containsString("1.0 +- 0.5"));
        }
    }


    @Test
    public void testAtLeast()
    {
        assertThat(2.0, greaterThanOrEqualTo(2.0));
        assertThat(2, greaterThanOrEqualTo(2));
        assertThat(3.0, greaterThanOrEqualTo(2.0));
        assertThat(1.9, not(greaterThanOrEqualTo(2.0)));
       
        try
        {
            assertThat(1.9, greaterThanOrEqualTo(2));
        }
        catch (AssertionError ex)
        {
            assertThat(ex.getMessage(), containsString("greater than or equal"));
            assertThat(ex.getMessage(), containsString("2"));
        }
    }


    @Test
    public void testNaN()
    {
        assertThat(2.0 * Double.NaN, is(notANumber()));
        assertThat(2.0 * 4, is(not(notANumber())));
    }

}
