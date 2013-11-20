/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter.writer;

import junit.framework.TestCase;

import org.csstudio.opibuilder.converter.model.EdmAttribute;
import org.csstudio.opibuilder.converter.model.EdmColor;
import org.csstudio.opibuilder.converter.model.EdmException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class OpiColorTest extends TestCase {
	
    //<color red="255" green="255" blue="255" />
	
	public void testRgbOpiColor() throws EdmException {
		
		// init document
		Document doc = XMLFileHandler.createDomDocument();
		
		EdmColor c = new EdmColor(new EdmAttribute("rgb 65535 512 256"), true);
		
		Element parent = doc.createElement("root");
		doc.appendChild(parent);
		Context context = new Context(doc, parent, null, 0, 0);
		new OpiColor(context, "color", c, null);
		//XMLFileHandler.writeXML(doc);		
		
		XMLFileHandler.isColorElementEqual("", 255, 2, 1, 0, 0, 0, "color", parent);
	}

	public void testDefinitionOpiColor() throws EdmException {
		
		// init document
		Document doc = XMLFileHandler.createDomDocument();
		
		EdmAttribute a = new EdmAttribute("\"blinking purple\"");
		a.appendValue("65535 512 256");
		EdmColor c = new EdmColor(a, true);
		
		Element parent = doc.createElement("root");
		doc.appendChild(parent);
		Context context = new Context(doc, parent, null, 0, 0);
		new OpiColor(context, "color", c, null);
		//XMLFileHandler.writeXML(doc);		
		
		XMLFileHandler.isColorElementEqual("blinking purple", 255, 2, 1, 0, 0, 0, "color", parent);
	}
}
