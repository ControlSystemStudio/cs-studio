package org.csstudio.apputil.text;

import static org.junit.Assert.*;

import org.junit.Test;

/** JUnit test for RegExHelper.
 *  @author Kay Kasemir
 */
public class RegExHelperTest
{
    @SuppressWarnings("nls")
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
}
