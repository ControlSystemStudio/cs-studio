package org.csstudio.apputil.args;

import org.junit.Test;
import static org.junit.Assert.*;

/** JUnit test and demo of the ArgParser.
 *  @author Kay Kasemir
 */
public class ArgsTest
{
    @SuppressWarnings("nls")
    @Test
    public void testArgs()
    {
        final String args[] = new String[]
        { "-url", "http://a.b.c/d", "-port", "4813", "-help" };
        
        final ArgParser parser = new ArgParser();
        final BooleanOption help = new BooleanOption(parser, "-help", "Help");
        final StringOption url =
            new StringOption(parser, "-url", "URL", "http://localhost");
        final IntegerOption port = new IntegerOption(parser, "-port", "TCP Port", 4812);
        
        assertTrue(help.matchesThisOption("-help"));
        assertTrue(help.matchesThisOption("-hel"));
        assertTrue(help.matchesThisOption("-he"));
        assertTrue(help.matchesThisOption("-h"));
        assertFalse(help.matchesThisOption("help"));
        assertFalse(help.matchesThisOption("-egon"));
        
        try
        {
            parser.parse(args);
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
            System.out.println(parser.getHelp());
            return;
        }
        assertEquals(true, help.get());
        assertEquals("http://a.b.c/d", url.get());
        assertEquals(4813, port.get());
    }
}
