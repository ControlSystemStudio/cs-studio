/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.cstudio.archive.reader;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.csstudio.archive.reader.MergingValueIterator;
import org.csstudio.archive.reader.ValueIterator;
import org.csstudio.archive.vtype.DefaultVTypeFormat;
import org.csstudio.archive.vtype.VTypeFormat;
import org.epics.vtype.VType;
import org.junit.Test;

/** JUnit test of the {@link MergingValueIterator}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class MergingValueIteratorUnitTest
{
    /** Merge two DemoDataIterators */
    @Test
    public void testMergingValueIterator() throws Exception
    {
        System.out.println("MergingValueIterator");
        final DemoDataIterator iter1 = DemoDataIterator.forStrings("A");
        final DemoDataIterator iter2 = DemoDataIterator.forStrings("B");
        final ValueIterator merge = new MergingValueIterator(iter1, iter2);
        int count = 0;
        final StringBuilder result = new StringBuilder();
        final VTypeFormat format = new DefaultVTypeFormat();
        while (merge.hasNext())
        {
            final VType value = merge.next();
            System.out.println(value);
            if (result.length() > 0)
                result.append(", ");
            format.format(value, result);
            ++count;
        }
        assertThat(count, equalTo(20));
        assertThat(result.toString(), equalTo("A 1, B 1, A 2, B 2, A 3, B 3, A 4, B 4, A 5, B 5, A 6, B 6, A 7, B 7, A 8, B 8, A 9, B 9, A 10, B 10"));
        assertThat(iter1.isOpen(), equalTo(true));
        assertThat(iter2.isOpen(), equalTo(true));
        merge.close();
        assertThat(iter1.isOpen(), equalTo(false));
        assertThat(iter2.isOpen(), equalTo(false));
    }
}
