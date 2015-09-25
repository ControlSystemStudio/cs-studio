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

import org.junit.BeforeClass;
import org.junit.Test;

/** JUnit test/demo of the MacroUtil
 *  @author Xihui Chen, Kay Kasemir
 */
@SuppressWarnings("nls")
public class MacroUtilUnitTest
{
    static IMacroTableProvider macros;
    static IMacroTableProvider emptyMacros;

    @BeforeClass
    public static void setUp() throws Exception
    {
        emptyMacros = new MacroTable("");

        // Actual macros
        macros = new MacroTable("ABC=DEF, 123=456, abc_456_def=789, " +
                    "A=$(B), B=C, C=D, " +
                    "1=$(2), 2=$(3), 3=a$(A)${1}," +
                    "d=hello, e= !, f =$(d)-world$(e)");

        System.out.println("Macros: " + macros);
    }

    @Test
    public void noMacroStringIsUnchangedWithEmptyMacroTable() throws Exception
    {
        assertEquals("No Change", MacroUtil.replaceMacros("No Change", emptyMacros));
    }
    @Test
    public void noMacroStringIsUnchangedWithEmptyNonEmptyMacroTable() throws Exception
    {
        assertEquals("No Change", MacroUtil.replaceMacros("No Change", macros));
    }

    @Test
    public void undefinedMacroIsUnchangedWithEmptyMacroTable() throws Exception
    {
        assertEquals("$(undefined)", MacroUtil.replaceMacros("$(undefined)", emptyMacros));
    }

    @Test
    public void undefinedMacroIsUnchangedWithNonEmptyMacroTable() throws Exception
    {
        assertEquals("$(undefined)", MacroUtil.replaceMacros("$(undefined)", macros));
    }

    @Test
    public void simpleReplacementMadeWithRoundBraces() throws Exception
    {
        assertEquals("DEF", MacroUtil.replaceMacros("$(ABC)", macros));
    }

    @Test
    public void simpleReplacementMadeWithCurleyBraces() throws Exception
    {
        assertEquals("DEF", MacroUtil.replaceMacros("${ABC}", macros));
    }

    @Test
    public void simpleReplacementMadeWithMixedBraces() throws Exception
    {
        assertEquals("DEF", MacroUtil.replaceMacros("$(ABC}", macros));
    }

    @Test
    public void repeatedReplacementInSameString() throws Exception
    {
        assertEquals("DEF DEF", MacroUtil.replaceMacros("$(ABC) $(ABC)", macros));
    }

    @Test
    public void simpleReplacementMadesWithBothBraces() throws Exception
    {
        //Both type of braces
        assertEquals("DEF 456", MacroUtil.replaceMacros("${ABC} $(123)", macros));
    }

    @Test
    public void macroInStringIsReplacedLeavingOuterMacro() throws Exception
    {
        //nested macro string test
        String input = "$($(abc_$(123)_def))";
        String result = MacroUtil.replaceMacros(input, macros);
        assertEquals("$(789)", result);
    }

    @Test
    public void nestedMacrosInTableAreExpandedCorrectly() throws Exception
    {
        //nested macro table test A->B->C
        assertEquals("C", MacroUtil.replaceMacros("$(A)", macros));
    }

    @Test
    public void specialCharacterHandledCorrectly() throws Exception
    {
        //special character and boundary test
        String input = "($(B))";
        String result = MacroUtil.replaceMacros(input, macros);
        assertEquals("(C)", result);
    }

    @Test
    public void complexMultipleMacroCaseHandledCorrectly() throws Exception
    {
        //robust parsing test
        String input = "$($($(abc_$(123)_def)))Hello $($($(A)))Best OPI $(ABC)D) Yet ${ABC}))!";
        String result = MacroUtil.replaceMacros(input, macros);
        assertEquals("$($(789))Hello $(D)Best OPI DEFD) Yet DEF))!", result);
    }

    @Test
    public void nestedTableMacroWithWrappingTextIsHandledCorrectly() throws Exception
    {
        assertEquals("hello-world!!", MacroUtil.replaceMacros("$(f)!", macros));
    }

    @Test(expected=InfiniteLoopException.class)
    public void exceptionRaisedIfInfiniteLoop() throws Exception
    {
        String input = "abc$(123)$(1)";
        MacroUtil.replaceMacros(input, macros);
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
