/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.engine;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.logging.Level;

import org.junit.Test;

/** Test of the <code>ThrottledLogger</code>.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ThrottledLoggerUnitTest
{
    @Test
    public void testLogInfo() throws Exception
    {
        final ThrottledLogger logger = new ThrottledLogger(Level.FINE, 2.0);
        assertTrue(logger.log("OK"));
        assertTrue(logger.log("Another"));
        assertFalse(logger.log("SHOULD NOT SEE THIS!"));
        assertFalse(logger.log("NOR THIS!"));
        Thread.sleep(2200);
        assertTrue(logger.log("OK Again"));
    }
}
