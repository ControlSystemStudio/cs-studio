/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.scantree;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.csstudio.scan.command.LogCommand;
import org.csstudio.scan.ui.scantree.properties.LogCommandAdapter;
import org.junit.Test;

/** JUnit test of the {@link LogCommandAdapter}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class LogCommandAdapterUnitTest
{
    @Test
    public void testDeviceEncoding()
    {
        final LogCommand command = new LogCommand("xpos", "ypos", "readback");
        
        final LogCommandAdapter adapter = new LogCommandAdapter(null, command);
        
        final String encoded = (String) adapter.getPropertyValue(LogCommandAdapter.DEVICES);
        System.out.println("Encoded: " + encoded);
        assertEquals("xpos, ypos, readback", encoded);
        
        String[] names = adapter.decode("xpos, ypos, readback");
        System.out.println("Decoded: " + Arrays.toString(names));
        assertEquals(3, names.length);
        assertEquals("ypos", names[1]);
        
        names = adapter.decode("xpos ypos readback");
        System.out.println("Decoded: " + Arrays.toString(names));
        assertEquals(3, names.length);
        assertEquals("ypos", names[1]);
    }
}
