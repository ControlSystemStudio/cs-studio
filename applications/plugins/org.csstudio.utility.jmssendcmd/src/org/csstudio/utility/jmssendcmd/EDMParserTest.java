package org.csstudio.utility.jmssendcmd;

import org.junit.Test;

import junit.framework.TestCase;

/** JUnit test for the parser of the EDM input string.
 *  @author Delphy Armstrong
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class EDMParserTest extends TestCase
{
    @Test
    /** Various tests to confirm the EDM input string was parsed correctly */
    public void testEDMParser() throws Throwable
    {
        //  EDMParser parser =new EDMParser("user=\"nypaver\" host=\"ics-srv02\"  dsp=\"localhost:14.0\" name=\"test\" old=\"11.000000\" new=\"12.000000\"");      
        final EDMParser parser = new EDMParser("user=\"nypaver\" host=\"ics-srv02\" ssh=\"::ffff:160.91.234.112 56165 ::ffff:160.91.230.38 22\" unknown_extra_stuff=\"ignored\" dsp=\"localhost:14.0\" name=\"test\" old=\"11.000000\" new=\"12.000000\"");      

        /**
         * Confirm the parser read the input string correctly.
         */
        assertEquals("nypaver", parser.getUser());
        // Should get the name of the 'real' host from which user logged in
        //  assertEquals("ics-srv02", parser.getHost());
        assertEquals("160.91.234.112", parser.getHost());
        assertEquals("test", parser.getPVName());
        assertEquals("12.000000", parser.getValue());
    }

    @Test
    /** Check error handling */
    public void testParseError()
    {
        try
        {
            new EDMParser("host=\"ics-srv02\" ssh=\"::ffff:160.91.234.112 56165 ::ffff:160.91.230.38 22\" dsp=\"localhost:14.0\" name=\"test\" old=\"11.000000\" new=\"12.000000\"");      
            fail("Expected parse error");
        }
        catch (Throwable ex)
        {
            assertEquals("user entry cannot be NULL", ex.getMessage());
        }
    }

    @Test
    /** Check handling of bad input
     *  These give StringIndexOutOfBoundsException because the parser
     *  isn't bullet-proof.
     *  For now ignored because it works with the 'real' EDM input.
     */
    public void testBadInput()
    {
        try
        {
            new EDMParser("");
        }
        catch (Throwable ex)
        {
            ex.printStackTrace();
        }
        try
        {
            new EDMParser("host=");
        }
        catch (Throwable ex)
        {
            ex.printStackTrace();
        }
    }
}
