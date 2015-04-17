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
import org.csstudio.opibuilder.converter.model.EdmException;
import org.csstudio.opibuilder.converter.model.EdmFont;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class OpiFontTest extends TestCase {
	
    //<font fontName="Arial" height="14" style="0" />
	
	/*style conversion definition:
	0 - medium, reg
	1 - bold, reg
	2 - medui, italic
	3 - b, i
	*/
	
	public void testOpiFont() throws EdmException {
		
		// init document
		Document doc = XMLFileHandler.createDomDocument();
		
		String val = "helvetica-bold-r-14.0";
		EdmFont f = new EdmFont(new EdmAttribute(val), true);
		Element parent = doc.createElement("root");
		doc.appendChild(parent);
		Context context = new Context(doc, parent, null, 0, 0);
		new OpiFont(context, "font", f);
		
		XMLFileHandler.isFontElementEqual(val, "font", parent);
		
		//XMLFileHandler.writeXML(doc);
		
	}
}
