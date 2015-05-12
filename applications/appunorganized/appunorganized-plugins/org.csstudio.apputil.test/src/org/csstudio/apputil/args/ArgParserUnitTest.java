/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.apputil.args;

import org.junit.Test;
import static org.junit.Assert.*;

/** JUnit test and demo of the ArgParser.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ArgParserUnitTest
{
    // Simulated arguments that would come from main() or
    // context.getArguments().get("application.args")
    final String args[] = new String[]
    {
        "-url", "http://a.b.c/d",
        "-port", "4813",
        "-help",
        "another", "parameter"
    };

    @Test
    public void testErrorForUnknownOption() throws Exception
    {
        final ArgParser parser = new ArgParser();
        try
        {
            parser.parse(args);
            fail("Should have reported unknown option");
        }
        catch (Exception ex)
        {
            assertEquals("Unknown option '-url'", ex.getMessage());
        }
    }

    @Test
    public void testErrorForExtraParameters() throws Exception
    {
        final ArgParser parser = new ArgParser();
        new BooleanOption(parser, "-help", "Help");
        new StringOption(parser, "-url", "URL", "http://localhost");
        new IntegerOption(parser, "-port", "TCP Port", 4812);
        try
        {
            parser.parse(args);
            fail("Should have reported extra parameters");
        }
        catch (Exception ex)
        {
            assertEquals("Extra, non-option parameter 'another'", ex.getMessage());
        }
    }

    @Test
    public void testArgs() throws Exception
    {
        // Create parser for the "-..." options
        final ArgParser parser = new ArgParser(true);
        final BooleanOption help = new BooleanOption(parser, "-help", "Help");
        final StringOption url =
            new StringOption(parser, "-url", "http://...", "URL", "http://localhost");
        final IntegerOption port = new IntegerOption(parser, "-port", "TCP Port", 4812);

        // Show help
        System.out.println(parser.getHelp());

        // Check if option match is somewhat flexible
        assertTrue(help.matchesThisOption("-help"));
        assertTrue(help.matchesThisOption("-hel"));
        assertTrue(help.matchesThisOption("-he"));
        assertTrue(help.matchesThisOption("-h"));
        assertFalse(help.matchesThisOption("help"));
        assertFalse(help.matchesThisOption("-egon"));

        // Parse arguments
        try
        {
            parser.parse(args);
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
            System.out.println(parser.getHelp());
            throw ex;
        }
        // Check received options
        assertEquals(true, help.get());
        assertEquals("http://a.b.c/d", url.get());
        assertEquals(4813, port.get());

        // Check remaining arguments (without '-...')
        final String extra[] = parser.getExtraParameters();
        assertEquals(2, extra.length);
        assertEquals("another", extra[0]);
        assertEquals("parameter", extra[1]);
    }
}
