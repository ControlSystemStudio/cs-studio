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
import org.csstudio.opibuilder.converter.model.EdmString;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class OpiStringTest extends TestCase {

	public void testOpiString() throws EdmException {
		// init document
		Document doc = XMLFileHandler.createDomDocument();
		
		String name = "stringElement";
		String val = "test";
		EdmString s = new EdmString(new EdmAttribute(val), true);
		
		Element parent = doc.createElement("root");
		doc.appendChild(parent);
		Context context = new Context(doc, parent, null, 0, 0);
		OpiString o = new OpiString(context, name, s);
		assertTrue(o instanceof OpiAttribute);
		
		// testing
		Element x = (Element)doc.getElementsByTagName(name).item(0);
		assertEquals(val, x.getTextContent());
		
		//XMLFileHandler.writeXML(doc);
	}
}
