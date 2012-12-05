/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.pvtable;

import org.hamcrest.Description;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Matcher;

/** Hamcrest {@link Matcher} for strings
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class StringMatcher
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
}