package org.csstudio.display.pace.model;

import static org.junit.Assert.*;

import org.junit.Test;

/** JUnit test of Macro
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class MacroTest
{
    @Test
    public void testMacros() throws Exception
    {
        final Macro[] macros = Macro.fromList("S=Test,N=2");
        assertEquals(2, macros.length);

        assertEquals("Test_RF:Gadget${N}:Signal",
                macros[0].apply("${S}_RF:Gadget${N}:Signal"));
        
        assertEquals("${S}_RF:Gadget2:Signal",
                macros[1].apply("${S}_RF:Gadget${N}:Signal"));
        
        assertEquals("Test_RF:Gadget2:Signal",
                Macro.apply(macros, "${S}_RF:Gadget${N}:Signal"));
    }
}
