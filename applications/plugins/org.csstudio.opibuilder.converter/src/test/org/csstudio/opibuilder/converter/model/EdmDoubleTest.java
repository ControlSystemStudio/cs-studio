/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter.model;

import junit.framework.TestCase;

public class EdmDoubleTest extends TestCase {

	public void testEdmDouble() throws EdmException {

		// required
		EdmDouble d1 = new EdmDouble(new EdmAttribute("13.14"), true);
		assertEquals(13.14, d1.get());
		assertEquals(true, d1.isRequired());
		assertEquals(true, d1.isInitialized());
		
		// required null
		try {
			EdmDouble d2 = new EdmDouble(null, true);
			assertEquals(true, d2.isRequired());
			assertEquals(false, d2.isInitialized());
		}
		catch (EdmException e) {
			assertEquals(EdmException.REQUIRED_ATTRIBUTE_MISSING, e.getType());
		}
		
		// optional
		EdmDouble d3 = new EdmDouble(new EdmAttribute("13.15"), false);
		assertEquals(13.15, d3.get());
		assertEquals(false, d3.isRequired());
		assertEquals(true, d3.isInitialized());
		
		// optional null
		EdmDouble d4;
		d4 = new EdmDouble(null, false);
		assertEquals(false, d4.isRequired());
		assertEquals(false, d4.isInitialized());
	}
	
	public void testWrongInput() throws EdmException {
		EdmAttribute a = new EdmAttribute("abc");
		
		try {
			a = new EdmDouble(a, true);
		}
		catch (EdmException e) {
			assertEquals(EdmException.DOUBLE_FORMAT_ERROR, e.getType());
		}
		assertFalse(a.isInitialized());
	}
	
	public void testWrongInput2() throws EdmException {
		EdmAttribute a = new EdmAttribute("abc");
		
		try {
			new EdmDouble(a, false);
		}
		catch (EdmException e) {
			assertEquals(EdmException.DOUBLE_FORMAT_ERROR, e.getType());
		}
		assertFalse(a.isInitialized());
	}
}
