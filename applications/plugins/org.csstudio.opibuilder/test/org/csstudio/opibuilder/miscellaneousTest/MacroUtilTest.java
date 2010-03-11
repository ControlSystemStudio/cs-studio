package org.csstudio.opibuilder.miscellaneousTest;


import static org.junit.Assert.assertEquals;

import org.csstudio.opibuilder.util.IMacroTableProvider;
import org.csstudio.opibuilder.util.InfiniteLoopException;
import org.csstudio.opibuilder.util.MacroUtil;
import org.junit.Test;

class TestMacroTableProvider implements IMacroTableProvider{

	public String getMacroValue(String macroName) {
		if(macroName.equals("ABC"))
			return "DEF";				
		if(macroName.equals("123"))
			return "456";
		if(macroName.equals("abc_456_def"))
			return "789";		
		if(macroName.equals("A"))
			return "$(B)";		
		if(macroName.equals("B"))
			return "C";
		if(macroName.equals("C"))
			return "D";
		if(macroName.equals("1"))
			return "$(2)";		
		if(macroName.equals("2"))
			return "$(1)";
		
		return null;
	}
	
}

public class MacroUtilTest {

	
	@Test
	public void testReplacemacros() throws InfiniteLoopException{		
		//simple test
		String input = "$(ABC)";		
		String result = MacroUtil.replaceMacros(input, new TestMacroTableProvider());
		assertEquals("DEF", result);
		
		//nested macro string test
		input = "$($(abc_$(123)_def))";		
		result = MacroUtil.replaceMacros(input, new TestMacroTableProvider());
		assertEquals("$(789)", result);
		
		//nested macro table test
		input = "$(A)";		
		result = MacroUtil.replaceMacros(input, new TestMacroTableProvider());
		assertEquals("C", result);
		
		//throw exception when infinite loop detected
		try {
			input = "$(1)";		
			result = MacroUtil.replaceMacros(input, new TestMacroTableProvider());			
		} catch (InfiniteLoopException e) {			
			result = "InfiniteLoopException";
		}
		assertEquals("InfiniteLoopException", result);
		
		//robust parsing test
		input = "$($($(abc_$(123)_def)))Hello $($($(A)))Best OPI $(ABC)D) Yet ${ABC}))!";
		result = MacroUtil.replaceMacros(input, new TestMacroTableProvider());
		assertEquals("$($(789))Hello $(D)Best OPI DEFD) Yet DEF))!", result);
	
	}
	
}
