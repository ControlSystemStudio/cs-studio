/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.cstudio.archive.reader;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import org.csstudio.archive.reader.ValueIterator;
import org.junit.Test;

/** JUnit test of the {@link DemoDataIterator}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class DemoValueIteratorUnitTest
{
    @Test
    public void testDemoDataIterator() throws Exception
    {
        System.out.println("DemoDataIterator");
        final ValueIterator iter = DemoDataIterator.forStrings("Demo");
        int count = 0;
        while (iter.hasNext())
        {
            System.out.println(iter.next());
            ++count;
        }
        assertThat(count, equalTo(10));
        iter.close();
    }
}
