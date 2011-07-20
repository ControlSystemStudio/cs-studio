/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast;

import static org.junit.Assert.*;

import org.junit.Test;

/** JUnit test of {@link TreeItem}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class TreeItemUnitTest
{
    @Test
    public void testTreeItem() throws Exception
    {
        // Item with undefined ID
        TreeItem tree = new TreeItem(null, "One", -1);
        assertEquals(-1, tree.getID());
        assertEquals("One", tree.getName());
        assertEquals("/One", tree.getPathName());

        // Set ID once
        tree.setID(1);
        assertEquals(1, tree.getID());

        // Cannot set again
        try
        {
            tree.setID(10);
            fail("Must not allow change in ID");
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
            assertTrue(ex.getMessage().contains("ID already set"));
        }

        // Build simple 'tree'
        final TreeItem two = new TreeItem(tree, "Two", 2);
        tree.dump(System.out);
        tree.check();
        assertEquals(1, tree.getChildCount());
        assertEquals("Two", tree.getChild(0).getName());
        assertEquals("/One/Two", tree.getChild(0).getPathName());

        assertSame(two, tree.getChild("Two"));
        assertSame(two, tree.getChild(0));

        System.out.println("Parent of " + two + " : " + two.getRoot());
        assertSame(tree, two.getRoot());

        // Shrink tree
        two.detachFromParent();
        tree.dump(System.out);
        assertNull(two.getParent());
        assertEquals(0, tree.getChildCount());
    }
}
