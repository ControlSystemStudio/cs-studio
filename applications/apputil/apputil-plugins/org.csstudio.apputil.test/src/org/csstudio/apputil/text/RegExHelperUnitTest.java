/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.apputil.text;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.regex.Pattern;

import org.junit.Test;

/** JUnit test for RegExHelper.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class RegExHelperUnitTest
{
    @Test
    public void testRegexFromGlob()
    {
        // Plain
        assertEquals("Fred", RegExHelper.regexFromGlob("Fred"));

        // Simplify
        assertEquals("Fred", RegExHelper.regexFromGlob("***Fred***"));

        // Mask '.'
        assertEquals("ab\\.cd", RegExHelper.regexFromGlob("ab.cd"));
        // Convert glob '?'
        assertEquals("Duh.Dah.", RegExHelper.regexFromGlob("Duh?Dah?"));
        // Convert glob '*'
        assertEquals("Duh.*Dah.*XYZ", RegExHelper.regexFromGlob("Duh*Dah*XYZ"));

        // PVs
        assertEquals("SCL_..RF:IOC.*:Load", RegExHelper.regexFromGlob("SCL_??RF:IOC*:Load"));
    }

    @Test
    public void testMatch()
    {
        final Pattern pattern =
            Pattern.compile(RegExHelper.fullRegexFromGlob("me*MPS"));
        assertTrue(pattern.matcher("Some_MPS:PV").matches());
        assertFalse(pattern.matcher("Another_MPS:PV").matches());
    }
}
