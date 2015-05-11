/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.apputil.macros;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

/** JUnit test/demo of the MacroUtil
 *  @author Xihui Chen, Kay Kasemir
 */
@SuppressWarnings("nls")
public class MacroUtilUnitTest
{
    @Test
    public void testReplacemacros() throws Exception
    {
        // The there-are-no-macros case
        final IMacroTableProvider nothing = new MacroTable("");
        assertEquals("No Change", MacroUtil.replaceMacros("No Change", nothing));

        assertEquals("$(undefined)", MacroUtil.replaceMacros("$(undefined)", nothing));

        // Actual macros
        final IMacroTableProvider macros =
            new MacroTable("ABC=DEF, 123=456, abc_456_def=789, " +
                    "A=$(B), B=C, C=D, " +
                    "1=$(2), 2=$(3), 3=a$(A)${1}," +
                    "d=hello, e= !, f =$(d)-world$(e)");

        System.out.println("Macros: " + macros);

        //simple test
        String input = "$(ABC)";
        String result = MacroUtil.replaceMacros(input, macros);
        assertEquals("DEF", result);

        //Both type of braces
        assertEquals("DEF 456", MacroUtil.replaceMacros("${ABC} $(123)", macros));

        //nested macro string test
        input = "$($(abc_$(123)_def))";
        result = MacroUtil.replaceMacros(input, macros);
        assertEquals("$(789)", result);

        //nested macro table test
        input = "$(A)";
        result = MacroUtil.replaceMacros(input, macros);
        assertEquals("C", result);

        //throw exception when infinite loop detected
        try
        {
            input = "abc$(123)$(1)";
            result = MacroUtil.replaceMacros(input, macros);
        }
        catch (InfiniteLoopException e)
        {
            result = "InfiniteLoopException";
        }
        assertEquals("InfiniteLoopException", result);

        //special character and boundary test
        input = "($(B))";
        result = MacroUtil.replaceMacros(input, macros);
        assertEquals("(C)", result);

        //robust parsing test
        input = "$($($(abc_$(123)_def)))Hello $($($(A)))Best OPI $(ABC)D) Yet ${ABC}))!";
        result = MacroUtil.replaceMacros(input, macros);
        assertEquals("$($(789))Hello $(D)Best OPI DEFD) Yet DEF))!", result);

        input = "$(f)!";
        result = MacroUtil.replaceMacros(input, macros);
        assertEquals("hello-world!!", result);
    }

    @Test
    public void testHashMapInput() throws Exception
    {
        // Handle null map
        Map<String, String> map = null;
        MacroTable macros = new MacroTable(map);
        assertEquals("Test", MacroUtil.replaceMacros("Test", macros));

        // Handle 'normal' map
        map = new HashMap<String, String>();
        map.put("KEY", "value");
        macros = new MacroTable(map);
        assertEquals("Key is value", MacroUtil.replaceMacros("Key is $(KEY)", macros));
    }
}
