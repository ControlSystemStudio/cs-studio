/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.server;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Test;

/** Custom hamcrest matchers
 * 
 *  ... since including hamcrest-library is so hard with Eclipse's existing junit plugin
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Matchers
{
    static class AtLeast extends BaseMatcher<Number>
    {
        final private double threshold;

        public AtLeast(final double threshold)
        {
            this.threshold = threshold;
        }
        
        @Override
        public boolean matches(final Object value)
        {
            if (value instanceof Number)
                return ((Number)value).doubleValue() >= threshold;
            return false;
        }

        @Override
        public void describeTo(final Description desc)
        {
            desc.appendText("at least " + threshold);
        }
    };
    
    public static Matcher<Number> atLeast(final double threshold)
    {
        return new AtLeast(threshold);
    }
    
    @Test
    public void testMatchers() throws Exception
    {
        assertThat(3.0, atLeast(3.0));
        assertThat(5.0, atLeast(3.0));
        assertThat(5.0, is(atLeast(3.0)));
        assertThat(2.0, is(not(atLeast(3.0))));
    }
}
