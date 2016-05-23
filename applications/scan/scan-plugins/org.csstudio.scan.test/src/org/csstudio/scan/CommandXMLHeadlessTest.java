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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.scan.command.ConfigLogCommand;
import org.csstudio.scan.command.LoopCommand;
import org.csstudio.scan.command.ScanCommand;
import org.csstudio.scan.command.ScanCommandFactory;
import org.csstudio.scan.command.SetCommand;
import org.csstudio.scan.command.SimpleScanCommandFactory;
import org.csstudio.scan.command.XMLCommandReader;
import org.csstudio.scan.command.XMLCommandWriter;
import org.junit.Ignore;
import org.junit.Test;

/** [Headless] JUnit Plug-In test of writing/reading a Command sequence as XML
 *
 *  All but readXMLUsingEclipseScanCommandFactory() runs as plain JUnit test.
 *
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
    public void testCommandIDMapping() throws Exception
    {
        ScanCommand cmd = new LoopCommand("device", 1, 10, 1);
        assertThat(cmd.getCommandID(), equalTo("loop"));

        cmd = new ConfigLogCommand();
        assertThat(cmd.getCommandID(), equalTo("config_log"));

        assertThat(SimpleScanCommandFactory.ID2CommandName("config_log"),
                   equalTo("ConfigLogCommand"));
        assertThat(SimpleScanCommandFactory.ID2CommandName("loop"),
                   equalTo("LoopCommand"));
    }

    @Test
    public void testWriteXML() throws Exception
    {
        final List<ScanCommand> commands = DemoCommands.createDemoCommands();

        storeXML(XMLCommandWriter.toXMLString(commands));
        System.out.println(xml);
        assertTrue(xml.contains("<commands>"));
        assertTrue(xml.contains("</commands>"));
    }

    @Test
    public void testWriteSpecialCharacters() throws Exception
    {
        final List<ScanCommand> commands = new ArrayList<>();
        final SetCommand set = new SetCommand("My<>Device", "Apple&Orange");
        commands.add(set);
        final String xml = XMLCommandWriter.toXMLString(commands);
        System.out.println(xml);
        assertTrue(xml.contains("<commands>"));
        assertTrue(xml.contains("<set>"));
        assertTrue(xml.contains("<device>My&lt;&gt;Device</device>"));
        assertTrue(xml.contains("<value>\"Apple&amp;Orange\"</value>"));
    }

    public void runReader(final XMLCommandReader reader) throws Exception
    {
        if (xml == null)
            testWriteXML();
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

    @Ignore
    @Test
    public void readXMLUsingEclipseScanCommandFactory() throws Exception
    {
        runReader(new XMLCommandReader(new ScanCommandFactory()));
    }
}
