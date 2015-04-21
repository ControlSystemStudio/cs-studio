/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter.parser;

import junit.framework.TestCase;

import org.csstudio.opibuilder.converter.model.EdmEntity;
import org.csstudio.opibuilder.converter.model.EdmException;


public class EdmDisplayParserTest extends TestCase {

	private String edlFile = "src/test/resources/EDMDisplayParser_example.edl"; 
	private String braceFile = "src/test/resources/brace_example.edl";
	private String groupFile = "src/test/resources/group_example.edl";
	private String braceErrorFile = "src/test/resources/brace_error.edl";
	private String groupErrorFile = "src/test/resources/group_error.edl";
	private String error1 = "src/test/resources/EDM_error01.edl";
	
	public void testEdmDisplayParser() throws EdmException {	

		EdmDisplayParser p = new EdmDisplayParser(edlFile);

		assertEquals("attribute_count", 23, p.getRoot().getAttributeCount());
		assertEquals("4", p.getRoot().getAttribute("major").getValue(0));
		assertEquals("0", p.getRoot().getAttribute("minor").getValue(0));
		assertEquals("1", p.getRoot().getAttribute("release").getValue(0));
		assertEquals("614", p.getRoot().getAttribute("x").getValue(0));
		assertEquals("278", p.getRoot().getAttribute("y").getValue(0));
		assertEquals("280", p.getRoot().getAttribute("w").getValue(0));
		assertEquals("177", p.getRoot().getAttribute("h").getValue(0));
		assertEquals("helvetica-bold-r-14.0", p.getRoot().getAttribute("font").getValue(0));
		assertEquals("helvetica-bold-r-14.0", p.getRoot().getAttribute("ctlFont").getValue(0));
		assertEquals("helvetica-bold-r-14.0", p.getRoot().getAttribute("btnFont").getValue(0));
		assertEquals("index 14", p.getRoot().getAttribute("fgColor").getValue(0));
		assertEquals("index 3", p.getRoot().getAttribute("bgColor").getValue(0));
		assertEquals("index 14", p.getRoot().getAttribute("textColor").getValue(0));
		assertEquals("rgb 256 512 65535", p.getRoot().getAttribute("ctlFgColor1").getValue(0));
		assertEquals("index 30", p.getRoot().getAttribute("ctlFgColor2").getValue(0));
		assertEquals("index 3", p.getRoot().getAttribute("ctlBgColor1").getValue(0));
		assertEquals("index 3", p.getRoot().getAttribute("ctlBgColor2").getValue(0));
		assertEquals("index 1", p.getRoot().getAttribute("topShadowColor").getValue(0));
		assertEquals("index 11", p.getRoot().getAttribute("botShadowColor").getValue(0));
		assertEquals("Motor control", p.getRoot().getAttribute("title").getValue(0));
		assertNotNull(p.getRoot().getAttribute("showGrid"));
		assertEquals("5", p.getRoot().getAttribute("gridSize").getValue(0));
		assertNotNull(p.getRoot().getAttribute("disableScroll"));

		assertEquals("subentity_count", 8, p.getRoot().getSubEntityCount());

		EdmEntity subE = p.getRoot().getSubEntity(0);
		assertEquals("activeRectangleClass", subE.getType());
		assertEquals("attribute_count", 16, subE.getAttributeCount());
		assertEquals("4", subE.getAttribute("major").getValue(0));
		assertEquals("0", subE.getAttribute("minor").getValue(0));
		assertEquals("0", subE.getAttribute("release").getValue(0));
		assertEquals("4", subE.getAttribute("x").getValue(0));
		assertEquals("45", subE.getAttribute("y").getValue(0));
		assertEquals("111", subE.getAttribute("w").getValue(0));
		assertEquals("42", subE.getAttribute("h").getValue(0));
		assertEquals("index 7", subE.getAttribute("lineColor").getValue(0));
		assertEquals("index 0", subE.getAttribute("fillColor").getValue(0));

		subE = p.getRoot().getSubEntity(1);
		assertEquals("activeRectangleClass", subE.getType());
		assertEquals("attribute_count", 9, subE.getAttributeCount());
		assertEquals("4", subE.getAttribute("major").getValue(0));
		assertEquals("0", subE.getAttribute("minor").getValue(0));
		assertEquals("0", subE.getAttribute("release").getValue(0));
		assertEquals("120", subE.getAttribute("x").getValue(0));
		assertEquals("45", subE.getAttribute("y").getValue(0));
		assertEquals("155", subE.getAttribute("w").getValue(0));
		assertEquals("42", subE.getAttribute("h").getValue(0));
		assertEquals("index 7", subE.getAttribute("lineColor").getValue(0));
		assertEquals("index 0", subE.getAttribute("fillColor").getValue(0));

		subE = p.getRoot().getSubEntity(2);
		assertEquals("activeRectangleClass", subE.getType());
		assertEquals("attribute_count", 9, subE.getAttributeCount());
		assertEquals("4", subE.getAttribute("major").getValue(0));
		assertEquals("0", subE.getAttribute("minor").getValue(0));
		assertEquals("0", subE.getAttribute("release").getValue(0));
		assertEquals("4", subE.getAttribute("x").getValue(0));
		assertEquals("94", subE.getAttribute("y").getValue(0));
		assertEquals("271", subE.getAttribute("w").getValue(0));
		assertEquals("80", subE.getAttribute("h").getValue(0));
		assertEquals("index 7", subE.getAttribute("lineColor").getValue(0));
		assertEquals("index 0", subE.getAttribute("fillColor").getValue(0));

		subE = p.getRoot().getSubEntity(3);
		assertEquals("activeXTextDspClass:noedit", subE.getType());
		assertEquals("attribute_count", 20, subE.getAttributeCount());
		assertEquals("4", subE.getAttribute("major").getValue(0));
		assertEquals("0", subE.getAttribute("minor").getValue(0));
		assertEquals("0", subE.getAttribute("release").getValue(0));
		assertEquals("159", subE.getAttribute("x").getValue(0));
		assertEquals("64", subE.getAttribute("y").getValue(0));
		assertEquals("78", subE.getAttribute("w").getValue(0));
		assertEquals("18", subE.getAttribute("h").getValue(0));
		assertEquals("$(DEVICE):$(MRN)_MON", subE.getAttribute("controlPv").getValue(0));
		assertEquals("decimal", subE.getAttribute("format").getValue(0));
		assertEquals("helvetica-bold-r-14.0", subE.getAttribute("font").getValue(0));
		assertEquals("center", subE.getAttribute("fontAlign").getValue(0));
		assertEquals("index 16", subE.getAttribute("fgColor").getValue(0));
		assertNotNull(subE.getAttribute("fgAlarm"));
		assertEquals("index 10", subE.getAttribute("bgColor").getValue(0));
		assertNotNull(subE.getAttribute("limitsFromDb"));
		assertEquals("index 0", subE.getAttribute("nullColor").getValue(0));
		assertNotNull(subE.getAttribute("fastUpdate"));
		assertNotNull(subE.getAttribute("showUnits"));
		assertNotNull(subE.getAttribute("newPos"));
		assertEquals("monitors", subE.getAttribute("objType").getValue(0));

		subE = p.getRoot().getSubEntity(4);
		assertEquals("activeMessageButtonClass", subE.getType());
		assertEquals("attribute_count", 19, subE.getAttributeCount());
		assertEquals("4", subE.getAttribute("major").getValue(0));
		assertEquals("0", subE.getAttribute("minor").getValue(0));
		assertEquals("0", subE.getAttribute("release").getValue(0));
		assertEquals("8", subE.getAttribute("x").getValue(0));
		assertEquals("54", subE.getAttribute("y").getValue(0));
		assertEquals("14", subE.getAttribute("w").getValue(0));
		assertEquals("14", subE.getAttribute("h").getValue(0));
		assertEquals("index 25", subE.getAttribute("fgColor").getValue(0));
		assertEquals("index 4", subE.getAttribute("onColor").getValue(0));
		assertEquals("index 3", subE.getAttribute("offColor").getValue(0));
		assertEquals("index 1", subE.getAttribute("topShadowColor").getValue(0));
		assertEquals("index 11", subE.getAttribute("botShadowColor").getValue(0));
		assertEquals("$(DEVICE):$(MRN)_TWR.PROC", subE.getAttribute("controlPv").getValue(0));
		assertEquals("1", subE.getAttribute("pressValue").getValue(0));
		assertEquals("-", subE.getAttribute("onLabel").getValue(0));
		assertEquals("-", subE.getAttribute("offLabel").getValue(0));
		assertNotNull(subE.getAttribute("3d"));
		assertEquals("helvetica-bold-r-12.0", subE.getAttribute("font").getValue(0));
		assertNotNull(subE.getAttribute("visInvert"));

		subE = p.getRoot().getSubEntity(5);
		assertEquals("activeMessageButtonClass", subE.getType());
		assertEquals("attribute_count", 19, subE.getAttributeCount());
		assertEquals("4", subE.getAttribute("major").getValue(0));
		assertEquals("0", subE.getAttribute("minor").getValue(0));
		assertEquals("0", subE.getAttribute("release").getValue(0));
		assertEquals("97", subE.getAttribute("x").getValue(0));
		assertEquals("54", subE.getAttribute("y").getValue(0));
		assertEquals("14", subE.getAttribute("w").getValue(0));
		assertEquals("14", subE.getAttribute("h").getValue(0));
		assertEquals("index 25", subE.getAttribute("fgColor").getValue(0));
		assertEquals("index 4", subE.getAttribute("onColor").getValue(0));
		assertEquals("index 3", subE.getAttribute("offColor").getValue(0));
		assertEquals("index 1", subE.getAttribute("topShadowColor").getValue(0));
		assertEquals("index 11", subE.getAttribute("botShadowColor").getValue(0));
		assertEquals("$(DEVICE):$(MRN)_TWF.PROC", subE.getAttribute("controlPv").getValue(0));
		assertEquals("1", subE.getAttribute("pressValue").getValue(0));
		assertEquals("+", subE.getAttribute("onLabel").getValue(0));
		assertEquals("+", subE.getAttribute("offLabel").getValue(0));
		assertNotNull(subE.getAttribute("3d"));
		assertEquals("helvetica-bold-r-12.0", subE.getAttribute("font").getValue(0));
		assertNotNull(subE.getAttribute("visInvert"));

		subE = p.getRoot().getSubEntity(6);
		assertEquals("attribute_count", 19, subE.getAttributeCount());
		assertEquals("activeXTextClass", subE.getType());
		assertEquals("4", subE.getAttribute("major").getValue(0));
		assertEquals("1", subE.getAttribute("minor").getValue(0));
		assertEquals("0", subE.getAttribute("release").getValue(0));
		assertEquals("123", subE.getAttribute("x").getValue(0));
		assertEquals("50", subE.getAttribute("y").getValue(0));
		assertEquals("42", subE.getAttribute("w").getValue(0));
		assertEquals("13", subE.getAttribute("h").getValue(0));
		assertEquals("helvetica-bold-r-12.0", subE.getAttribute("font").getValue(0));
		assertEquals("index 10", subE.getAttribute("fgColor").getValue(0));
		assertEquals("index 3", subE.getAttribute("bgColor").getValue(0));
		assertEquals("At low", subE.getAttribute("value").getValue(0));
		assertNotNull(subE.getAttribute("autoSize"));
		assertNotNull(subE.getAttribute("border"));
		assertEquals("2", subE.getAttribute("lineWidth").getValue(0));
		assertNotNull(subE.getAttribute("useDisplayBg"));

		subE = p.getRoot().getSubEntity(7);
		assertEquals("activeGroupClass", subE.getType());
		assertEquals("attribute_count", 7, subE.getAttributeCount());
		assertEquals("4", subE.getAttribute("major").getValue(0));
		assertEquals("0", subE.getAttribute("minor").getValue(0));
		assertEquals("0", subE.getAttribute("release").getValue(0));
		assertEquals("8", subE.getAttribute("x").getValue(0));
		assertEquals("120", subE.getAttribute("y").getValue(0));
		assertEquals("52", subE.getAttribute("w").getValue(0));
		assertEquals("11", subE.getAttribute("h").getValue(0));
		{
			assertEquals(2, subE.getSubEntityCount());

			EdmEntity subE2 = subE.getSubEntity(0);
			assertEquals("activeXTextClass", subE2.getType());
			assertEquals("attribute_count", 12, subE2.getAttributeCount());
			assertEquals("4", subE2.getAttribute("major").getValue(0));
			assertEquals("1", subE2.getAttribute("minor").getValue(0));
			assertEquals("0", subE2.getAttribute("release").getValue(0));
			assertEquals("26", subE2.getAttribute("x").getValue(0));
			assertEquals("120", subE2.getAttribute("y").getValue(0));
			assertEquals("35", subE2.getAttribute("w").getValue(0));
			assertEquals("13", subE2.getAttribute("h").getValue(0));
			assertEquals("helvetica-bold-r-12.0", subE2.getAttribute("font").getValue(0));
			assertEquals("index 10", subE2.getAttribute("fgColor").getValue(0));
			assertEquals("index 3", subE2.getAttribute("bgColor").getValue(0));
			assertEquals("Hello", subE2.getAttribute("value").getValue(0));
			assertEquals("Multi-line", subE2.getAttribute("value").getValue(1));
			assertEquals("World", subE2.getAttribute("value").getValue(2));
			assertNotNull(subE2.getAttribute("autoSize"));

			subE2 = subE.getSubEntity(1);
			assertEquals("ByteClass", subE2.getType());
			assertEquals("attribute_count", 13, subE2.getAttributeCount());
			assertEquals("4", subE2.getAttribute("major").getValue(0));
			assertEquals("0", subE2.getAttribute("minor").getValue(0));
			assertEquals("0", subE2.getAttribute("release").getValue(0));
			assertEquals("8", subE2.getAttribute("x").getValue(0));
			assertEquals("121", subE2.getAttribute("y").getValue(0));
			assertEquals("11", subE2.getAttribute("w").getValue(0));
			assertEquals("10", subE2.getAttribute("h").getValue(0));
			assertEquals("$(DEVICE):$(MRN)_HOCPL_STS", subE2.getAttribute("controlPv").getValue(0));
			assertEquals("index 14", subE2.getAttribute("lineColor").getValue(0));
			assertEquals("index 15", subE2.getAttribute("onColor").getValue(0));
			assertEquals("index 8", subE2.getAttribute("offColor").getValue(0));
			assertEquals("2", subE2.getAttribute("lineWidth").getValue(0));
			assertEquals("1", subE2.getAttribute("numBits").getValue(0));
		}
	}

