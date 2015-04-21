/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter.model;

import junit.framework.TestCase;

public class Edm_activeGroupClassTest extends TestCase {

	public void testEdm_activeGroupClass() throws EdmException {

		// Prepare a model.
		System.setProperty("edm2xml.robustParsing", "false");
		System.setProperty("edm2xml.colorsFile", "src/test/resources/colors.list");

		String edlFile = "src/test/resources/group_example_spec.edl";
		EdmModel.getInstance();
		EdmDisplay d = EdmModel.getDisplay(edlFile);


		assertEquals("subentity_count", 2, d.getWidgets().size());

		// Get objects in various places in the group hierarchy.
		EdmEntity entity = d.getSubEntity(0);
		assertTrue(entity instanceof Edm_activeGroupClass);
		Edm_activeGroupClass subGroup0 = (Edm_activeGroupClass)entity;

		assertEquals(4, subGroup0.getMajor());
		assertTrue(subGroup0.getAttribute("major") instanceof EdmInt);
		assertEquals(0, subGroup0.getMinor());
		assertTrue(subGroup0.getAttribute("minor") instanceof EdmInt);
		assertEquals(0, subGroup0.getRelease());
		assertTrue(subGroup0.getAttribute("release") instanceof EdmInt);
		assertEquals(8, subGroup0.getX());
		assertTrue(subGroup0.getAttribute("x") instanceof EdmInt);
		assertEquals(120, subGroup0.getY());
		assertTrue(subGroup0.getAttribute("y") instanceof EdmInt);
		assertEquals(52, subGroup0.getW());
		assertTrue(subGroup0.getAttribute("w") instanceof EdmInt);
		assertEquals(11, subGroup0.getH());
		assertTrue(subGroup0.getAttribute("h") instanceof EdmInt);

		assertEquals("$(S)_LLRF:FCM$(N):cavAmpCheck.SEVR", subGroup0.getVisPv());
		assertTrue(subGroup0.getAttribute("visPv") instanceof EdmString);
		assertEquals(-1.1, subGroup0.getVisMin());
		assertTrue(subGroup0.getAttribute("visMin") instanceof EdmDouble);
		assertEquals(10.78, subGroup0.getVisMax());
		assertTrue(subGroup0.getAttribute("visMax") instanceof EdmDouble);
		
		
		assertEquals(3, subGroup0.getSubEntityCount());
		assertEquals(2, subGroup0.getWidgets().size());
		{
			entity = subGroup0.getSubEntity(0);
			assertTrue(entity instanceof Edm_activeXTextClass);
			Edm_activeXTextClass t = (Edm_activeXTextClass)entity;

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

			entity = subGroup0.getSubEntity(1);
			assertTrue(entity instanceof Edm_activeGroupClass);
			Edm_activeGroupClass subGroup01 = (Edm_activeGroupClass)entity;

			assertEquals(4, subGroup01.getMajor());
			assertTrue(subGroup01.getAttribute("major") instanceof EdmInt);
			assertEquals(0, subGroup01.getMinor());
			assertTrue(subGroup01.getAttribute("minor") instanceof EdmInt);
			assertEquals(0, subGroup01.getRelease());
			assertTrue(subGroup01.getAttribute("release") instanceof EdmInt);
			assertEquals(108, subGroup01.getX());
			assertTrue(subGroup01.getAttribute("x") instanceof EdmInt);
			assertEquals(220, subGroup01.getY());
			assertTrue(subGroup01.getAttribute("y") instanceof EdmInt);
			assertEquals(152, subGroup01.getW());
			assertTrue(subGroup01.getAttribute("w") instanceof EdmInt);
			assertEquals(111, subGroup01.getH());
			assertTrue(subGroup01.getAttribute("h") instanceof EdmInt);

			assertEquals(4, subGroup01.getSubEntityCount());
			assertEquals(3, subGroup01.getWidgets().size());
			{
				entity = subGroup01.getSubEntity(0);
				assertTrue(entity instanceof Edm_activeXTextClass);
				t = (Edm_activeXTextClass)entity;

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

				entity = subGroup01.getSubEntity(1);
				assertTrue(entity instanceof Edm_activeGroupClass);
				Edm_activeGroupClass subGroup011 = (Edm_activeGroupClass)entity;

				assertEquals(4, subGroup011.getMajor());
				assertTrue(subGroup011.getAttribute("major") instanceof EdmInt);
				assertEquals(0, subGroup011.getMinor());
				assertTrue(subGroup011.getAttribute("minor") instanceof EdmInt);
				assertEquals(0, subGroup011.getRelease());
				assertTrue(subGroup011.getAttribute("release") instanceof EdmInt);
				assertEquals(208, subGroup011.getX());
				assertTrue(subGroup011.getAttribute("x") instanceof EdmInt);
				assertEquals(320, subGroup011.getY());
				assertTrue(subGroup011.getAttribute("y") instanceof EdmInt);
				assertEquals(252, subGroup011.getW());
				assertTrue(subGroup011.getAttribute("w") instanceof EdmInt);
				assertEquals(211, subGroup011.getH());
				assertTrue(subGroup011.getAttribute("h") instanceof EdmInt);

				assertEquals(2, subGroup011.getSubEntityCount());
				assertEquals(1, subGroup011.getWidgets().size());
				{
					entity = subGroup011.getSubEntity(0);
					assertTrue(entity instanceof Edm_activeXTextClass);
					t = (Edm_activeXTextClass)entity;

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

					entity = subGroup011.getSubEntity(1);
					assertEquals("ByteClass", entity.getType());
					assertEquals("4", entity.getAttribute("major").toString());
					assertEquals("0", entity.getAttribute("minor").toString());
					assertEquals("0", entity.getAttribute("release").toString());
					assertEquals("8", entity.getAttribute("x").toString());
					assertEquals("121", entity.getAttribute("y").toString());
					assertEquals("11", entity.getAttribute("w").toString());
					assertEquals("10", entity.getAttribute("h").toString());
					assertEquals("$(DEVICE):$(MRN)_HOCPL_STS", entity.getAttribute("controlPv").toString());
					assertEquals("index 14", entity.getAttribute("lineColor").toString());
					assertEquals("index 15", entity.getAttribute("onColor").toString());
					assertEquals("index 8", entity.getAttribute("offColor").toString());
					assertEquals("2", entity.getAttribute("lineWidth").toString());
					assertEquals("1", entity.getAttribute("numBits").toString());
				}

				entity = subGroup01.getSubEntity(2);
				assertTrue(entity instanceof Edm_activeGroupClass);
				Edm_activeGroupClass subGroup012 = (Edm_activeGroupClass)entity;

				assertEquals(4, subGroup012.getMajor());
				assertTrue(subGroup012.getAttribute("major") instanceof EdmInt);
				assertEquals(0, subGroup012.getMinor());
				assertTrue(subGroup012.getAttribute("minor") instanceof EdmInt);
				assertEquals(0, subGroup012.getRelease());
				assertTrue(subGroup012.getAttribute("release") instanceof EdmInt);
				assertEquals(308, subGroup012.getX());
				assertTrue(subGroup012.getAttribute("x") instanceof EdmInt);
				assertEquals(420, subGroup012.getY());
				assertTrue(subGroup012.getAttribute("y") instanceof EdmInt);
				assertEquals(352, subGroup012.getW());
				assertTrue(subGroup012.getAttribute("w") instanceof EdmInt);
				assertEquals(311, subGroup012.getH());
				assertTrue(subGroup012.getAttribute("h") instanceof EdmInt);

				assertEquals(2, subGroup012.getSubEntityCount());
				assertEquals(1, subGroup012.getWidgets().size());
				{
					entity = subGroup012.getSubEntity(0);
					assertTrue(entity instanceof Edm_activeXTextClass);
					t = (Edm_activeXTextClass)entity;

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

					entity = subGroup012.getSubEntity(1);
					assertEquals("ByteClass", entity.getType());
					assertEquals("4", entity.getAttribute("major").toString());
					assertEquals("0", entity.getAttribute("minor").toString());
					assertEquals("0", entity.getAttribute("release").toString());
					assertEquals("8", entity.getAttribute("x").toString());
					assertEquals("121", entity.getAttribute("y").toString());
					assertEquals("11", entity.getAttribute("w").toString());
					assertEquals("10", entity.getAttribute("h").toString());
					assertEquals("$(DEVICE):$(MRN)_HOCPL_STS", entity.getAttribute("controlPv").toString());
					assertEquals("index 14", entity.getAttribute("lineColor").toString());
					assertEquals("index 15", entity.getAttribute("onColor").toString());
					assertEquals("index 8", entity.getAttribute("offColor").toString());
					assertEquals("2", entity.getAttribute("lineWidth").toString());
					assertEquals("1", entity.getAttribute("numBits").toString());
				}
				
				entity = subGroup01.getSubEntity(3);
				assertEquals("ByteClass", entity.getType());
				assertEquals("4", entity.getAttribute("major").toString());
				assertEquals("0", entity.getAttribute("minor").toString());
				assertEquals("0", entity.getAttribute("release").toString());
				assertEquals("8", entity.getAttribute("x").toString());
				assertEquals("121", entity.getAttribute("y").toString());
				assertEquals("11", entity.getAttribute("w").toString());
				assertEquals("10", entity.getAttribute("h").toString());
				assertEquals("$(DEVICE):$(MRN)_HOCPL_STS", entity.getAttribute("controlPv").toString());
				assertEquals("index 14", entity.getAttribute("lineColor").toString());
				assertEquals("index 15", entity.getAttribute("onColor").toString());
				assertEquals("index 8", entity.getAttribute("offColor").toString());
				assertEquals("2", entity.getAttribute("lineWidth").toString());
				assertEquals("1", entity.getAttribute("numBits").toString());
			}

			entity = subGroup0.getSubEntity(2);
			assertEquals("ByteClass", entity.getType());
			assertEquals("4", entity.getAttribute("major").toString());
			assertEquals("0", entity.getAttribute("minor").toString());
			assertEquals("0", entity.getAttribute("release").toString());
			assertEquals("8", entity.getAttribute("x").toString());
			assertEquals("121", entity.getAttribute("y").toString());
			assertEquals("11", entity.getAttribute("w").toString());
			assertEquals("10", entity.getAttribute("h").toString());
			assertEquals("$(DEVICE):$(MRN)_HOCPL_STS", entity.getAttribute("controlPv").toString());
			assertEquals("index 14", entity.getAttribute("lineColor").toString());
			assertEquals("index 15", entity.getAttribute("onColor").toString());
			assertEquals("index 8", entity.getAttribute("offColor").toString());
			assertEquals("2", entity.getAttribute("lineWidth").toString());
			assertEquals("1", entity.getAttribute("numBits").toString());
		}

		entity = d.getSubEntity(1);
		assertTrue(entity instanceof Edm_activeGroupClass);
		Edm_activeGroupClass subGroup1 = (Edm_activeGroupClass)entity;

		assertEquals(4, subGroup1.getMajor());
		assertTrue(subGroup1.getAttribute("major") instanceof EdmInt);
		assertEquals(0, subGroup1.getMinor());
		assertTrue(subGroup1.getAttribute("minor") instanceof EdmInt);
		assertEquals(0, subGroup1.getRelease());
		assertTrue(subGroup1.getAttribute("release") instanceof EdmInt);
		assertEquals(408, subGroup1.getX());
		assertTrue(subGroup1.getAttribute("x") instanceof EdmInt);
		assertEquals(520, subGroup1.getY());
		assertTrue(subGroup1.getAttribute("y") instanceof EdmInt);
		assertEquals(452, subGroup1.getW());
		assertTrue(subGroup1.getAttribute("w") instanceof EdmInt);
		assertEquals(411, subGroup1.getH());
		assertTrue(subGroup1.getAttribute("h") instanceof EdmInt);

		assertEquals(2, subGroup1.getSubEntityCount());
		assertEquals(1, subGroup1.getWidgets().size());
		{
			entity = subGroup1.getSubEntity(0);
			assertTrue(entity instanceof Edm_activeXTextClass);
			Edm_activeXTextClass t = (Edm_activeXTextClass)entity;

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
			
			entity = subGroup1.getSubEntity(1);
			assertEquals("ByteClass", entity.getType());
			assertEquals("4", entity.getAttribute("major").toString());
			assertEquals("0", entity.getAttribute("minor").toString());
			assertEquals("0", entity.getAttribute("release").toString());
			assertEquals("8", entity.getAttribute("x").toString());
			assertEquals("121", entity.getAttribute("y").toString());
			assertEquals("11", entity.getAttribute("w").toString());
			assertEquals("10", entity.getAttribute("h").toString());
			assertEquals("$(DEVICE):$(MRN)_HOCPL_STS", entity.getAttribute("controlPv").toString());
			assertEquals("index 14", entity.getAttribute("lineColor").toString());
			assertEquals("index 15", entity.getAttribute("onColor").toString());
			assertEquals("index 8", entity.getAttribute("offColor").toString());
			assertEquals("2", entity.getAttribute("lineWidth").toString());
			assertEquals("1", entity.getAttribute("numBits").toString());
		}
	}
}
