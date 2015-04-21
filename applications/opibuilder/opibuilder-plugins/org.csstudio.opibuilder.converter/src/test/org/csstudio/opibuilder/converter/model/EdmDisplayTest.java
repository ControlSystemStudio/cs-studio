/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter.model;

import junit.framework.TestCase;

public class EdmDisplayTest extends TestCase {

	public void testEdmDisplay() throws EdmException {

		System.setProperty("edm2xml.robustParsing", "false");
		System.setProperty("edm2xml.colorsFile", "src/test/resources/colors.list");

		String edlFile = "src/test/resources/EDMDisplayParser_example.edl";
		//String edlFile = "test/LLRF_AUTO.edl";
		EdmModel.getInstance();
		EdmDisplay d = EdmModel.getDisplay(edlFile);

		assertEquals(4, d.getMajor());
		assertTrue(d.getAttribute("major") instanceof EdmInt);
		assertEquals(0, d.getMinor());
		assertTrue(d.getAttribute("minor") instanceof EdmInt);
		assertEquals(1, d.getRelease());
		assertTrue(d.getAttribute("release") instanceof EdmInt);
		assertEquals(614, d.getX());
		assertTrue(d.getAttribute("x") instanceof EdmInt);
		assertEquals(278, d.getY());
		assertTrue(d.getAttribute("y") instanceof EdmInt);
		assertEquals(280, d.getW());
		assertTrue(d.getAttribute("w") instanceof EdmInt);
		assertEquals(177, d.getH());
		assertTrue(d.getAttribute("h") instanceof EdmInt);

		EdmComparator.isFontEqual("helvetica-bold-r-14.0", d.getFont());
		assertTrue(d.getAttribute("font") instanceof EdmFont);
		EdmComparator.isFontEqual("helvetica-bold-r-14.0", d.getCtlFont());
		assertTrue(d.getAttribute("ctlFont") instanceof EdmFont);
		EdmComparator.isFontEqual("helvetica-bold-r-14.0", d.getBtnFont());
		assertTrue(d.getAttribute("btnFont") instanceof EdmFont);

		EdmComparator.isColorEqual(d.getFgColor(), new EdmColor(14));
		assertTrue(d.getAttribute("fgColor") instanceof EdmColor);
		EdmComparator.isColorEqual(d.getBgColor(), new EdmColor(3));
		assertTrue(d.getAttribute("bgColor") instanceof EdmColor);
		EdmComparator.isColorEqual(d.getTextColor(), new EdmColor(14));
		assertTrue(d.getAttribute("textColor") instanceof EdmColor);
		EdmComparator.isColorEqual(new EdmColor(new EdmAttribute("rgb 256 512 65535"), true), d.getCtlFgColor1());
		assertTrue(d.getAttribute("ctlFgColor1") instanceof EdmColor);
		EdmComparator.isColorEqual(d.getCtlFgColor2(), new EdmColor(30));
		assertTrue(d.getAttribute("ctlFgColor2") instanceof EdmColor);
		EdmComparator.isColorEqual(d.getCtlBgColor1(), new EdmColor(3));
		assertTrue(d.getAttribute("ctlBgColor1") instanceof EdmColor);
		EdmComparator.isColorEqual(d.getCtlBgColor2(), new EdmColor(3));
		assertTrue(d.getAttribute("ctlBgColor2") instanceof EdmColor);
		EdmComparator.isColorEqual(d.getTopShadowColor(), new EdmColor(1));
		assertTrue(d.getAttribute("topShadowColor") instanceof EdmColor);
		EdmComparator.isColorEqual(d.getBotShadowColor(), new EdmColor(11));
		assertTrue(d.getAttribute("botShadowColor") instanceof EdmColor);

		assertEquals("Motor control", d.getTitle());
		assertTrue(d.getAttribute("title") instanceof EdmString);
		assertEquals(true, d.isShowGrid());
		assertTrue(d.getAttribute("showGrid") instanceof EdmBoolean);
		assertEquals(false, d.isSnapToGrid());
		assertTrue(d.getAttribute("snapToGrid") instanceof EdmBoolean);

		assertEquals(true, d.isDisableScroll());
		assertTrue(d.getAttribute("disableScroll") instanceof EdmBoolean);
		
		assertEquals(5, d.getGridSize());
		assertTrue(d.getAttribute("gridSize") instanceof EdmInt);
		assertEquals(true, d.isDisableScroll());
		assertTrue(d.getAttribute("showGrid") instanceof EdmBoolean);

		assertEquals(5, d.getWidgets().size());

		Edm_activeRectangleClass r = (Edm_activeRectangleClass)d.getWidgets().get(0);

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

		r = (Edm_activeRectangleClass)d.getWidgets().get(1);

		assertEquals(4, r.getMajor());
		assertTrue(r.getAttribute("major") instanceof EdmInt);
		assertEquals(0, r.getMinor());
		assertTrue(r.getAttribute("minor") instanceof EdmInt);
		assertEquals(0, r.getRelease());
		assertTrue(r.getAttribute("release") instanceof EdmInt);
		assertEquals(120, r.getX());
		assertTrue(r.getAttribute("x") instanceof EdmInt);
		assertEquals(45, r.getY());
		assertTrue(r.getAttribute("y") instanceof EdmInt);
		assertEquals(155, r.getW());
		assertTrue(r.getAttribute("w") instanceof EdmInt);
		assertEquals(42, r.getH());
		assertTrue(r.getAttribute("h") instanceof EdmInt);

		EdmComparator.isColorEqual(r.getLineColor(), new EdmColor(7));
		assertTrue(r.getAttribute("lineColor") instanceof EdmColor);
		EdmComparator.isColorEqual(r.getFillColor(), new EdmColor(0));
		assertTrue(r.getAttribute("fillColor") instanceof EdmColor);


		r = (Edm_activeRectangleClass)d.getWidgets().get(2);

		assertEquals(4, r.getMajor());
		assertTrue(r.getAttribute("major") instanceof EdmInt);
		assertEquals(0, r.getMinor());
		assertTrue(r.getAttribute("minor") instanceof EdmInt);
		assertEquals(0, r.getRelease());
		assertTrue(r.getAttribute("release") instanceof EdmInt);
		assertEquals(4, r.getX());
		assertTrue(r.getAttribute("x") instanceof EdmInt);
		assertEquals(94, r.getY());
		assertTrue(r.getAttribute("y") instanceof EdmInt);
		assertEquals(271, r.getW());
		assertTrue(r.getAttribute("w") instanceof EdmInt);
		assertEquals(80, r.getH());
		assertTrue(r.getAttribute("h") instanceof EdmInt);

		EdmComparator.isColorEqual(r.getLineColor(), new EdmColor(7));
		assertTrue(r.getAttribute("lineColor") instanceof EdmColor);
		EdmComparator.isColorEqual(r.getFillColor(), new EdmColor(0));
		assertTrue(r.getAttribute("fillColor") instanceof EdmColor);


		Edm_activeXTextClass t = (Edm_activeXTextClass)d.getWidgets().get(3);

		assertEquals(4, t.getMajor());
		assertTrue(t.getAttribute("major") instanceof EdmInt);
		assertEquals(1, t.getMinor());
		assertTrue(t.getAttribute("minor") instanceof EdmInt);
		assertEquals(0, t.getRelease());
		assertTrue(t.getAttribute("release") instanceof EdmInt);
		assertEquals(123, t.getX());
		assertTrue(t.getAttribute("x") instanceof EdmInt);
		assertEquals(50, t.getY());
		assertTrue(t.getAttribute("y") instanceof EdmInt);
		assertEquals(42, t.getW());
		assertTrue(t.getAttribute("w") instanceof EdmInt);
		assertEquals(13, t.getH());
		assertTrue(t.getAttribute("h") instanceof EdmInt);

		EdmComparator.isFontEqual("helvetica-bold-r-12.0", t.getFont());
		assertTrue(t.getAttribute("font") instanceof EdmFont);

		EdmComparator.isColorEqual(new EdmColor(10), t.getFgColor());
		assertTrue(t.getAttribute("fgColor") instanceof EdmColor);
		EdmComparator.isColorEqual(new EdmColor(3), t.getBgColor());
		assertTrue(t.getAttribute("bgColor") instanceof EdmColor);

		assertEquals("At low", t.getValue().get());
		assertTrue(t.getAttribute("value") instanceof EdmMultilineText);
		assertEquals(true, t.isAutoSize());
		assertTrue(t.getAttribute("autoSize") instanceof EdmBoolean);
		
		assertTrue(d.getWidgets().get(4) instanceof Edm_activeGroupClass);
		{
			t = (Edm_activeXTextClass)d.getWidgets().get(4).getSubEntity(0);

			assertEquals(4, t.getMajor());
			assertTrue(t.getAttribute("major") instanceof EdmInt);
			assertEquals(1, t.getMinor());
			assertTrue(t.getAttribute("minor") instanceof EdmInt);
			assertEquals(0, t.getRelease());
			assertTrue(t.getAttribute("release") instanceof EdmInt);
			assertEquals(26, t.getX());
			assertTrue(t.getAttribute("x") instanceof EdmInt);
			assertEquals(120, t.getY());
			assertTrue(t.getAttribute("y") instanceof EdmInt);
			assertEquals(35, t.getW());
			assertTrue(t.getAttribute("w") instanceof EdmInt);
			assertEquals(13, t.getH());
			assertTrue(t.getAttribute("h") instanceof EdmInt);

			EdmComparator.isFontEqual("helvetica-bold-r-12.0", t.getFont());
			assertTrue(t.getAttribute("font") instanceof EdmFont);

			EdmComparator.isColorEqual(new EdmColor(10), t.getFgColor());
			assertTrue(t.getAttribute("fgColor") instanceof EdmColor);
			EdmComparator.isColorEqual(new EdmColor(3), t.getBgColor());
			assertTrue(t.getAttribute("bgColor") instanceof EdmColor);

			assertEquals("Hello\rMulti-line\rWorld", t.getValue().toString());
			assertTrue(t.getAttribute("value") instanceof EdmMultilineText);
			assertEquals(true, t.isAutoSize());
			assertTrue(t.getAttribute("autoSize") instanceof EdmBoolean);

		}
		
	}

