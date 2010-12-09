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
        final TreeItem tree = new TreeItem(null, "One", 1);
        new TreeItem(tree, "Two", 2);
        tree.dump(System.out);
        tree.check();
        assertEquals(1, tree.getChildCount());
        assertEquals("Two", tree.getChild(0).getName());
        assertEquals("/One/Two", tree.getChild(0).getPathName());

        assertNotNull(tree.getChild("Two"));
        assertSame(tree.getChild(0), tree.getChild("Two"));

        System.out.println("Parent of " + tree.getChild(0) + " : " + tree.getChild(0).getRoot());
        assertSame(tree, tree.getChild(0).getRoot());
    }
}
