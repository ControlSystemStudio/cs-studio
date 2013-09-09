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

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.python.core.PyCode;
import org.python.util.PythonInterpreter;

/** JUnit test of Java calling Jython, which calls back into Java
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Jython101UnitTest
{
    private static int number = 0;

    public static void setNumber(final int number)
    {
        Jython101UnitTest.number = number;
    }

    @Test(timeout=5000)
    public void testJython()
    {
        final PythonInterpreter interpreter = new PythonInterpreter();
        PyCode code = interpreter.compile("print 'Hello'");
        interpreter.exec(code);
        interpreter.exec(code);
        interpreter.exec(code);

        interpreter.exec("import sys");
        interpreter.exec("print 'Jython version :'");
        interpreter.exec("print sys.version");
        interpreter.exec("print sys.version_info");

        interpreter.exec("x=42");
        interpreter.exec("print 'I set x to %d ...' % x");
        interpreter.exec("import org.csstudio.scan.client.Jython101UnitTest as T");

        assertEquals(0, number);
        interpreter.exec("T.setNumber(x)");
        assertEquals(42, number);

        interpreter.exec("print 'Looks like you saw that on the Java side!'");
    }
}
