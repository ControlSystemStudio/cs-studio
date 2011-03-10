/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter.model;

import junit.framework.TestCase;

public class EdmFontTest extends TestCase {
	
	public void testEdmFont() throws EdmException {
		EdmAttribute a = new EdmAttribute("helvetica-bold-r-14.0");
		
		EdmFont f = new EdmFont(a, true);
		assertEquals("helvetica", f.getName());
		assertEquals(true, f.isBold());
		assertEquals(false, f.isItalic());
		assertEquals(14.0, f.getSize());
		
		assertEquals(true, f.isRequired());
		assertEquals(true, f.isInitialized());
	}
	
	public void testWrongFormat() throws EdmException {
		EdmAttribute a = new EdmAttribute("helveticabold-r-14.0");
		
		try {
			new EdmFont(a, true);
		}
		catch (EdmException e) {
			assertEquals(EdmException.FONT_FORMAT_ERROR, e.getType());
		}
		assertEquals(true, a.isRequired());
		assertEquals(false, a.isInitialized());
	}
	
	public void testWrongData() throws EdmException {
		EdmAttribute a = new EdmAttribute("helvetica-boldd-r-14.0");
		
		try {
			a = new EdmFont(a, true);
		}
		catch (EdmException e) {
			assertEquals(EdmException.SPECIFIC_PARSING_ERROR, e.getType());
		}
		assertEquals(true, a.isRequired());
		assertEquals(false, a.isInitialized());
	}
	
	public void testOptionality() throws EdmException {
		EdmFont f = new EdmFont(new EdmAttribute("helvetica-bold-r-14.0"), false);
		assertEquals(false, f.isRequired());
		assertEquals(true, f.isInitialized());
		
		EdmFont f2 = new EdmFont(null, false);
		assertEquals(false, f2.isRequired());
		assertEquals(false, f2.isInitialized());
	}
}
