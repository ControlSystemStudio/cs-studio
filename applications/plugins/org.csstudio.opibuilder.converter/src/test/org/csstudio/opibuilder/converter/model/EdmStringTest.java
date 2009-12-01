package org.csstudio.opibuilder.converter.model;

import junit.framework.TestCase;

public class EdmStringTest extends TestCase {
	
	public void testEdmString() throws EdmException {
		EdmAttribute a = new EdmAttribute("abcd123.4");
		EdmString s = new EdmString(a, true);
		
		assertEquals("abcd123.4", s.get());
		assertEquals(true, s.isRequired());
		assertEquals(true, s.isInitialized());
		
		// empty
		a = new EdmAttribute("");
		s = new EdmString(a, true);
		
		assertEquals("", s.get());
		assertEquals(true, s.isRequired());
		assertEquals(true, s.isInitialized());
		
		
		// optional
		a = new EdmAttribute("abcd123.4");
		s = new EdmString(a, false);
		assertEquals(false, s.isRequired());
		assertEquals(true, s.isInitialized());
		
		s = new EdmString(null, false);
		assertEquals(false, s.isRequired());
		assertEquals(false, s.isInitialized());
	}
}
