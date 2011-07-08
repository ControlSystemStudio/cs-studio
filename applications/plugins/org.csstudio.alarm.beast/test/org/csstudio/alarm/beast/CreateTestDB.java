/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast;

import java.io.PrintStream;

/** Create test IOC database and alarm config file
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class CreateTestDB
{
	// 5000 PVs in groups of 100 operate like this:
	//
	// PVs 000..099 Ramp 0..5000 and alarm at  90..100
	// PVs 100..199 Ramp 0..5000 and alarm at 190..200
	final private static int COUNT = 5000;
	private static final int GROUPSIZE = 100;
	
    public static void main(String[] args) throws Exception
    {
		PrintStream out = new PrintStream("demo.db");
		
		for (int i=0; i<COUNT; ++i)
		{
			final String num = String.format("%04d", i);
			
			final int group = (1 + i/GROUPSIZE) * GROUPSIZE;
			
			out.println("# Group " + group);
			out.println("record(calc, \"Ramp" + num + "\")");
			out.println("{");
			out.println("    field(INPA, \"Ramp" + num + "\")");
			out.println("    field(CALC, \"A<" + COUNT + "?A+1:0\")");
			out.println("    field(SCAN, \"1 second\")");
			out.println("    field(HOPR, \"" + COUNT + "\")");
			out.println("    field(FLNK, \"Alarm" + num + "\")");
			out.println("}");
			out.println("record(calc, \"Alarm" + num + "\")");
			out.println("{");
			out.println("    field(INPA, \"Ramp" + num + "\")");
			out.println("    field(CALC, \"A>=" + (group-10) + "&&A<=" + group + "\")");
			out.println("    field(HIGH, \"1\")");
			out.println("    field(HSV , \"MINOR\")");
			out.println("}");
			out.println();
		}
		out.close();
		
		out = new PrintStream("demo.xml");
		out.println("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
		out.println("<config name=\"demo\">");
		out.println("<component name=\"Area0000\">");
		for (int i=0; i<COUNT; ++i)
		{
			final String num = String.format("%04d", i);
			if ((i % GROUPSIZE) == 0)
			{
		        out.println("</component>");
				out.println("<component name=\"Area" + num + "\">");
				
			}
			out.println("    <pv name=\"Alarm" + num + "\">");
			out.println("        <description>Test PV</description>");
			out.println("        <latching>true</latching>");
			out.println("        <annunciating>true</annunciating>");
			out.println("        <guidance>");
			out.println("            <title>Test</title>");
			out.println("            <details>You can&#039;t do anything, this is just a test</details>");
			out.println("        </guidance>");
			out.println("    </pv>");
		}
        out.println("</component>");
        out.println("</config>");
		out.close();
    }
}