	public void testRobustness() throws EdmException {

		System.setProperty("edm2xml.robustParsing", "true");
		System.setProperty("edm2xml.colorsFile", "src/test/resources/colors.list");

		String edlFile = "src/test/resources/EDM_error01.edl";
		EdmModel.getInstance();
		EdmDisplay d = EdmModel.getDisplay(edlFile);

		assertEquals(4, d.getMajor());
		assertTrue(d.getAttribute("major") instanceof EdmInt);
		assertEquals(0, d.getMinor());
		assertTrue(d.getAttribute("minor") instanceof EdmInt);
		assertEquals(1, d.getRelease());
		assertTrue(d.getAttribute("release") instanceof EdmInt);
		assertEquals(614, d.getX());
		assertTrue(d.getAttribute("x") instanceof EdmInt);
		assertEquals(278, d.getY());
		assertTrue(d.getAttribute("y") instanceof EdmInt);
		assertEquals(280, d.getW());
		assertTrue(d.getAttribute("w") instanceof EdmInt);
		assertEquals(177, d.getH());
		assertTrue(d.getAttribute("h") instanceof EdmInt);
		EdmComparator.isFontEqual("helvetica-bold-r-14.0", d.getFont());
		assertTrue(d.getAttribute("font") instanceof EdmFont);
		EdmComparator.isFontEqual("helvetica-bold-r-14.0", d.getCtlFont());
		assertTrue(d.getAttribute("ctlFont") instanceof EdmFont);
		EdmComparator.isFontEqual("helvetica-bold-r-14.0", d.getBtnFont());
		assertTrue(d.getAttribute("btnFont") instanceof EdmFont);
		EdmComparator.isColorEqual(d.getFgColor(), new EdmColor(14));
		assertTrue(d.getAttribute("fgColor") instanceof EdmColor);
		EdmComparator.isColorEqual(d.getBgColor(), new EdmColor(3));
		assertTrue(d.getAttribute("bgColor") instanceof EdmColor);
		EdmComparator.isColorEqual(d.getTextColor(), new EdmColor(14));
		assertTrue(d.getAttribute("textColor") instanceof EdmColor);
		EdmComparator.isColorEqual(new EdmColor(new EdmAttribute("rgb 256 512 65535"), true), d.getCtlFgColor1());
		assertTrue(d.getAttribute("ctlFgColor1") instanceof EdmColor);
		EdmComparator.isColorEqual(d.getCtlFgColor2(), new EdmColor(30));
		assertTrue(d.getAttribute("ctlFgColor2") instanceof EdmColor);
		EdmComparator.isColorEqual(d.getCtlBgColor1(), new EdmColor(3));
		assertTrue(d.getAttribute("ctlBgColor1") instanceof EdmColor);
		EdmComparator.isColorEqual(d.getCtlBgColor2(), new EdmColor(3));
		assertTrue(d.getAttribute("ctlBgColor2") instanceof EdmColor);
		EdmComparator.isColorEqual(d.getTopShadowColor(), new EdmColor(1));
		assertTrue(d.getAttribute("topShadowColor") instanceof EdmColor);
		EdmComparator.isColorEqual(d.getBotShadowColor(), new EdmColor(11));
		assertTrue(d.getAttribute("botShadowColor") instanceof EdmColor);
		assertEquals("Motor control", d.getTitle());
		assertTrue(d.getAttribute("title") instanceof EdmString);
		assertEquals(true, d.isShowGrid());
		assertTrue(d.getAttribute("showGrid") instanceof EdmBoolean);
		assertEquals(5, d.getGridSize());
		assertTrue(d.getAttribute("gridSize") instanceof EdmInt);
		assertEquals(true, d.isDisableScroll());
		assertTrue(d.getAttribute("showGrid") instanceof EdmBoolean);
		
		assertEquals(4, d.getWidgets().size());

		
		Edm_activeRectangleClass r = (Edm_activeRectangleClass)d.getWidgets().get(1);
		assertEquals(4, r.getMajor());
		assertTrue(r.getAttribute("major") instanceof EdmInt);
		assertEquals(0, r.getMinor());
		assertTrue(r.getAttribute("minor") instanceof EdmInt);
		assertEquals(0, r.getRelease());
		assertTrue(r.getAttribute("release") instanceof EdmInt);
		assertEquals(4, r.getX());
		assertTrue(r.getAttribute("x") instanceof EdmInt);
		assertEquals(94, r.getY());
		assertTrue(r.getAttribute("y") instanceof EdmInt);
		assertEquals(271, r.getW());
		assertTrue(r.getAttribute("w") instanceof EdmInt);
		assertEquals(80, r.getH());
		assertTrue(r.getAttribute("h") instanceof EdmInt);
		EdmComparator.isColorEqual(r.getLineColor(), new EdmColor(7));
		assertTrue(r.getAttribute("lineColor") instanceof EdmColor);
		EdmComparator.isColorEqual(r.getFillColor(), new EdmColor(0));
		assertTrue(r.getAttribute("fillColor") instanceof EdmColor);

		Edm_activeXTextClass t = (Edm_activeXTextClass)d.getWidgets().get(2);
		assertEquals(4, t.getMajor());
		assertTrue(t.getAttribute("major") instanceof EdmInt);
		assertEquals(1, t.getMinor());
		assertTrue(t.getAttribute("minor") instanceof EdmInt);
		assertEquals(0, t.getRelease());
		assertTrue(t.getAttribute("release") instanceof EdmInt);
		assertEquals(123, t.getX());
		assertTrue(t.getAttribute("x") instanceof EdmInt);
		assertEquals(50, t.getY());
		assertTrue(t.getAttribute("y") instanceof EdmInt);
		assertEquals(42, t.getW());
		assertTrue(t.getAttribute("w") instanceof EdmInt);
		assertEquals(13, t.getH());
		assertTrue(t.getAttribute("h") instanceof EdmInt);
		EdmComparator.isFontEqual("helvetica-bold-r-12.0", t.getFont());
		assertTrue(t.getAttribute("font") instanceof EdmFont);
		EdmComparator.isColorEqual(new EdmColor(10), t.getFgColor());
		assertTrue(t.getAttribute("fgColor") instanceof EdmColor);
		EdmComparator.isColorEqual(new EdmColor(3), t.getBgColor());
		assertTrue(t.getAttribute("bgColor") instanceof EdmColor);
		assertEquals("At low", t.getValue().get());
		assertTrue(t.getAttribute("value") instanceof EdmMultilineText);
		assertEquals(true, t.isAutoSize());
		assertTrue(t.getAttribute("autoSize") instanceof EdmBoolean);

		assertTrue(d.getWidgets().get(3) instanceof Edm_activeGroupClass);
		{
			t = (Edm_activeXTextClass)d.getWidgets().get(3).getSubEntity(0);

			assertEquals(4, t.getMajor());
			assertTrue(t.getAttribute("major") instanceof EdmInt);
			assertEquals(1, t.getMinor());
			assertTrue(t.getAttribute("minor") instanceof EdmInt);
			assertEquals(0, t.getRelease());
			assertTrue(t.getAttribute("release") instanceof EdmInt);
			assertEquals(26, t.getX());
			assertTrue(t.getAttribute("x") instanceof EdmInt);
			assertEquals(120, t.getY());
			assertTrue(t.getAttribute("y") instanceof EdmInt);
			assertEquals(35, t.getW());
			assertTrue(t.getAttribute("w") instanceof EdmInt);
			assertEquals(13, t.getH());
			assertTrue(t.getAttribute("h") instanceof EdmInt);

			EdmComparator.isFontEqual("helvetica-bold-r-12.0", t.getFont());
			assertTrue(t.getAttribute("font") instanceof EdmFont);

			EdmComparator.isColorEqual(new EdmColor(10), t.getFgColor());
			assertTrue(t.getAttribute("fgColor") instanceof EdmColor);
			EdmComparator.isColorEqual(new EdmColor(3), t.getBgColor());
			assertTrue(t.getAttribute("bgColor") instanceof EdmColor);

			assertEquals("Homed", t.getValue().get());
			assertTrue(t.getAttribute("value") instanceof EdmMultilineText);
			assertEquals(true, t.isAutoSize());
			assertTrue(t.getAttribute("autoSize") instanceof EdmBoolean);
		}
	}
}