	public void testBracedValues() throws EdmException {
		EdmDisplayParser p = new EdmDisplayParser(braceFile);

		EdmEntity subE = p.getRoot().getSubEntity(0);
		assertEquals("relatedDisplayClass", subE.getType());
		assertEquals("attribute_count", 17, subE.getAttributeCount());
		assertEquals("4", subE.getAttribute("major").getValue(0));
		assertEquals("0", subE.getAttribute("minor").getValue(0));
		assertEquals("0", subE.getAttribute("release").getValue(0));
		assertEquals("215", subE.getAttribute("x").getValue(0));
		assertEquals("152", subE.getAttribute("y").getValue(0));
		assertEquals("55", subE.getAttribute("w").getValue(0));
		assertEquals("18", subE.getAttribute("h").getValue(0));
		assertEquals("index 44", subE.getAttribute("fgColor").getValue(0));
		assertEquals("index 3", subE.getAttribute("bgColor").getValue(0));
		assertEquals("index 1", subE.getAttribute("topShadowColor").getValue(0));
		assertEquals("index 11", subE.getAttribute("botShadowColor").getValue(0));
		assertEquals("helvetica-bold-r-12.0", subE.getAttribute("font").getValue(0));
		assertEquals("More", subE.getAttribute("buttonLabel").getValue(0));
		assertEquals("4", subE.getAttribute("numPvs").getValue(0));
		assertEquals("1", subE.getAttribute("numDsps").getValue(0));
		assertEquals("0 sin_motorMore.edl", subE.getAttribute("displayFileName").getValue(0));
		assertEquals("0 DEVICE=$(DEVICE),MRN=$(MRN)", subE.getAttribute("symbols").getValue(0));
				
		subE = p.getRoot().getSubEntity(1);
		assertEquals("menuMuxClass", subE.getType());
		assertEquals("4", subE.getAttribute("major").getValue(0));
		assertEquals("0", subE.getAttribute("minor").getValue(0));
		assertEquals("0", subE.getAttribute("release").getValue(0));
		assertEquals("143", subE.getAttribute("x").getValue(0));
		assertEquals("13", subE.getAttribute("y").getValue(0));
		assertEquals("110", subE.getAttribute("w").getValue(0));
		assertEquals("19", subE.getAttribute("h").getValue(0));
		assertEquals("index 25", subE.getAttribute("fgColor").getValue(0));
		assertEquals("index 3", subE.getAttribute("bgColor").getValue(0));
		assertEquals("index 1", subE.getAttribute("topShadowColor").getValue(0));
		assertEquals("index 11", subE.getAttribute("botShadowColor").getValue(0));
		assertEquals("helvetica-bold-r-12.0", subE.getAttribute("font").getValue(0));
		assertEquals("$(INITSTATE)", subE.getAttribute("initialState").getValue(0));
		assertEquals("8", subE.getAttribute("numItems").getValue(0));
		assertEquals("val_count", 8, subE.getAttribute("symbolTag").getValueCount());
		assertEquals("0 MOTOR 1", subE.getAttribute("symbolTag").getValue(0));
		assertEquals("1 MOTOR 2", subE.getAttribute("symbolTag").getValue(1));
		assertEquals("2 MOTOR 3", subE.getAttribute("symbolTag").getValue(2));
		assertEquals("3 MOTOR 4", subE.getAttribute("symbolTag").getValue(3));
		assertEquals("4 MOTOR 5", subE.getAttribute("symbolTag").getValue(4));
		assertEquals("5 MOTOR 6", subE.getAttribute("symbolTag").getValue(5));
		assertEquals("6 MOTOR 7", subE.getAttribute("symbolTag").getValue(6));
		assertEquals("7 MOTOR 8", subE.getAttribute("symbolTag").getValue(7));
		assertEquals("val_count", 8, subE.getAttribute("symbol0").getValueCount());
		assertEquals("0 MRN", subE.getAttribute("symbol0").getValue(0));
		assertEquals("1 MRN", subE.getAttribute("symbol0").getValue(1));
		assertEquals("2 MRN", subE.getAttribute("symbol0").getValue(2));
		assertEquals("3 MRN", subE.getAttribute("symbol0").getValue(3));
		assertEquals("4 MRN", subE.getAttribute("symbol0").getValue(4));
		assertEquals("5 MRN", subE.getAttribute("symbol0").getValue(5));
		assertEquals("6 MRN", subE.getAttribute("symbol0").getValue(6));
		assertEquals("7 MRN", subE.getAttribute("symbol0").getValue(7));
	}
	
