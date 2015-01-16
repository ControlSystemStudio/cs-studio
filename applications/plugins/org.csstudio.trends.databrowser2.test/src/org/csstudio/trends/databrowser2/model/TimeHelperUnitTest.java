/*******************************************************************************
 * Copyright (c) 2014 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.model;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.time.Instant;

import org.junit.Test;

/** JUnit test of {@link TimeHelper}
 *  @author Kay Kasemir
 */
public class TimeHelperUnitTest
{
    @Test
    public void testFormat()
    {
        Instant now = Instant.now();
        final String text = TimeHelper.format(now);

        System.out.println(text);

        final Instant parsed = TimeHelper.parse(text);
        System.out.println(parsed);

        assertThat(parsed, equalTo(now));
    }
}
