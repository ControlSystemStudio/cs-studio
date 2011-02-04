/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui.actions;

import static org.junit.Assert.*;

import org.junit.Test;

/** JUnit test of {@link DisplayReference}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class DisplayReferenceUnitTest
{
    @Test
    public void testWorkspaceDisplayReference() throws Exception
    {
        // Workspace references, starting with '/'
        DisplayReference display = new DisplayReference("/some/path/file.opi \"macro1=value1, macro2 = value2  \" ");
        assertTrue(display.isValid());
        assertEquals("/some/path/file.opi", display.getFilename());
        assertEquals("macro1=value1, macro2 = value2  ", display.getData());

        // Allow spaces
        display = new DisplayReference("\"/some/longer path/file.opi\"      \"macro1=value1, macro2 = value2\"    ");
        assertTrue(display.isValid());
        assertEquals("/some/longer path/file.opi", display.getFilename());
        assertEquals("macro1=value1, macro2 = value2", display.getData());
    }

    @Test
    public void testExplicitDisplayReference() throws Exception
    {
        // Force workspace file to be opened as just that, not using display mechanism
        DisplayReference display = new DisplayReference("opi:/some/path/file.opi \"macro1=value1, macro2 = value2\" ");
        assertTrue(display.isValid());
        assertEquals("/some/path/file.opi", display.getFilename());
        assertEquals("macro1=value1, macro2 = value2", display.getData());

        // Force web link to be treated as display reference
        display = new DisplayReference("opi:http://host/path/get_display.cgi?name=file.opi     \"macro1=value1, macro2 = value2\"     ");
        assertTrue(display.isValid());
        assertEquals("http://host/path/get_display.cgi?name=file.opi", display.getFilename());
        assertEquals("macro1=value1, macro2 = value2", display.getData());
    }

    @Test
    public void testNonDisplayReference() throws Exception
    {
        // Plain web link
        DisplayReference display = new DisplayReference("http://a/b/c/d");
        assertFalse(display.isValid());

        // Script, external program
        display = new DisplayReference("startedm screen -m 'm=v'");
        assertFalse(display.isValid());
    }
}
