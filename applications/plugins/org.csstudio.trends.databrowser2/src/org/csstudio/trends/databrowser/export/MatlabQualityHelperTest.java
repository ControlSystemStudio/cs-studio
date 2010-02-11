package org.csstudio.trends.databrowser.export;

import static org.junit.Assert.*;

import org.csstudio.platform.data.ValueFactory;
import org.junit.Test;


/** JUnit test of the MatlabQualityHelper
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class MatlabQualityHelperTest
{
    @Test
    public void testMatlabQualityHelper()
    {
        final MatlabQualityHelper quality_helper = new MatlabQualityHelper();
        
        // Assume quality codes are assigned first-come, so the first one is 0
        assertEquals(0, quality_helper.getQualityCode(ValueFactory.createOKSeverity(), "OK"));
        // then 1
        assertEquals(1, quality_helper.getQualityCode(ValueFactory.createInvalidSeverity(), "READ"));
        
        // Looking for the same severity/status again gives the same code
        assertEquals(1, quality_helper.getQualityCode(ValueFactory.createInvalidSeverity(), "READ"));
        
        // New code
        assertEquals(2, quality_helper.getQualityCode(ValueFactory.createInvalidSeverity(), "WRITE"));

        // Check codes learned so far
        assertEquals(3, quality_helper.getNumCodes());
        assertEquals("INVALID/WRITE", quality_helper.getQuality(2));
    }
}
