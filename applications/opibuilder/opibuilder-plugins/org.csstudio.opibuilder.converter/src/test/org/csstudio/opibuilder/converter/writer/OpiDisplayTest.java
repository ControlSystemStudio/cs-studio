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
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class OpiDisplayTest extends TestCase {

	// <display typeId="org.csstudio.opibuilder.Display" ... >

	public void testOpiDisplay() throws EdmException {

		System.setProperty("edm2xml.robustParsing", "false");
		System.setProperty("edm2xml.colorsFile", "src/test/resources/colors.list");

		// init document
		Document doc = XMLFileHandler.createDomDocument();

		String edlFile = "src/test/resources/EDMDisplayParser_example.edl";
		EdmModel.getInstance();
		EdmDisplay display = EdmModel.getDisplay(edlFile);

		new OpiDisplay(doc, display, edlFile);


		Element e = (Element)doc.getElementsByTagName("display").item(0);
		assertEquals("org.csstudio.opibuilder.Display", e.getAttribute("typeId"));
		assertEquals("1.0", e.getAttribute("version"));

		XMLFileHandler.isElementEqual("614", "x", e);
		XMLFileHandler.isElementEqual("278", "y", e);
		XMLFileHandler.isElementEqual("280", "width", e);
		XMLFileHandler.isElementEqual("177", "height", e);

		XMLFileHandler.isFontElementEqual("helvetica-bold-r-14.0", "font", e);
		XMLFileHandler.isFontElementEqual("helvetica-bold-r-14.0", "font_ctl", e);
		XMLFileHandler.isFontElementEqual("helvetica-bold-r-14.0", "font_button", e);

		XMLFileHandler.isColorElementEqual(new EdmColor(14), "color_foreground", e);
		XMLFileHandler.isColorElementEqual(new EdmColor(3), "color_background", e);
		XMLFileHandler.isColorElementEqual(new EdmColor(14), "color_text", e);
		XMLFileHandler.isColorElementEqual("", 1, 2, 255, 0, 0, 0, "color_ctlFgColor1", e);
		XMLFileHandler.isColorElementEqual(new EdmColor(30), "color_ctlFgColor2", e);
		XMLFileHandler.isColorElementEqual(new EdmColor(3), "color_ctlBgColor1", e);
		XMLFileHandler.isColorElementEqual(new EdmColor(3), "color_ctlBgColor2", e);
		XMLFileHandler.isColorElementEqual(new EdmColor(1), "color_topshadowcolor", e);
		XMLFileHandler.isColorElementEqual(new EdmColor(11), "color_botshadowcolor", e);

		XMLFileHandler.isElementEqual("Motor control", "name", e);
		XMLFileHandler.isElementEqual("true", "grid_show", e);
		XMLFileHandler.isElementEqual("5", "grid_space", e);
		XMLFileHandler.isElementEqual("true", "scroll_disable", e);

		//XMLFileHandler.writeXML(doc);
	}

	public void testOptionality() throws EdmException {

		System.setProperty("edm2xml.robustParsing", "false");
		System.setProperty("edm2xml.colorsFile", "src/test/resources/colors.list");

		// init document
		Document doc = XMLFileHandler.createDomDocument();

		String edlFile = "src/test/resources/EDMDisplay_optionals.edl";
		EdmModel.getInstance();
		EdmDisplay d = EdmModel.getDisplay(edlFile);

		new OpiDisplay(doc, d, edlFile);

		Element e = (Element)doc.getElementsByTagName("display").item(0);
		assertEquals("org.csstudio.opibuilder.Display", e.getAttribute("typeId"));
		assertEquals("1.0", e.getAttribute("version"));

		XMLFileHandler.isElementEqual("614", "x", e);
		XMLFileHandler.isElementEqual("278", "y", e);
		XMLFileHandler.isElementEqual("280", "width", e);
		XMLFileHandler.isElementEqual("177", "height", e);

		XMLFileHandler.isFontElementEqual("helvetica-bold-r-14.0", "font", e);
		XMLFileHandler.isFontElementEqual("helvetica-bold-r-14.0", "font_ctl", e);
		XMLFileHandler.isFontElementEqual("helvetica-bold-r-14.0", "font_button", e);

		XMLFileHandler.isColorElementEqual(new EdmColor(14), "color_foreground", e);
		XMLFileHandler.isColorElementEqual(new EdmColor(3), "color_background", e);
		XMLFileHandler.isColorElementEqual(new EdmColor(14), "color_text", e);
		XMLFileHandler.isColorElementEqual("", 1, 2, 255, 0, 0, 0, "color_ctlFgColor1", e);
		XMLFileHandler.isColorElementEqual(new EdmColor(30), "color_ctlFgColor2", e);
		XMLFileHandler.isColorElementEqual(new EdmColor(3), "color_ctlBgColor1", e);
		XMLFileHandler.isColorElementEqual(new EdmColor(3), "color_ctlBgColor2", e);
		XMLFileHandler.isColorElementEqual(new EdmColor(1), "color_topshadowcolor", e);
		XMLFileHandler.isColorElementEqual(new EdmColor(11), "color_botshadowcolor", e);

		// should be missing!
		//assertFalse(XMLFileHandler.isChildElement("name", e));
		XMLFileHandler.isElementEqual("true", "grid_show", e);
		// should be missing!
		assertFalse(XMLFileHandler.isChildElement("grid_space", e));
		XMLFileHandler.isElementEqual("true", "scroll_disable", e);
		{
			e = (Element)doc.getElementsByTagName("widget").item(0);
			assertEquals("org.csstudio.opibuilder.widgets.Rectangle", e.getAttribute("typeId"));
			assertEquals("1.0", e.getAttribute("version"));

			XMLFileHandler.isElementEqual("EDM Rectangle", "name", e);
			
			XMLFileHandler.isElementEqual("4", "x", e);
			XMLFileHandler.isElementEqual("45", "y", e);
			XMLFileHandler.isElementEqual("111", "width", e);
			XMLFileHandler.isElementEqual("42", "height", e);

			XMLFileHandler.isColorElementEqual(new EdmColor(7), "border_color", e);
			// should be missing!
			assertFalse(XMLFileHandler.isChildElement("color_background", e));
			XMLFileHandler.isElementEqual("0", "border_style", e);
		}

		{

			e = (Element)doc.getElementsByTagName("widget").item(2);
			assertEquals("org.csstudio.opibuilder.widgets.TextUpdate", e.getAttribute("typeId"));
			assertEquals("1.0", e.getAttribute("version"));

			//XMLFileHandler.writeXML(doc);

			XMLFileHandler.isElementEqual("EDM Text Update", "name", e);
			XMLFileHandler.isElementEqual("490", "x", e);
			XMLFileHandler.isElementEqual("400", "y", e);
			XMLFileHandler.isElementEqual("110", "width", e);
			XMLFileHandler.isElementEqual("20", "height", e);

			XMLFileHandler.isElementEqual("$(S)_LLRF:ResCtrl$(N):ResErr_Avg", "pv_name", e);

			XMLFileHandler.isColorElementEqual(new EdmColor(112), "color_foreground", e);
			XMLFileHandler.isColorElementEqual(new EdmColor(5), "color_background", e);
			XMLFileHandler.isElementEqual("true", "color_fill", e);

			XMLFileHandler.isFontElementEqual("courier-medium-r-16.0", "font", e);
			// should be missing!
			assertFalse(XMLFileHandler.isChildElement("font_align", e));

			// should be missing!
			assertFalse(XMLFileHandler.isChildElement("border_width", e));
			XMLFileHandler.isElementEqual("false", "foregroundcolor_alarmsensitive", e);
			XMLFileHandler.isElementEqual("false", "border_alarmsensitive", e);
		}
	}
}
