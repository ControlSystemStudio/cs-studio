/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter.writer;

import junit.framework.TestCase;

import org.csstudio.opibuilder.converter.model.EdmColor;
import org.csstudio.opibuilder.converter.model.EdmDisplay;
import org.csstudio.opibuilder.converter.model.EdmException;
import org.csstudio.opibuilder.converter.model.EdmModel;
import org.csstudio.opibuilder.converter.model.Edm_activeGroupClass;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Opi_activeGroupClassTest extends TestCase {

	private static final int groupAttributesCount = 4;
	
	public void testOpi_activeGroupClass() throws EdmException {

		// Prepare DOM model.
		System.setProperty("edm2xml.robustParsing", "false");
		System.setProperty("edm2xml.colorsFile", "src/test/resources/colors.list");

		Document doc = XMLFileHandler.createDomDocument();
		Element root = doc.createElement("root");
		doc.appendChild(root);

		String edlFile = "src/test/resources/group_example_spec.edl";
		EdmModel.getInstance();
		EdmDisplay d = EdmModel.getDisplay(edlFile);

		assertTrue(d.getSubEntity(0) instanceof Edm_activeGroupClass);
		Edm_activeGroupClass g = (Edm_activeGroupClass)d.getSubEntity(0);

		Context context = new Context(doc, root, d, 0, 0);
		Opi_activeGroupClass o = new Opi_activeGroupClass(context, g);
		//XMLFileHandler.writeXML(doc);

		assertTrue(o instanceof OpiWidget);

		// Check the generated model hierarchy.
		assertEquals("subentity_count", 1, root.getChildNodes().getLength());

		// Get objects in various places in the group hierarchy.
		Element subElement0 = (Element)root.getFirstChild();
		

		assertEquals("org.csstudio.opibuilder.widgets.groupingContainer", subElement0.getAttribute("typeId"));
		assertEquals("1.0", subElement0.getAttribute("version"));
		XMLFileHandler.isElementEqual("8", "x", subElement0);
		XMLFileHandler.isElementEqual("120", "y", subElement0);
		XMLFileHandler.isElementEqual("52", "width", subElement0);
		XMLFileHandler.isElementEqual("11", "height", subElement0);

		assertEquals(groupAttributesCount + 2, subElement0.getChildNodes().getLength());
		{
			Element subElement00 = (Element)subElement0.getChildNodes().item(groupAttributesCount);

			assertEquals("org.csstudio.opibuilder.widgets.Label", subElement00.getAttribute("typeId"));
			assertEquals("1.0", subElement00.getAttribute("version"));
			XMLFileHandler.isElementEqual("EDM Label", "name", subElement00);
			XMLFileHandler.isElementEqual("18", "x", subElement00);
			XMLFileHandler.isElementEqual("0", "y", subElement00);
			XMLFileHandler.isElementEqual("35", "width", subElement00);
			XMLFileHandler.isElementEqual("13", "height", subElement00);
			XMLFileHandler.isFontElementEqual("helvetica-bold-r-12.0", "font", subElement00);
			XMLFileHandler.isColorElementEqual(new EdmColor(10), "color_foreground", subElement00);
			XMLFileHandler.isColorElementEqual(new EdmColor(3), "color_background", subElement00);
			XMLFileHandler.isElementEqual("Homed", "text", subElement00);
			XMLFileHandler.isElementEqual("true", "auto_size", subElement00);
			XMLFileHandler.isElementEqual("0", "border_style", subElement00);
			XMLFileHandler.isElementEqual("false", "transparency", subElement00);
			
			Element subElement01 = (Element)subElement0.getChildNodes().item(groupAttributesCount + 1);

			assertEquals("org.csstudio.opibuilder.widgets.groupingContainer", subElement01.getAttribute("typeId"));
			assertEquals("1.0", subElement01.getAttribute("version"));
			XMLFileHandler.isElementEqual("100", "x", subElement01);
			XMLFileHandler.isElementEqual("100", "y", subElement01);
			XMLFileHandler.isElementEqual("152", "width", subElement01);
			XMLFileHandler.isElementEqual("111", "height", subElement01);

			assertEquals(3 + 4, subElement01.getChildNodes().getLength());
			{

				Element subElement010 = (Element)subElement01.getChildNodes().item(groupAttributesCount);

				assertEquals("org.csstudio.opibuilder.widgets.Label", subElement010.getAttribute("typeId"));
				assertEquals("1.0", subElement010.getAttribute("version"));
				XMLFileHandler.isElementEqual("EDM Label", "name", subElement010);
				XMLFileHandler.isElementEqual("-82", "x", subElement010);
				XMLFileHandler.isElementEqual("-100", "y", subElement010);
				XMLFileHandler.isElementEqual("35", "width", subElement010);
				XMLFileHandler.isElementEqual("13", "height", subElement010);
				XMLFileHandler.isFontElementEqual("helvetica-bold-r-12.0", "font", subElement010);
				XMLFileHandler.isColorElementEqual(new EdmColor(10), "color_foreground", subElement010);
				XMLFileHandler.isColorElementEqual(new EdmColor(3), "color_background", subElement010);
				XMLFileHandler.isElementEqual("Homed", "text", subElement010);
				XMLFileHandler.isElementEqual("true", "auto_size", subElement010);
				XMLFileHandler.isElementEqual("0", "border_style", subElement010);
				XMLFileHandler.isElementEqual("false", "transparency", subElement010);

				Element subElement011 = (Element)subElement01.getChildNodes().item(groupAttributesCount + 1);

				assertEquals("org.csstudio.opibuilder.widgets.groupingContainer", subElement011.getAttribute("typeId"));
				assertEquals("1.0", subElement011.getAttribute("version"));
				XMLFileHandler.isElementEqual("100", "x", subElement011);
				XMLFileHandler.isElementEqual("100", "y", subElement011);
				XMLFileHandler.isElementEqual("252", "width", subElement011);
				XMLFileHandler.isElementEqual("211", "height", subElement011);

				assertEquals(1 + 4, subElement011.getChildNodes().getLength());
				{
					Element subElement0110 = (Element)subElement011.getChildNodes().item(groupAttributesCount);

					assertEquals("org.csstudio.opibuilder.widgets.Label", subElement0110.getAttribute("typeId"));
					assertEquals("1.0", subElement0110.getAttribute("version"));
					XMLFileHandler.isElementEqual("EDM Label", "name", subElement0110);
					XMLFileHandler.isElementEqual("-182", "x", subElement0110);
					XMLFileHandler.isElementEqual("-200", "y", subElement0110);
					XMLFileHandler.isElementEqual("35", "width", subElement0110);
					XMLFileHandler.isElementEqual("13", "height", subElement0110);
					XMLFileHandler.isFontElementEqual("helvetica-bold-r-12.0", "font", subElement0110);
					XMLFileHandler.isColorElementEqual(new EdmColor(10), "color_foreground", subElement0110);
					XMLFileHandler.isColorElementEqual(new EdmColor(3), "color_background", subElement0110);
					XMLFileHandler.isElementEqual("Homed", "text", subElement0110);
					XMLFileHandler.isElementEqual("true", "auto_size", subElement0110);
					XMLFileHandler.isElementEqual("0", "border_style", subElement0110);
					XMLFileHandler.isElementEqual("false", "transparency", subElement0110);
}

				Element subElement012 = (Element)subElement01.getChildNodes().item(groupAttributesCount + 2);
				assertEquals("org.csstudio.opibuilder.widgets.groupingContainer", subElement012.getAttribute("typeId"));
				assertEquals("1.0", subElement012.getAttribute("version"));
				XMLFileHandler.isElementEqual("200", "x", subElement012);
				XMLFileHandler.isElementEqual("200", "y", subElement012);
				XMLFileHandler.isElementEqual("352", "width", subElement012);
				XMLFileHandler.isElementEqual("311", "height", subElement012);

				assertEquals(1 + 4, subElement012.getChildNodes().getLength());
				{
					Element subElement0120 = (Element)subElement012.getChildNodes().item(groupAttributesCount);

					assertEquals("org.csstudio.opibuilder.widgets.Label", subElement0120.getAttribute("typeId"));
					assertEquals("1.0", subElement0120.getAttribute("version"));
					XMLFileHandler.isElementEqual("EDM Label", "name", subElement0120);
					XMLFileHandler.isElementEqual("-282", "x", subElement0120);
					XMLFileHandler.isElementEqual("-300", "y", subElement0120);
					XMLFileHandler.isElementEqual("35", "width", subElement0120);
					XMLFileHandler.isElementEqual("13", "height", subElement0120);
					XMLFileHandler.isFontElementEqual("helvetica-bold-r-12.0", "font", subElement0120);
					XMLFileHandler.isColorElementEqual(new EdmColor(10), "color_foreground", subElement0120);
					XMLFileHandler.isColorElementEqual(new EdmColor(3), "color_background", subElement0120);
					XMLFileHandler.isElementEqual("Homed", "text", subElement0120);
					XMLFileHandler.isElementEqual("true", "auto_size", subElement0120);
					XMLFileHandler.isElementEqual("0", "border_style", subElement0120);
					XMLFileHandler.isElementEqual("false", "transparency", subElement0120);
}
			}
		}
	}
}
