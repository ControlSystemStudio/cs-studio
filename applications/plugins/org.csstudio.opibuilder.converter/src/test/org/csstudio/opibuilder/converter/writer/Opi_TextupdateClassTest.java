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
import org.csstudio.opibuilder.converter.model.Edm_TextupdateClass;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Opi_TextupdateClassTest extends TestCase {

	public void testOpi_TextupdateClass() throws EdmException {

		System.setProperty("edm2xml.robustParsing", "false");
		System.setProperty("edm2xml.colorsFile", "src/test/resources/colors.list");

		Document doc = XMLFileHandler.createDomDocument();
		Element root = doc.createElement("root");
		doc.appendChild(root);

		String edlFile = "src/test/resources/TextUpdate_example.edl";
		EdmModel.getInstance();
		EdmDisplay d = EdmModel.getDisplay(edlFile);
		assertTrue(d.getSubEntity(0) instanceof Edm_TextupdateClass);
		Edm_TextupdateClass t = (Edm_TextupdateClass)d.getSubEntity(0);

		Context context = new Context(doc, root, d, 0, 0);
		Opi_TextupdateClass o = new Opi_TextupdateClass(context, t);
		assertTrue(o instanceof OpiWidget);

		Element e = (Element)doc.getElementsByTagName("widget").item(0);
		assertEquals("org.csstudio.opibuilder.widgets.TextUpdate", e.getAttribute("typeId"));
		assertEquals("1.0", e.getAttribute("version"));

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
		XMLFileHandler.isElementEqual("right", "font_align", e);

		XMLFileHandler.isElementEqual("2", "border_width", e);
		XMLFileHandler.isElementEqual("true", "foregroundcolor_alarmsensitive", e);
		XMLFileHandler.isElementEqual("true", "border_alarmsensitive", e);

		//XMLFileHandler.writeXML(doc);
	}
}
