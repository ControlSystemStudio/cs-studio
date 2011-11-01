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
package org.csstudio.scan.client;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.python.util.PythonInterpreter;

/** JUnit test of Jython connecting to ScanServer
 *  @author Kay Kasemir
 */
public class Jython102UnitTest
{
    private static String info = null;

    public static void setInfo(final String info)
    {
        Jython102UnitTest.info = info;
    }

    @Test(timeout=5000)
    public void testJython()
    {
        final PythonInterpreter interpreter = new PythonInterpreter();

        interpreter.exec("import yabes.client.ScanServerConnector");
        interpreter.exec("server=yabes.client.ScanServerConnector.connect()");
        interpreter.exec("info=server.getInfo()");
        interpreter.exec("print 'Connected to %s' % info");

        interpreter.exec("import yabes.Jython102UnitTest as T");
        interpreter.exec("T.setInfo(info)");

        System.out.println("Received from Jython: " + info);
        assertTrue(info.startsWith("Scan Server"));
    }
}
