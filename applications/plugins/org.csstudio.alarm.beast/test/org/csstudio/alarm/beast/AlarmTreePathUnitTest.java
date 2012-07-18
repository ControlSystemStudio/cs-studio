/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/** JUnit test of AlarmTreePath
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class AlarmTreePathUnitTest
{
    @Test
    public void testPathCheck()
    {
        assertEquals("/root", AlarmTreePath.makePath(null, "root"));
        assertTrue(AlarmTreePath.isPath("/path/to/some/pv"));
        assertEquals("pv", AlarmTreePath.getName("/path/to/some/pv"));
        assertFalse(AlarmTreePath.isPath("some_pv"));
        assertFalse(AlarmTreePath.isPath("sim:\\/\\/sine"));
        assertEquals("sim://sine", AlarmTreePath.getName("sim:\\/\\/sine"));
    }

    @Test
    public void testMakePath()
    {
        // Split
        final String[] path = AlarmTreePath.splitPath("/path/to/some/pv");
        assertEquals(4, path.length);
        assertEquals("to", path[1]);

        // Sub-path
        final String new_path = AlarmTreePath.makePath(path, 2);
        assertEquals("/path/to", new_path);

        // New PV
        assertEquals("/path/to/another",
                     AlarmTreePath.makePath(new_path, "another"));
    }

    @Test
    public void testSpaces()
    {
        String path = AlarmTreePath.makePath("the path", "to");
        assertEquals("/the path/to", path);

        path = AlarmTreePath.makePath(path, "an item");
        assertEquals("/the path/to/an item", path);

        path = AlarmTreePath.makePath(path, "with / in it");
        assertEquals("/the path/to/an item/with \\/ in it", path);

        // Split
        final String[] items = AlarmTreePath.splitPath(path);
        assertEquals(4, items.length);
        assertEquals("the path", items[0]);
        assertEquals("to", items[1]);
        assertEquals("an item", items[2]);
        assertEquals("with / in it", items[3]);

        // Re-assemble
        path = AlarmTreePath.makePath(items, items.length);
        assertEquals("/the path/to/an item/with \\/ in it", path);
    }


    @Test
    public void testSpecialChars()
    {
    	String path = AlarmTreePath.makePath("path", "to");
    	assertEquals("/path/to", path);

    	// First element already contains '/'
    	path = AlarmTreePath.makePath("/path", "to");
        assertEquals("/path/to", path);

    	path = AlarmTreePath.makePath(path, "sim://sine");
    	// String is really "/path/to/sim:\/\/sine",
    	// but to get the '\' into the string,
    	// it itself needs to be escaped
    	assertEquals("/path/to/sim:\\/\\/sine", path);

    	// Split
    	final String[] items = AlarmTreePath.splitPath(path);
    	assertEquals(3, items.length);
    	assertEquals("path", items[0]);
    	assertEquals("to", items[1]);
    	assertEquals("sim://sine", items[2]);

    	// Re-assemble
    	path = AlarmTreePath.makePath(items, items.length);
    	assertEquals("/path/to/sim:\\/\\/sine", path);
    }
}