	public void testGroupNesting() throws EdmException {
		EdmDisplayParser p = new EdmDisplayParser(groupFile);
		
		assertEquals("subentity_count", 2, p.getRoot().getSubEntityCount());
		
		EdmEntity subE = p.getRoot().getSubEntity(0);
		EdmEntity subE2;
		EdmEntity subE3;
		EdmEntity subE4;
		assertEquals("activeGroupClass", subE.getType());
		assertEquals("attribute_count", 7, subE.getAttributeCount());
		assertEquals("4", subE.getAttribute("major").getValue(0));
		assertEquals("0", subE.getAttribute("minor").getValue(0));
		assertEquals("0", subE.getAttribute("release").getValue(0));
		assertEquals("8", subE.getAttribute("x").getValue(0));
		assertEquals("120", subE.getAttribute("y").getValue(0));
		assertEquals("52", subE.getAttribute("w").getValue(0));
		assertEquals("11", subE.getAttribute("h").getValue(0));
		assertEquals(3, subE.getSubEntityCount());
		{
			subE2 = subE.getSubEntity(0);
			assertEquals("activeXTextClass0", subE2.getType());
			assertEquals("attribute_count", 12, subE2.getAttributeCount());
			assertEquals("4", subE2.getAttribute("major").getValue(0));
			assertEquals("1", subE2.getAttribute("minor").getValue(0));
			assertEquals("0", subE2.getAttribute("release").getValue(0));
			assertEquals("26", subE2.getAttribute("x").getValue(0));
			assertEquals("120", subE2.getAttribute("y").getValue(0));
			assertEquals("35", subE2.getAttribute("w").getValue(0));
			assertEquals("13", subE2.getAttribute("h").getValue(0));
			assertEquals("helvetica-bold-r-12.0", subE2.getAttribute("font").getValue(0));
			assertEquals("index 10", subE2.getAttribute("fgColor").getValue(0));
			assertEquals("index 3", subE2.getAttribute("bgColor").getValue(0));
			assertEquals("Homed", subE2.getAttribute("value").getValue(0));
			assertNotNull(subE2.getAttribute("autoSize"));
			
			subE2 = subE.getSubEntity(1);
			assertEquals("activeGroupClass", subE.getType());
			assertEquals("attribute_count", 7, subE.getAttributeCount());
			assertEquals("4", subE.getAttribute("major").getValue(0));
			assertEquals("0", subE.getAttribute("minor").getValue(0));
			assertEquals("0", subE.getAttribute("release").getValue(0));
			assertEquals("8", subE.getAttribute("x").getValue(0));
			assertEquals("120", subE.getAttribute("y").getValue(0));
			assertEquals("52", subE.getAttribute("w").getValue(0));
			assertEquals("11", subE.getAttribute("h").getValue(0));
			{
				subE3 = subE2.getSubEntity(0);
				assertEquals("activeXTextClass1", subE3.getType());
				assertEquals("attribute_count", 12, subE3.getAttributeCount());
				assertEquals("4", subE3.getAttribute("major").getValue(0));
				assertEquals("1", subE3.getAttribute("minor").getValue(0));
				assertEquals("0", subE3.getAttribute("release").getValue(0));
				assertEquals("26", subE3.getAttribute("x").getValue(0));
				assertEquals("120", subE3.getAttribute("y").getValue(0));
				assertEquals("35", subE3.getAttribute("w").getValue(0));
				assertEquals("13", subE3.getAttribute("h").getValue(0));
				assertEquals("helvetica-bold-r-12.0", subE3.getAttribute("font").getValue(0));
				assertEquals("index 10", subE3.getAttribute("fgColor").getValue(0));
				assertEquals("index 3", subE3.getAttribute("bgColor").getValue(0));
				assertEquals("Homed", subE3.getAttribute("value").getValue(0));
				assertNotNull(subE3.getAttribute("autoSize"));
				
				subE3 = subE2.getSubEntity(1);
				assertEquals("activeGroupClass", subE3.getType());
				assertEquals("attribute_count", 10, subE3.getAttributeCount());
				assertEquals("4", subE3.getAttribute("major").getValue(0));
				assertEquals("0", subE3.getAttribute("minor").getValue(0));
				assertEquals("0", subE3.getAttribute("release").getValue(0));
				assertEquals("8", subE3.getAttribute("x").getValue(0));
				assertEquals("120", subE3.getAttribute("y").getValue(0));
				assertEquals("52", subE3.getAttribute("w").getValue(0));
				assertEquals("11", subE3.getAttribute("h").getValue(0));
				assertEquals("ICS_MPS:Lmp_MM:TgtEcho", subE3.getAttribute("visPv").getValue(0));
				assertEquals("1", subE3.getAttribute("visMin").getValue(0));
				assertEquals("99999", subE3.getAttribute("visMax").getValue(0));
				{
					subE4 = subE3.getSubEntity(0);
					assertEquals("activeXTextClass2X", subE4.getType());
					assertEquals("attribute_count", 12, subE4.getAttributeCount());
					assertEquals("4", subE4.getAttribute("major").getValue(0));
					assertEquals("1", subE4.getAttribute("minor").getValue(0));
					assertEquals("0", subE4.getAttribute("release").getValue(0));
					assertEquals("26", subE4.getAttribute("x").getValue(0));
					assertEquals("120", subE4.getAttribute("y").getValue(0));
					assertEquals("35", subE4.getAttribute("w").getValue(0));
					assertEquals("13", subE4.getAttribute("h").getValue(0));
					assertEquals("helvetica-bold-r-12.0", subE4.getAttribute("font").getValue(0));
					assertEquals("index 10", subE4.getAttribute("fgColor").getValue(0));
					assertEquals("index 3", subE4.getAttribute("bgColor").getValue(0));
					assertEquals("Homed", subE4.getAttribute("value").getValue(0));
					assertNotNull(subE4.getAttribute("autoSize"));
					
					subE4 = subE3.getSubEntity(1);
					assertEquals("ByteClass2X", subE4.getType());
					assertEquals("attribute_count", 13, subE4.getAttributeCount());
					assertEquals("4", subE4.getAttribute("major").getValue(0));
					assertEquals("0", subE4.getAttribute("minor").getValue(0));
					assertEquals("0", subE4.getAttribute("release").getValue(0));
					assertEquals("8", subE4.getAttribute("x").getValue(0));
					assertEquals("121", subE4.getAttribute("y").getValue(0));
					assertEquals("11", subE4.getAttribute("w").getValue(0));
					assertEquals("10", subE4.getAttribute("h").getValue(0));
					assertEquals("$(DEVICE):$(MRN)_HOCPL_STS", subE4.getAttribute("controlPv").getValue(0));
					assertEquals("index 14", subE4.getAttribute("lineColor").getValue(0));
					assertEquals("index 15", subE4.getAttribute("onColor").getValue(0));
					assertEquals("index 8", subE4.getAttribute("offColor").getValue(0));
					assertEquals("2", subE4.getAttribute("lineWidth").getValue(0));
					assertEquals("1", subE4.getAttribute("numBits").getValue(0));
				}
			
				subE3 = subE2.getSubEntity(2);
				assertEquals("activeGroupClass", subE3.getType());
				assertEquals("attribute_count", 7, subE3.getAttributeCount());
				assertEquals("4", subE3.getAttribute("major").getValue(0));
				assertEquals("0", subE3.getAttribute("minor").getValue(0));
				assertEquals("0", subE3.getAttribute("release").getValue(0));
				assertEquals("8", subE3.getAttribute("x").getValue(0));
				assertEquals("120", subE3.getAttribute("y").getValue(0));
				assertEquals("52", subE3.getAttribute("w").getValue(0));
				assertEquals("11", subE3.getAttribute("h").getValue(0));
				{
					subE4 = subE3.getSubEntity(0);
					assertEquals("activeXTextClass2Y", subE4.getType());
					assertEquals("attribute_count", 12, subE4.getAttributeCount());
					assertEquals("4", subE4.getAttribute("major").getValue(0));
					assertEquals("1", subE4.getAttribute("minor").getValue(0));
					assertEquals("0", subE4.getAttribute("release").getValue(0));
					assertEquals("26", subE4.getAttribute("x").getValue(0));
					assertEquals("120", subE4.getAttribute("y").getValue(0));
					assertEquals("35", subE4.getAttribute("w").getValue(0));
					assertEquals("13", subE4.getAttribute("h").getValue(0));
					assertEquals("helvetica-bold-r-12.0", subE4.getAttribute("font").getValue(0));
					assertEquals("index 10", subE4.getAttribute("fgColor").getValue(0));
					assertEquals("index 3", subE4.getAttribute("bgColor").getValue(0));
					assertEquals("Homed", subE4.getAttribute("value").getValue(0));
					assertNotNull(subE4.getAttribute("autoSize"));

					subE4 = subE3.getSubEntity(1);
					assertEquals("ByteClass2Y", subE4.getType());
					assertEquals("attribute_count", 13, subE4.getAttributeCount());
					assertEquals("4", subE4.getAttribute("major").getValue(0));
					assertEquals("0", subE4.getAttribute("minor").getValue(0));
					assertEquals("0", subE4.getAttribute("release").getValue(0));
					assertEquals("8", subE4.getAttribute("x").getValue(0));
					assertEquals("121", subE4.getAttribute("y").getValue(0));
					assertEquals("11", subE4.getAttribute("w").getValue(0));
					assertEquals("10", subE4.getAttribute("h").getValue(0));
					assertEquals("$(DEVICE):$(MRN)_HOCPL_STS", subE4.getAttribute("controlPv").getValue(0));
					assertEquals("index 14", subE4.getAttribute("lineColor").getValue(0));
					assertEquals("index 15", subE4.getAttribute("onColor").getValue(0));
					assertEquals("index 8", subE4.getAttribute("offColor").getValue(0));
					assertEquals("2", subE4.getAttribute("lineWidth").getValue(0));
					assertEquals("1", subE4.getAttribute("numBits").getValue(0));	
				}
				
				subE3 = subE2.getSubEntity(3);
				assertEquals("ByteClass1", subE3.getType());
				assertEquals("attribute_count", 13, subE3.getAttributeCount());
				assertEquals("4", subE3.getAttribute("major").getValue(0));
				assertEquals("0", subE3.getAttribute("minor").getValue(0));
				assertEquals("0", subE3.getAttribute("release").getValue(0));
				assertEquals("8", subE3.getAttribute("x").getValue(0));
				assertEquals("121", subE3.getAttribute("y").getValue(0));
				assertEquals("11", subE3.getAttribute("w").getValue(0));
				assertEquals("10", subE3.getAttribute("h").getValue(0));
				assertEquals("$(DEVICE):$(MRN)_HOCPL_STS", subE3.getAttribute("controlPv").getValue(0));
				assertEquals("index 14", subE3.getAttribute("lineColor").getValue(0));
				assertEquals("index 15", subE3.getAttribute("onColor").getValue(0));
				assertEquals("index 8", subE3.getAttribute("offColor").getValue(0));
				assertEquals("2", subE3.getAttribute("lineWidth").getValue(0));
				assertEquals("1", subE3.getAttribute("numBits").getValue(0));
			}
		
			subE2 = subE.getSubEntity(2);
			assertEquals("ByteClass0", subE2.getType());
			assertEquals("attribute_count", 13, subE2.getAttributeCount());
			assertEquals("4", subE2.getAttribute("major").getValue(0));
			assertEquals("0", subE2.getAttribute("minor").getValue(0));
			assertEquals("0", subE2.getAttribute("release").getValue(0));
			assertEquals("8", subE2.getAttribute("x").getValue(0));
			assertEquals("121", subE2.getAttribute("y").getValue(0));
			assertEquals("11", subE2.getAttribute("w").getValue(0));
			assertEquals("10", subE2.getAttribute("h").getValue(0));
			assertEquals("$(DEVICE):$(MRN)_HOCPL_STS", subE2.getAttribute("controlPv").getValue(0));
			assertEquals("index 14", subE2.getAttribute("lineColor").getValue(0));
			assertEquals("index 15", subE2.getAttribute("onColor").getValue(0));
			assertEquals("index 8", subE2.getAttribute("offColor").getValue(0));
			assertEquals("2", subE2.getAttribute("lineWidth").getValue(0));
			assertEquals("1", subE2.getAttribute("numBits").getValue(0));
		}
			
		subE = p.getRoot().getSubEntity(1);
		assertEquals("activeGroupClass", subE.getType());
		assertEquals("attribute_count", 7, subE.getAttributeCount());
		assertEquals("4", subE.getAttribute("major").getValue(0));
		assertEquals("0", subE.getAttribute("minor").getValue(0));
		assertEquals("0", subE.getAttribute("release").getValue(0));
		assertEquals("8", subE.getAttribute("x").getValue(0));
		assertEquals("120", subE.getAttribute("y").getValue(0));
		assertEquals("52", subE.getAttribute("w").getValue(0));
		assertEquals("11", subE.getAttribute("h").getValue(0));
		{
			assertEquals(2, subE.getSubEntityCount());
			
			subE2 = subE.getSubEntity(0);
			assertEquals("activeXTextClassX", subE2.getType());
			assertEquals("attribute_count", 12, subE2.getAttributeCount());
			assertEquals("4", subE2.getAttribute("major").getValue(0));
			assertEquals("1", subE2.getAttribute("minor").getValue(0));
			assertEquals("0", subE2.getAttribute("release").getValue(0));
			assertEquals("26", subE2.getAttribute("x").getValue(0));
			assertEquals("120", subE2.getAttribute("y").getValue(0));
			assertEquals("35", subE2.getAttribute("w").getValue(0));
			assertEquals("13", subE2.getAttribute("h").getValue(0));
			assertEquals("helvetica-bold-r-12.0", subE2.getAttribute("font").getValue(0));
			assertEquals("index 10", subE2.getAttribute("fgColor").getValue(0));
			assertEquals("index 3", subE2.getAttribute("bgColor").getValue(0));
			assertEquals("Homed", subE2.getAttribute("value").getValue(0));
			assertNotNull(subE2.getAttribute("autoSize"));
		
			subE2 = subE.getSubEntity(1);
			assertEquals("ByteClassX", subE2.getType());
			assertEquals("attribute_count", 13, subE2.getAttributeCount());
			assertEquals("4", subE2.getAttribute("major").getValue(0));
			assertEquals("0", subE2.getAttribute("minor").getValue(0));
			assertEquals("0", subE2.getAttribute("release").getValue(0));
			assertEquals("8", subE2.getAttribute("x").getValue(0));
			assertEquals("121", subE2.getAttribute("y").getValue(0));
			assertEquals("11", subE2.getAttribute("w").getValue(0));
			assertEquals("10", subE2.getAttribute("h").getValue(0));
			assertEquals("$(DEVICE):$(MRN)_HOCPL_STS", subE2.getAttribute("controlPv").getValue(0));
			assertEquals("index 14", subE2.getAttribute("lineColor").getValue(0));
			assertEquals("index 15", subE2.getAttribute("onColor").getValue(0));
			assertEquals("index 8", subE2.getAttribute("offColor").getValue(0));
			assertEquals("2", subE2.getAttribute("lineWidth").getValue(0));
			assertEquals("1", subE2.getAttribute("numBits").getValue(0));
		}
	}
	
