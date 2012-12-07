/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.model;

import org.hamcrest.Description;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Matcher;

/** Matchers for Hamcrest
 *  @author Kay Kasemir
 *  
 *  TODO Find shared place for this.
 *  For now the 'master' is in archive.vtype
 */
@SuppressWarnings("nls")
public class HamcrestMatchers
{
	/** @param segment Segment to find within {@link String}
	 *  @return {@link Matcher}
	 */
	public static Matcher<String> contains(final String segment)
	{
		return new BaseMatcher<String>()
		{
            @Override
			public void describeTo(final Description descr)
			{
				descr.appendText("text that contains \"").appendText(segment).appendText("\"");
			}

			@Override
			public boolean matches(final Object obj)
			{
				return obj instanceof String
					&& ((String) obj).contains(segment);
			}
		};
	}
	
	/** @param goal Desired value
	 *  @param tolerance Allowed tolerance
     *  @return {@link Matcher}
	 */
    public static Matcher<Number> equalTo(final Number goal, final double tolerance)
    {
        return new BaseMatcher<Number>()
        {
            @Override
            public void describeTo(final Description desc)
            {
                desc.appendText(goal + " +- " + tolerance);
            }
            
            @Override
            public boolean matches(final Object value)
            {
                if (value instanceof Number)
                {
                    final double dbl = ((Number)value).doubleValue();
                    return Math.abs(goal.doubleValue() - dbl) <= tolerance;
                }
                return false;
            }
        };
    }

}
