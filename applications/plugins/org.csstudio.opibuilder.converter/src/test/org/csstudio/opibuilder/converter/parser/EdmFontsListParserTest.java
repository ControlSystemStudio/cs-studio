package org.csstudio.opibuilder.converter.parser;

import org.csstudio.opibuilder.converter.model.EdmException;
import junit.framework.TestCase;

public class EdmFontsListParserTest extends TestCase {
	
	private String fontsFile = "src/test/resources/fonts_example.list";
	
	public void testEdmFontsListParser() throws EdmException {
		
		EdmFontsListParser p = new EdmFontsListParser(fontsFile);
		
		assertEquals("attribute_count", 2, p.getRoot().getAttributeCount());
		
		assertEquals("bold", p.getRoot().getAttribute("courier").getValue(0));
		assertEquals("r", p.getRoot().getAttribute("courier").getValue(1));
		assertEquals("12.0", p.getRoot().getAttribute("courier").getValue(2));
		
		assertEquals("bold", p.getRoot().getAttribute("helvetica").getValue(0));
		assertEquals("i", p.getRoot().getAttribute("helvetica").getValue(1));
		assertEquals("18.0", p.getRoot().getAttribute("helvetica").getValue(2));
		
	}
}
