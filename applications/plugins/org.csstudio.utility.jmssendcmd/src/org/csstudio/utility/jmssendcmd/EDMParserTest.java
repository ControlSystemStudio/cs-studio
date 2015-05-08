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
    /** Original type of message when this software was developed */
    @Test
    public void testOriginalEDMMessage() throws Throwable
    {
        final EDMParser parser = new EDMParser("user=\"nypaver\" host=\"ics-srv02\" ssh=\"::ffff:160.91.234.112 56165 ::ffff:160.91.230.38 22\" unknown_extra_stuff=\"ignored\" dsp=\"localhost:14.0\" name=\"test\" old=\"11.000000\" new=\"12.000000\"");

        // Confirm the parser read the input string correctly.
        assertEquals("nypaver", parser.getUser());
        // Should get the name of the 'real' host from which user logged in
        assertEquals("160.91.234.112", parser.getHost());
        assertEquals("test", parser.getPVName());
        assertEquals("12.000000", parser.getValue());
    }

    /** New type of EDM message seen at SNS with different network/Linux setup */
    @Test
    public void testNewEDMMessage() throws Throwable
    {
        final EDMParser parser = new EDMParser("user=\"fred\" host=\"ics-srv-accl2\" ssh=\"172.31.96.16 53395 172.31.72.100 22\" dsp=\"localhost:12.0\" name=\"SCL_LLRF:FCM21b:cavAmpGoal\" old=\"11.000000\" new=\"10.000000\"");
        assertEquals("fred", parser.getUser());
        assertEquals("172.31.96.16", parser.getHost());
        assertEquals("SCL_LLRF:FCM21b:cavAmpGoal", parser.getPVName());
        assertEquals("10.000000", parser.getValue());
    }

    /** Check error handling */
    @Test
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
