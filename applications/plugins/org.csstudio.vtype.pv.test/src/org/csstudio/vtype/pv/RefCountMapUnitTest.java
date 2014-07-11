/*******************************************************************************
 * Copyright (c) 2014 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.vtype.pv;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;

/** JUnit test of the {@link RefCountMap}
 *  @author Kay Kasemir
 */
public class RefCountMapUnitTest
{
    @Test
    public void testMap()
    {
        final RefCountMap<String, Integer> map = new RefCountMap<>();
        // Empty
        Integer item = map.get("one");
        assertThat(item, is(nullValue()));
        
        // Add 'one', reference 1
        final Integer one = Integer.valueOf(1);
        map.put("one", one);
        // When getting it, ref count becomes 2
        item = map.get("one");
        assertThat(item, sameInstance(one));

        // Add 'two', reference 1
        map.put("two", 2);
        item = map.get("two");
        assertThat(item, equalTo(2));
        
        // Release, one reference left
        assertThat(map.release("one"), equalTo(1));
        // Release, no reference left
        assertThat(map.release("one"), equalTo(0));
        item = map.get("one");
        assertThat(item, is(nullValue()));
    }

    @Test
    public void testAddTwice()
    {
        final RefCountMap<String, Integer> map = new RefCountMap<>();
        map.put("one", Integer.valueOf(1));
        try
        {
            map.put("one", Integer.valueOf(1));
            fail("Added item again?");
        }
        catch (IllegalStateException expected)
        {
            // Ignore
        }
    }

    @Test
    public void testReleaseUnkown()
    {
        final RefCountMap<String, Integer> map = new RefCountMap<>();
        map.put("one", Integer.valueOf(1));
        map.release("one");
        try
        {
            map.release("one");
            fail("Released unknown item?");
        }
        catch (IllegalStateException expected)
        {
            // Ignore
        }
    }
}
