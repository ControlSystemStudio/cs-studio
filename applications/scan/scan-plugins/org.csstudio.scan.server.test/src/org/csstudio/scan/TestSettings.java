/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan;

import org.csstudio.scan.server.JythonSupport;

/** Common test settings
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class TestSettings
{
    /** Anything that uses Jython should call this.
     *  Tests can be executed in arbitrary order,
     *  so trying to set the path in one specific test
     *  might not have any effect if another test already
     *  initialized the {@link JythonSupport}
     */
    public static void init()
    {
        System.setProperty("python.path", "src/org/csstudio/scan");
        System.setProperty("python.verbose", "debug");
        System.setProperty("python.import.site", "false");
    }
}
