/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.scantree;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.csstudio.scan.ui.scantree.properties.StringArrayCellEditor;
import org.junit.Test;

/** JUnit test of the {@link StringArrayCellEditor}'s encoding and decoding
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class StringArrayCellEditorUnitTest
{
    @Test
    public void testDeviceEncoding() throws Exception
    {
        final String[] items = new String[] { "xpos", "ypos", "readback", "some \"other\" PV" };
        
        final String encoded = StringArrayCellEditor.encode(items);
        System.out.println("Encoded:\n" + encoded);
        assertEquals("xpos, ypos, readback, some \"other\" PV", encoded);

        final String[] decoded = StringArrayCellEditor.decode(encoded);
        System.out.println("Decoded:");
        for (String item : decoded)
            System.out.println(item);
        assertArrayEquals(items, decoded);
    }
}