	public void testBraceError() {
		try {
			new EdmDisplayParser(braceErrorFile);
		} catch (EdmException e) {
			assertEquals(EdmException.NESTING_ERROR, e.getType());
			assertEquals("NESTING_ERROR exception: Nesting error at attribute: value1 {",
					e.getMessage());
		}
	}

	public void testGroupError() {
		try {
			new EdmDisplayParser(groupErrorFile);
		} catch (EdmException e) {
			assertEquals(EdmException.NESTING_ERROR, e.getType());
			assertTrue(e.getMessage().startsWith("NESTING_ERROR exception: Open and close expressions count do not match"));
		}
	}

	public void testObjectRobustness() throws EdmException {	

		System.setProperty("edm2xml.robustParsing", "true");
		EdmDisplayParser p = new EdmDisplayParser(error1);

		assertEquals("attribute_count", 23, p.getRoot().getAttributeCount());
		assertEquals("4", p.getRoot().getAttribute("major").getValue(0));
		assertEquals("0", p.getRoot().getAttribute("minor").getValue(0));
		assertEquals("1", p.getRoot().getAttribute("release").getValue(0));
		assertEquals("614", p.getRoot().getAttribute("x").getValue(0));
		assertEquals("278", p.getRoot().getAttribute("y").getValue(0));
		assertEquals("280", p.getRoot().getAttribute("w").getValue(0));
		assertEquals("177", p.getRoot().getAttribute("h").getValue(0));
		assertEquals("helvetica-bold-r-14.0", p.getRoot().getAttribute("font").getValue(0));
		assertEquals("helvetica-bold-r-14.0", p.getRoot().getAttribute("ctlFont").getValue(0));
		assertEquals("helvetica-bold-r-14.0", p.getRoot().getAttribute("btnFont").getValue(0));
		assertEquals("index 14", p.getRoot().getAttribute("fgColor").getValue(0));
		assertEquals("index 3", p.getRoot().getAttribute("bgColor").getValue(0));
		assertEquals("index 14", p.getRoot().getAttribute("textColor").getValue(0));
		assertEquals("rgb 256 512 65535", p.getRoot().getAttribute("ctlFgColor1").getValue(0));
		assertEquals("index 30", p.getRoot().getAttribute("ctlFgColor2").getValue(0));
		assertEquals("index 3", p.getRoot().getAttribute("ctlBgColor1").getValue(0));
		assertEquals("index 3", p.getRoot().getAttribute("ctlBgColor2").getValue(0));
		assertEquals("index 1", p.getRoot().getAttribute("topShadowColor").getValue(0));
		assertEquals("index 11", p.getRoot().getAttribute("botShadowColor").getValue(0));
		assertEquals("Motor control", p.getRoot().getAttribute("title").getValue(0));
		assertNotNull(p.getRoot().getAttribute("showGrid"));
		assertEquals("5", p.getRoot().getAttribute("gridSize").getValue(0));
		assertNotNull(p.getRoot().getAttribute("disableScroll"));

		assertEquals("subentity_count", 7, p.getRoot().getSubEntityCount());

		EdmEntity subE = p.getRoot().getSubEntity(0);
		assertEquals("activeRectangleClass", subE.getType());
		assertEquals("attribute_count", 9, subE.getAttributeCount());
		assertEquals("4", subE.getAttribute("major").getValue(0));
		assertEquals("0", subE.getAttribute("minor").getValue(0));
		assertEquals("0", subE.getAttribute("release").getValue(0));
		assertEquals("120", subE.getAttribute("x").getValue(0));
		assertEquals("45", subE.getAttribute("y").getValue(0));
		assertEquals("155", subE.getAttribute("w").getValue(0));
		assertEquals("42", subE.getAttribute("h").getValue(0));
		assertEquals("index 123", subE.getAttribute("lineColor").getValue(0));
		assertEquals("index 0", subE.getAttribute("fillColor").getValue(0));

		subE = p.getRoot().getSubEntity(1);
		assertEquals("activeRectangleClass", subE.getType());
		assertEquals("attribute_count", 9, subE.getAttributeCount());
		assertEquals("4", subE.getAttribute("major").getValue(0));
		assertEquals("0", subE.getAttribute("minor").getValue(0));
		assertEquals("0", subE.getAttribute("release").getValue(0));
		assertEquals("4", subE.getAttribute("x").getValue(0));
		assertEquals("94", subE.getAttribute("y").getValue(0));
		assertEquals("271", subE.getAttribute("w").getValue(0));
		assertEquals("80", subE.getAttribute("h").getValue(0));
		assertEquals("index 7", subE.getAttribute("lineColor").getValue(0));
		assertEquals("index 0", subE.getAttribute("fillColor").getValue(0));

		subE = p.getRoot().getSubEntity(2);
		assertEquals("activeXTextDspClass:noedit", subE.getType());
		assertEquals("attribute_count", 20, subE.getAttributeCount());
		assertEquals("4", subE.getAttribute("major").getValue(0));
		assertEquals("0", subE.getAttribute("minor").getValue(0));
		assertEquals("0", subE.getAttribute("release").getValue(0));
		assertEquals("159", subE.getAttribute("x").getValue(0));
		assertEquals("64", subE.getAttribute("y").getValue(0));
		assertEquals("78", subE.getAttribute("w").getValue(0));
		assertEquals("18", subE.getAttribute("h").getValue(0));
		assertEquals("$(DEVICE):$(MRN)_MON", subE.getAttribute("controlPv").getValue(0));
		assertEquals("decimal", subE.getAttribute("format").getValue(0));
		assertEquals("helvetica-bold-r-14.0", subE.getAttribute("font").getValue(0));
		assertEquals("center", subE.getAttribute("fontAlign").getValue(0));
		assertEquals("index 16", subE.getAttribute("fgColor").getValue(0));
		assertNotNull(subE.getAttribute("fgAlarm"));
		assertEquals("index 10", subE.getAttribute("bgColor").getValue(0));
		assertNotNull(subE.getAttribute("limitsFromDb"));
		assertEquals("index 0", subE.getAttribute("nullColor").getValue(0));
		assertNotNull(subE.getAttribute("fastUpdate"));
		assertNotNull(subE.getAttribute("showUnits"));
		assertNotNull(subE.getAttribute("newPos"));
		assertEquals("monitors", subE.getAttribute("objType").getValue(0));

		subE = p.getRoot().getSubEntity(3);
		assertEquals("activeMessageButtonClass", subE.getType());
		assertEquals("attribute_count", 19, subE.getAttributeCount());
		assertEquals("4", subE.getAttribute("major").getValue(0));
		assertEquals("0", subE.getAttribute("minor").getValue(0));
		assertEquals("0", subE.getAttribute("release").getValue(0));
		assertEquals("8", subE.getAttribute("x").getValue(0));
		assertEquals("54", subE.getAttribute("y").getValue(0));
		assertEquals("14", subE.getAttribute("w").getValue(0));
		assertEquals("14", subE.getAttribute("h").getValue(0));
		assertEquals("index 25", subE.getAttribute("fgColor").getValue(0));
		assertEquals("index 4", subE.getAttribute("onColor").getValue(0));
		assertEquals("index 3", subE.getAttribute("offColor").getValue(0));
		assertEquals("index 1", subE.getAttribute("topShadowColor").getValue(0));
		assertEquals("index 11", subE.getAttribute("botShadowColor").getValue(0));
		assertEquals("$(DEVICE):$(MRN)_TWR.PROC", subE.getAttribute("controlPv").getValue(0));
		assertEquals("1", subE.getAttribute("pressValue").getValue(0));
		assertEquals("-", subE.getAttribute("onLabel").getValue(0));
		assertEquals("-", subE.getAttribute("offLabel").getValue(0));
		assertNotNull(subE.getAttribute("3d"));
		assertEquals("helvetica-bold-r-12.0", subE.getAttribute("font").getValue(0));
		assertNotNull(subE.getAttribute("visInvert"));

		subE = p.getRoot().getSubEntity(4);
		assertEquals("activeMessageButtonClass", subE.getType());
		assertEquals("attribute_count", 19, subE.getAttributeCount());
		assertEquals("4", subE.getAttribute("major").getValue(0));
		assertEquals("0", subE.getAttribute("minor").getValue(0));
		assertEquals("0", subE.getAttribute("release").getValue(0));
		assertEquals("97", subE.getAttribute("x").getValue(0));
		assertEquals("54", subE.getAttribute("y").getValue(0));
		assertEquals("14", subE.getAttribute("w").getValue(0));
		assertEquals("14", subE.getAttribute("h").getValue(0));
		assertEquals("index 25", subE.getAttribute("fgColor").getValue(0));
		assertEquals("index 4", subE.getAttribute("onColor").getValue(0));
		assertEquals("index 3", subE.getAttribute("offColor").getValue(0));
		assertEquals("index 1", subE.getAttribute("topShadowColor").getValue(0));
		assertEquals("index 11", subE.getAttribute("botShadowColor").getValue(0));
		assertEquals("$(DEVICE):$(MRN)_TWF.PROC", subE.getAttribute("controlPv").getValue(0));
		assertEquals("1", subE.getAttribute("pressValue").getValue(0));
		assertEquals("+", subE.getAttribute("onLabel").getValue(0));
		assertEquals("+", subE.getAttribute("offLabel").getValue(0));
		assertNotNull(subE.getAttribute("3d"));
		assertEquals("helvetica-bold-r-12.0", subE.getAttribute("font").getValue(0));
		assertNotNull(subE.getAttribute("visInvert"));

		subE = p.getRoot().getSubEntity(5);
		assertEquals("attribute_count", 12, subE.getAttributeCount());
		assertEquals("activeXTextClass", subE.getType());
		assertEquals("4", subE.getAttribute("major").getValue(0));
		assertEquals("1", subE.getAttribute("minor").getValue(0));
		assertEquals("0", subE.getAttribute("release").getValue(0));
		assertEquals("123", subE.getAttribute("x").getValue(0));
		assertEquals("50", subE.getAttribute("y").getValue(0));
		assertEquals("42", subE.getAttribute("w").getValue(0));
		assertEquals("13", subE.getAttribute("h").getValue(0));
		assertEquals("helvetica-bold-r-12.0", subE.getAttribute("font").getValue(0));
		assertEquals("index 10", subE.getAttribute("fgColor").getValue(0));
		assertEquals("index 3", subE.getAttribute("bgColor").getValue(0));
		assertEquals("At low", subE.getAttribute("value").getValue(0));
		assertNotNull(subE.getAttribute("autoSize"));

		subE = p.getRoot().getSubEntity(6);
		assertEquals("activeGroupClass", subE.getType());
		assertEquals("attribute_count", 7, subE.getAttributeCount());
		assertEquals("4", subE.getAttribute("major").getValue(0));
		assertEquals("0", subE.getAttribute("minor").getValue(0));
		assertEquals("0", subE.getAttribute("release").getValue(0));
		assertEquals("8", subE.getAttribute("x").getValue(0));
		assertEquals("120", subE.getAttribute("y").getValue(0));
		assertEquals("52", subE.getAttribute("w").getValue(0));
		assertEquals("11", subE.getAttribute("h").getValue(0));
		{
			assertEquals(2, subE.getSubEntityCount());

			EdmEntity subE2 = subE.getSubEntity(0);
			assertEquals("activeXTextClass", subE2.getType());
			assertEquals("attribute_count", 12, subE2.getAttributeCount());
			assertEquals("4", subE2.getAttribute("major").getValue(0));
			assertEquals("1", subE2.getAttribute("minor").getValue(0));
			assertEquals("0", subE2.getAttribute("release").getValue(0));
			assertEquals("26", subE2.getAttribute("x").getValue(0));
			assertEquals("120", subE2.getAttribute("y").getValue(0));
			assertEquals("35", subE2.getAttribute("w").getValue(0));
			assertEquals("13", subE2.getAttribute("h").getValue(0));
			assertEquals("helvetica-bold-r-12.0", subE2.getAttribute("font").getValue(0));
			assertEquals("index 10", subE2.getAttribute("fgColor").getValue(0));
			assertEquals("index 3", subE2.getAttribute("bgColor").getValue(0));
			assertEquals("Homed", subE2.getAttribute("value").getValue(0));
			assertNotNull(subE2.getAttribute("autoSize"));

			subE2 = subE.getSubEntity(1);
			assertEquals("ByteClass", subE2.getType());
			assertEquals("attribute_count", 13, subE2.getAttributeCount());
			assertEquals("4", subE2.getAttribute("major").getValue(0));
			assertEquals("0", subE2.getAttribute("minor").getValue(0));
			assertEquals("0", subE2.getAttribute("release").getValue(0));
			assertEquals("8", subE2.getAttribute("x").getValue(0));
			assertEquals("121", subE2.getAttribute("y").getValue(0));
			assertEquals("11", subE2.getAttribute("w").getValue(0));
			assertEquals("10", subE2.getAttribute("h").getValue(0));
			assertEquals("$(DEVICE):$(MRN)_HOCPL_STS", subE2.getAttribute("controlPv").getValue(0));
			assertEquals("index 14", subE2.getAttribute("lineColor").getValue(0));
			assertEquals("index 15", subE2.getAttribute("onColor").getValue(0));
			assertEquals("index 8", subE2.getAttribute("offColor").getValue(0));
			assertEquals("2", subE2.getAttribute("lineWidth").getValue(0));
			assertEquals("1", subE2.getAttribute("numBits").getValue(0));
		}
	}
}
