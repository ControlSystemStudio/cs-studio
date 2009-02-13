package org.csstudio.utility.jmssendcmd;

import junit.framework.TestCase;

public class EDMParserTest extends TestCase
{

   @SuppressWarnings("null")
   public void testEDMParser()
   {
      EDMParser parser =new EDMParser("user=\"nypaver\" host=\"ics-srv02\" ssh=\"::ffff:160.91.234.112 56165 ::ffff:160.91.230.38 22\" dsp=\"localhost:14.0\" name=\"test\" old=\"11.000000\" new=\"12.000000\"");      
      assertEquals("nypaver", parser.getUser());
   }

}
