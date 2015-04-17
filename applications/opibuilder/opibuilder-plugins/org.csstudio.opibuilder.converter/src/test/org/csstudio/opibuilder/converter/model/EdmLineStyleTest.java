/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter.model;

import junit.framework.TestCase;

public class EdmLineStyleTest extends TestCase {

	public void testEdmLineStyle() throws EdmException {

		// required
		EdmLineStyle i1 = new EdmLineStyle(new EdmAttribute("solid"), true);
		assertEquals(EdmLineStyle.SOLID, i1.get());
		assertEquals(true, i1.isRequired());
		assertEquals(true, i1.isInitialized());
		
		// required null
		try {
			EdmLineStyle i2 = new EdmLineStyle(null, true);
			assertEquals(true, i2.isRequired());
			assertEquals(false, i2.isInitialized());
		}
		catch (EdmException e) {
			assertEquals(EdmException.REQUIRED_ATTRIBUTE_MISSING, e.getType());
		}
		
		// optional
		EdmLineStyle i3 = new EdmLineStyle(new EdmAttribute("dash"), false);
		assertEquals(EdmLineStyle.DASH, i3.get());
		assertEquals(false, i3.isRequired());
		assertEquals(true, i3.isInitialized());
		
		// optional null
		EdmLineStyle i4;
		i4 = new EdmLineStyle(null, false);
		assertEquals(false, i4.isRequired());
		assertEquals(false, i4.isInitialized());
	}
	
	public void testWrongInput() throws EdmException {
		EdmAttribute a = new EdmAttribute("dotted");
		
		try {
			a = new EdmLineStyle(a, true);
		}
		catch (EdmException e) {
			assertEquals(EdmException.SPECIFIC_PARSING_ERROR, e.getType());
		}
		assertFalse(a.isInitialized());
	}
}
