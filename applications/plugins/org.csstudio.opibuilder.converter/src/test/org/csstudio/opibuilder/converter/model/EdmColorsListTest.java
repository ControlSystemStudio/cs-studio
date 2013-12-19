/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter.model;

import junit.framework.TestCase;

import org.csstudio.opibuilder.converter.parser.EdmColorsListParser;

public class EdmColorsListTest extends TestCase {

	private EdmAttribute initStaticAttribute() {
		
		EdmAttribute a = new EdmAttribute("\"blinking purple\"");
		a.appendValue("49344 0 49344");
		a.appendValue("0 0 0");
		
		return a;
	}
	
	public void testEdmColorsList() throws EdmException {
		
		String colorsFile = "src/test/resources/colors_example.list";
		EdmColorsListParser p = new EdmColorsListParser(colorsFile);
		
		EdmColorsList cList = new EdmColorsList(p.getRoot());
		
		assertEquals(7, cList.getCount());
		
		EdmColor testC = cList.getColor(0);
		assertEquals("Disconn/Invalid", testC.getName());
		assertEquals(65535, testC.getRed());
		assertEquals(65535, testC.getGreen());
		assertEquals(65535, testC.getBlue());
		assertEquals(false, testC.isBlinking());
		
		testC = cList.getColor(107);
		assertEquals("Top Shadow", testC.getName());
		assertEquals(60652, testC.getRed());
		assertEquals(60652, testC.getGreen());
		assertEquals(60652, testC.getBlue());
		assertEquals(false, testC.isBlinking());
		
		testC = cList.getColor(5);
		assertEquals("Wid-alt/Anno-sec", testC.getName());
		assertEquals(44718, testC.getRed());
		assertEquals(44718, testC.getGreen());
		assertEquals(44718, testC.getBlue());
		assertEquals(false, testC.isBlinking());
		
		testC = cList.getColor(6);
		assertEquals("GLOBAL title", testC.getName());
		assertEquals(40606, testC.getRed());
		assertEquals(40606, testC.getGreen());
		assertEquals(40606, testC.getBlue());
		assertEquals(false, testC.isBlinking());
		
		testC = cList.getColor(112);
		assertEquals("black", testC.getName());
		assertEquals(0, testC.getRed());
		assertEquals(0, testC.getGreen());
		assertEquals(0, testC.getBlue());
		assertEquals(false, testC.isBlinking());
		
		testC = cList.getColor(142);
		assertEquals("blinking purple", testC.getName());
		assertEquals(49344, testC.getRed());
		assertEquals(0, testC.getGreen());
		assertEquals(49344, testC.getBlue());
		assertEquals(true, testC.isBlinking());
		assertEquals(0, testC.getBlinkRed());
		assertEquals(0, testC.getBlinkGreen());
		assertEquals(0, testC.getBlinkBlue());
		
		testC = cList.getColor(155);
		assertEquals("bl in ki ng purple", testC.getName());
		assertEquals(7852, testC.getRed());
		assertEquals(4427, testC.getGreen());
		assertEquals(450, testC.getBlue());
		assertEquals(true, testC.isBlinking());
		assertEquals(1, testC.getBlinkRed());
		assertEquals(786, testC.getBlinkGreen());
		assertEquals(5, testC.getBlinkBlue());
		
		EdmColor c = new EdmColor(initStaticAttribute(), true);
		int index = 456;
		cList.addColor(index, c);
		
		assertEquals(8, cList.getCount());
		
		testC = cList.getColor(index);
		assertEquals(c.getName(), testC.getName());
		assertEquals(c.getRed(), testC.getRed());
		assertEquals(c.getGreen(), testC.getGreen());
		assertEquals(c.getBlue(), testC.getBlue());
	}
}
