/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.apputil.xml;

import static org.junit.Assert.*;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.junit.Test;

/** JUnit test of the XMLWriter
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class XMLWriterUnitTest
{
    @Test
    public void testXML()
    {
        // Write into string
        final StringWriter sw = new StringWriter();
        final PrintWriter out = new PrintWriter(sw);

        // Create some XML
        XMLWriter.header(out);
        XMLWriter.start(out, 0, "content");
        out.println();
        XMLWriter.XML(out, 1, "name", "value");
        // Code point 246 is o-umlaut
        XMLWriter.XML(out, 1, "umlaut", "Hall\u00f6?");
        XMLWriter.end(out, 0, "content");

        // Get the XML as string
        out.flush();
        final String xml = sw.toString();

        // Check XML
        System.out.println(xml);
        assertTrue("Header", xml.startsWith("<?xml"));
        assertTrue("Name/Value", xml.contains("<name>value</name>"));
        assertTrue("Umlaut", xml.contains("<umlaut>Hall&#246;?</umlaut>"));
    }
}
