package org.csstudio.display.pace.model;

import static org.junit.Assert.*;

import org.junit.Test;

/** JUnit test of Macro
 *  @author Kay Kasemir
 *  
 *     reviewed by Delphy 01/29/09
 */
@SuppressWarnings("nls")
public class MacroTest
{
    /** Various macro tests. Not expecting exceptions, passing them back up */
    @Test
    public void testMacros() throws Exception
    {
        // Test empty macro definition
        Macro[] macros = Macro.fromList("");
        assertEquals(0, macros.length);

        // Test 2 macros: S -> Test,  dummy empty one, N -> 2
        macros = Macro.fromList("S=Test,  , N = 2");
        // Found 2 macros?
        assertEquals(2, macros.length);

        // Replace ${S}
        assertEquals("Test_RF:Gadget${N}:Signal",
                macros[0].apply("${S}_RF:Gadget${N}:Signal"));
        
        // Replace ${N}
        assertEquals("${S}_RF:Gadget2:Signal",
                macros[1].apply("${S}_RF:Gadget${N}:Signal"));
        
        // Replace both macros
        assertEquals("Test_RF:Gadget2:Signal",
                Macro.apply(macros, "${S}_RF:Gadget$(N):Signal"));
    }
}
