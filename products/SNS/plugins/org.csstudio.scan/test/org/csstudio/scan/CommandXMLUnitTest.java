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

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.csstudio.scan.command.ScanCommand;
import org.csstudio.scan.command.XMLCommandWriter;
import org.junit.Test;

/** JUnit test of writing/reading a Command sequence as XML
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class CommandXMLUnitTest
{
    @Test
    public void testCommands() throws Exception
    {
        final List<ScanCommand> commands = DemoCommands.createDemoCommands();
        
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        new XMLCommandWriter(out).writeXML(commands);
        out.close();
        
        final String xml = out.toString();
        System.out.println(xml);
        assertTrue(xml.startsWith("<?xml"));
        assertTrue(xml.contains("<commands>"));
        assertTrue(xml.contains("</commands>"));
    }
}
