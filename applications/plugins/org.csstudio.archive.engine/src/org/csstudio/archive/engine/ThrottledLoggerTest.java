package org.csstudio.archive.engine;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.log4j.Level;
import org.junit.Test;

/** Test of the <code>ThrottledLogger</code>.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ThrottledLoggerTest
{
    @Test
    public void testLogInfo() throws Exception
    {
        final ThrottledLogger logger = new ThrottledLogger(Level.DEBUG, 2.0);
        assertTrue(logger.log("OK"));
        assertTrue(logger.log("Another"));
        assertFalse(logger.log("SHOULD NOT SEE THIS!"));
        assertFalse(logger.log("NOR THIS!"));
        Thread.sleep(2200);
        assertTrue(logger.log("OK Again"));
    }
}
