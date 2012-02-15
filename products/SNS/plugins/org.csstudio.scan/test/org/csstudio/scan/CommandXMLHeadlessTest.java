/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * The scan engine idea is based on the "ScanEngine" developed
 * by the Software Services Group (SSG),  Advanced Photon Source,
 * Argonne National Laboratory,
 * Copyright (c) 2011 , UChicago Argonne, LLC.
 *
 * This implementation, however, contains no SSG "ScanEngine" source code
 * and is not endorsed by the SSG authors.
 ******************************************************************************/
package org.csstudio.scan;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.csstudio.scan.command.ScanCommand;
import org.csstudio.scan.command.ScanCommandFactory;
import org.csstudio.scan.command.SimpleScanCommandFactory;
import org.csstudio.scan.command.XMLCommandReader;
import org.csstudio.scan.command.XMLCommandWriter;
import org.junit.Test;

/** [Headless] JUnit Plug-In test of writing/reading a Command sequence as XML
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class CommandXMLHeadlessTest
{
    private static String xml;

    // Static method to write static member to please FindBugs
    private static void storeXML(final String xml)
    {
        CommandXMLHeadlessTest.xml = xml;
    }

    @Test
    public void testWriteXML() throws Exception
    {
        final List<ScanCommand> commands = DemoCommands.createDemoCommands();

        storeXML(XMLCommandWriter.toXMLString(commands));
        System.out.println(xml);
        assertTrue(xml.startsWith("<?xml"));
        assertTrue(xml.contains("<commands>"));
        assertTrue(xml.contains("</commands>"));
    }

    public void runReader(final XMLCommandReader reader) throws Exception
    {
        assertNotNull(xml);
        assertTrue(xml.length() > 0);

        final List<ScanCommand> commands = reader.readXMLString(xml);
        assertNotNull(commands);

        // When turned back into XML, result should match
        final String copy = XMLCommandWriter.toXMLString(commands);
        System.out.println("Read from XML:");
        System.out.println(copy);
        assertEquals(xml, copy);
    }

    @Test
    public void readXMLUsingSimpleScanCommandFactory() throws Exception
    {
        runReader(new XMLCommandReader(new SimpleScanCommandFactory()));
    }

    @Test
    public void readXMLUsingEclipseScanCommandFactory() throws Exception
    {
        runReader(new XMLCommandReader(new ScanCommandFactory()));
    }
}
