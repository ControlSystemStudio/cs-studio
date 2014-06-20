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
import org.csstudio.opibuilder.converter.model.Edm_activeXTextClass;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Opi_activeXTextClassTest extends TestCase {

	public void testOpi_activeXTextClass() throws EdmException {

		System.setProperty("edm2xml.robustParsing", "false");
		System.setProperty("edm2xml.colorsFile", "src/test/resources/colors.list");

		Document doc = XMLFileHandler.createDomDocument();
		Element root = doc.createElement("root");
		doc.appendChild(root);

		String edlFile = "src/test/resources/EDMDisplayParser_example.edl";
		EdmModel.getInstance();
		EdmDisplay d = EdmModel.getDisplay(edlFile);

		assertTrue(d.getSubEntity(6) instanceof Edm_activeXTextClass);
		Edm_activeXTextClass t = (Edm_activeXTextClass)d.getSubEntity(6);

		Context context = new Context(doc, root, d, 0, 0);
		Opi_activeXTextClass o = new Opi_activeXTextClass(context, t);
		assertTrue(o instanceof OpiWidget);

		Element e = (Element)doc.getElementsByTagName("widget").item(0);
		assertEquals("org.csstudio.opibuilder.widgets.Label", e.getAttribute("typeId"));
		assertEquals("1.0", e.getAttribute("version"));

		XMLFileHandler.isElementEqual("EDM Label", "name", e);
		XMLFileHandler.isElementEqual("123", "x", e);
		XMLFileHandler.isElementEqual("50", "y", e);
		XMLFileHandler.isElementEqual("42", "width", e);
		XMLFileHandler.isElementEqual("13", "height", e);

		XMLFileHandler.isFontElementEqual("helvetica-bold-r-12.0", "font", e);

		XMLFileHandler.isColorElementEqual(new EdmColor(10), "color_foreground", e);
		XMLFileHandler.isColorElementEqual(new EdmColor(3), "color_background", e);

		XMLFileHandler.isElementEqual("At low", "text", e);
		XMLFileHandler.isElementEqual("true", "auto_size", e);

		XMLFileHandler.isElementEqual("1", "border_style", e);
		XMLFileHandler.isElementEqual("2", "border_width", e);
		XMLFileHandler.isElementEqual("true", "transparency", e);
		
		//XMLFileHandler.writeXML(doc);
	}
}
