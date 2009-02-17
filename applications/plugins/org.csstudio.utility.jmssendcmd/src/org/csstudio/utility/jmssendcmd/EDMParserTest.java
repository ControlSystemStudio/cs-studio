package org.csstudio.utility.jmssendcmd;

import org.junit.Test;

import junit.framework.TestCase;

/** JUnit test for the parser of the EDM input string.
 *  @author Delphy Armstrong
 */
public class EDMParserTest extends TestCase
{
   @Test
   public void testEDMParser()
   {
      /** Various tests to confirm the EDM input string was parsed correctly */

    //  EDMParser parser =new EDMParser("user=\"nypaver\" host=\"ics-srv02\"  dsp=\"localhost:14.0\" name=\"test\" old=\"11.000000\" new=\"12.000000\"");      
      EDMParser parser =new EDMParser("user=\"nypaver\" host=\"ics-srv02\" ssh=\"::ffff:160.91.234.112 56165 ::ffff:160.91.230.38 22\" dsp=\"localhost:14.0\" name=\"test\" old=\"11.000000\" new=\"12.000000\"");      
/**
 * If an error exists in the parsed input string, return.
 */
      if(parser.hasError()) return;
      /**
       * Confirm the parser read the input string correctly.
       */
      assertEquals("nypaver", parser.getUser());
    //  assertEquals("ics-srv02", parser.getHost());
       assertEquals("160.91.234.112", parser.getHost());
       assertEquals("test", parser.getPVName());
       assertEquals("12.000000", parser.getValue());
   }

}
