/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter.model;

import junit.framework.TestCase;

public class EdmIntTest extends TestCase {

	public void testEdmInt() throws EdmException {

		// required
		EdmInt i1 = new EdmInt(new EdmAttribute("13"), true);
		assertEquals(13, i1.get());
		assertEquals(true, i1.isRequired());
		assertEquals(true, i1.isInitialized());
		
		// required null
		try {
			EdmInt i2 = new EdmInt(null, true);
			assertEquals(true, i2.isRequired());
			assertEquals(false, i2.isInitialized());
		}
		catch (EdmException e) {
			assertEquals(EdmException.REQUIRED_ATTRIBUTE_MISSING, e.getType());
		}
		
		// optional
		EdmInt i3 = new EdmInt(new EdmAttribute("13"), false);
		assertEquals(13, i3.get());
		assertEquals(false, i3.isRequired());
		assertEquals(true, i3.isInitialized());
		
		// optional null
		EdmInt i4;
		i4 = new EdmInt(null, false);
		assertEquals(false, i4.isRequired());
		assertEquals(false, i4.isInitialized());
	}
	
	public void testWrongInput() throws EdmException {
		EdmAttribute a = new EdmAttribute("abc");
		
		try {
			a = new EdmInt(a, true);
		}
		catch (EdmException e) {
			assertEquals(EdmException.INTEGER_FORMAT_ERROR, e.getType());
		}
		assertFalse(a.isInitialized());
	}
	
	public void testWrongInput2() throws EdmException {
		EdmAttribute a = new EdmAttribute("abc");
		
		try {
			new EdmInt(a, false);
		}
		catch (EdmException e) {
			assertEquals(EdmException.INTEGER_FORMAT_ERROR, e.getType());
		}
		assertFalse(a.isInitialized());
	}
}
