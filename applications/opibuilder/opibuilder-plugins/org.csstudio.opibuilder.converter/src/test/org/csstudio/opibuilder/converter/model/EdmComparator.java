/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter.model;

import junit.framework.TestCase;

public class EdmComparator extends TestCase {

	public static void isColorEqual(EdmColor c1, EdmColor c2) throws EdmException {
		
		if (c1.getName() != "")
			assertEquals(c1.getName(), c2.getName());
		
		assertEquals(c1.getRed(), c2.getRed());
		assertEquals(c1.getGreen(), c2.getGreen());
		assertEquals(c1.getBlue(), c2.getBlue());
		
		if (c1.isBlinking() || c2.isBlinking()) {
			assertEquals(c1.getBlinkRed(), c2.getBlinkRed());
			assertEquals(c1.getBlinkGreen(), c2.getBlinkGreen());
			assertEquals(c1.getBlinkBlue(), c2.getBlinkBlue());
		}
	}
	
	public static void isFontEqual(String ctrlFontString, EdmFont testFont) {
		try {
			EdmFont ctrlFont = new EdmFont(new EdmAttribute(ctrlFontString), true);
			
			assertEquals(ctrlFont.getName(), testFont.getName());
			assertEquals(ctrlFont.isBold(), testFont.isBold());
			assertEquals(ctrlFont.isItalic(), testFont.isItalic());
			assertEquals(ctrlFont.getSize(), testFont.getSize());
		} catch (EdmException e) {
			fail("Invalid control font format.");
		}
	}
	
	public void testIsColorEqual() throws EdmException {
		EdmAttribute a = new EdmAttribute("\"blinking purple\"");
		a.appendValue("49344 0 49344");
		a.appendValue("0 0 0");
		EdmColor c = new EdmColor(a, true);
		
		isColorEqual(c, c);
	}
	
	public void testIsFontEqual() throws EdmException {
		String fontString = "helvetica-bold-r-14.0";
		EdmAttribute a = new EdmAttribute(fontString);
		
		EdmFont f = new EdmFont(a, true);
		
		isFontEqual(fontString, f);
	}
}

