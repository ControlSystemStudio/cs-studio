/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter.model;

import junit.framework.TestCase;

public class Edm_activeRectangleClassTest extends TestCase {

	public void testEdm_activeRectangleClass() throws EdmException {

		System.setProperty("edm2xml.robustParsing", "false");
		System.setProperty("edm2xml.colorsFile", "src/test/resources/colors.list");

		String edlFile = "src/test/resources/EDMDisplayParser_example.edl";
		EdmModel.getInstance();
		EdmDisplay d = EdmModel.getDisplay(edlFile);

		//this entity represents activeRectClass in example file
		EdmEntity e = d.getSubEntity(0);
		assertTrue(e instanceof Edm_activeRectangleClass);
		Edm_activeRectangleClass r = (Edm_activeRectangleClass)e;

		assertEquals(4, r.getMajor());
		assertTrue(r.getAttribute("major") instanceof EdmInt);
		assertEquals(0, r.getMinor());
		assertTrue(r.getAttribute("minor") instanceof EdmInt);
		assertEquals(0, r.getRelease());
		assertTrue(r.getAttribute("release") instanceof EdmInt);
		assertEquals(4, r.getX());
		assertTrue(r.getAttribute("x") instanceof EdmInt);
		assertEquals(45, r.getY());
		assertTrue(r.getAttribute("y") instanceof EdmInt);
		assertEquals(111, r.getW());
		assertTrue(r.getAttribute("w") instanceof EdmInt);
		assertEquals(42, r.getH());
		assertTrue(r.getAttribute("h") instanceof EdmInt);

		EdmComparator.isColorEqual(r.getLineColor(), new EdmColor(7));
		assertTrue(r.getAttribute("lineColor") instanceof EdmColor);
		EdmComparator.isColorEqual(r.getFillColor(), new EdmColor(0));
		assertTrue(r.getAttribute("fillColor") instanceof EdmColor);

		assertEquals(2, r.getLineWidth());
		assertTrue(r.getAttribute("lineWidth") instanceof EdmInt);
		assertEquals(EdmLineStyle.DASH, r.getLineStyle().get());
		assertTrue(r.getAttribute("lineStyle") instanceof EdmLineStyle);
		
		assertTrue(r.isInvisible());
		assertTrue(r.getAttribute("invisible") instanceof EdmBoolean);
		
		assertEquals("$(S)_LLRF:FCM$(N):cavAmpCheck.SEVR", r.getVisPv());
		assertTrue(r.getAttribute("visPv") instanceof EdmString);
		assertEquals(-1.1, r.getVisMin());
		assertTrue(r.getAttribute("visMin") instanceof EdmDouble);
		assertEquals(10.78, r.getVisMax());
		assertTrue(r.getAttribute("visMax") instanceof EdmDouble);
		assertTrue(r.isVisInvert());
		assertTrue(r.getAttribute("visInvert") instanceof EdmBoolean);
	}
}
