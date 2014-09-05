/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.annunciator.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/** JUnit test of Severity
 *  Can be used as JUnit Plug-in test when using preferences,
 *  see comments below
 *  
 *  @author Kay Kasemir
 *  @author Delphy Nypaver
 *  
 *    reviewed by Delphy 1/29/09
 */
@SuppressWarnings("nls")
public class SeverityTest
{
    @Test
    public void testSeverities()
    {
        // Use the line below to read preferences.ini 
        // Severity.initialize(Preferences.getJMSSeverities());
       
        // Use the line below for a hardcoded string
        Severity.initialize("ERROR, MAJOR, MINOR, INFO, DEBUG");

        // Obtain severity for some names:
        final Severity major = Severity.fromString("MAJOR");
        final Severity info = Severity.fromString("INFO");
        final Severity info2 = Severity.forInfo();
        final Severity minor = Severity.fromString("MINOR");
        final Severity error = Severity.fromString("ERROR");
        final Severity debug = Severity.fromString("DEBUG");
        final Severity unknown = Severity.fromString("UNKNOWN");
        // Check at least one name
        assertEquals("MAJOR", major.getName());
        // Check comparison
        assertTrue(major.compareTo(minor) > 0);
        assertTrue(! major.equals(minor));
        assertTrue(info.compareTo(major) < 0);
        assertTrue(minor.compareTo(info) > 0);
        assertTrue(error.compareTo(info) > 0);
        assertTrue(unknown.compareTo(debug) < 0);
        // Check identity
        assertTrue(info == info2);
        assertTrue(info.equals(info2));
    }
}

