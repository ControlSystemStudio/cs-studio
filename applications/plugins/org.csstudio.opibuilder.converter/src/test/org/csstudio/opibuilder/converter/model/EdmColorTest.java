/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter.model;

import junit.framework.TestCase;

public class EdmColorTest extends TestCase {

	private EdmAttribute initStaticAttribute() {

		EdmAttribute a = new EdmAttribute("\"blinking purple\"");
		a.appendValue("49344 0 49344");

		return a;
	}

	private EdmAttribute initStaticBlinkAttribute() {

		EdmAttribute a = new EdmAttribute("\"blinking purple\"");
		a.appendValue("49344 0 49344");
		a.appendValue("0 0 0");

		return a;
	}

	public void testStaticEdmColor() throws EdmException {

		EdmColor c = new EdmColor(initStaticBlinkAttribute(), true);

		assertEquals("blinking purple", c.getName());
		assertEquals(49344, c.getRed());
		assertEquals(0, c.getGreen());
		assertEquals(49344, c.getBlue());

		assertEquals(true, c.isBlinking());

		assertEquals(0, c.getBlinkRed());
		assertEquals(0, c.getBlinkGreen());
		assertEquals(0, c.getBlinkBlue());

		c = new EdmColor(initStaticAttribute(), true);

		assertEquals("blinking purple", c.getName());
		assertEquals(49344, c.getRed());
		assertEquals(0, c.getGreen());
		assertEquals(49344, c.getBlue());

		assertEquals(false, c.isBlinking());
	}

	public void testIndexEdmColor() throws EdmException {

		System.setProperty("edm2xml.robustParsing", "false");
		System.setProperty("edm2xml.colorsFile", "src/test/resources/colors.list");

		// generate EdmColor from index & list
		EdmModel.getInstance();
		EdmColor testC = new EdmColor(new EdmAttribute("index 142"), true);
		assertEquals("index 142", testC.getValue(0));

		assertEquals("blinking purple", testC.getName());
		assertEquals(49344, testC.getRed());
		assertEquals(0, testC.getGreen());
		assertEquals(49344, testC.getBlue());

		assertEquals(true, testC.isBlinking());

		assertEquals(0, testC.getBlinkRed());
		assertEquals(0, testC.getBlinkGreen());
		assertEquals(0, testC.getBlinkBlue());
	}

	public void testRgbEdmColor() throws EdmException {
		//topShadowColor rgb 0 0 0

		EdmColor c = new EdmColor(new EdmAttribute("rgb 1 2 3"), true);

		assertEquals(null, c.getName());
		assertEquals(1, c.getRed());
		assertEquals(2, c.getGreen());
		assertEquals(3, c.getBlue());

		assertEquals(false, c.isBlinking());
	}

	public void testOptionality() throws EdmException {

		System.setProperty("edm2xml.robustParsing", "false");
		System.setProperty("edm2xml.colorsFile", "src/test/resources/colors.list");

		EdmModel.getInstance();
		EdmColor testC = new EdmColor(new EdmAttribute("index 142"), false);
		assertEquals(false, testC.isRequired());
		assertEquals(true, testC.isInitialized());

		EdmColor testC2 = new EdmColor(null, false);
		assertEquals(false, testC2.isRequired());
		assertEquals(false, testC2.isInitialized());
	}
}
