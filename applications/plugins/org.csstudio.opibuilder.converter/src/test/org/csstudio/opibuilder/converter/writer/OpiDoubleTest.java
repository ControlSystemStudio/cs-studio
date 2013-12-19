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
import org.csstudio.opibuilder.converter.model.EdmDouble;
import org.csstudio.opibuilder.converter.model.EdmException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class OpiDoubleTest extends TestCase {

	public void testOpiDouble() throws EdmException {
		// init document
		Document doc = XMLFileHandler.createDomDocument();
		Element parent = doc.createElement("root");
		doc.appendChild(parent);
		
		String name = "doubleElement";
		String val = "12.3";
		EdmDouble d = new EdmDouble(new EdmAttribute(val), true);
		
		Context context = new Context(doc, parent, null, 0, 0);
		OpiDouble o = new OpiDouble(context, name, d);
		assertTrue(o instanceof OpiAttribute);
		
		// testing
		Element x = (Element)doc.getElementsByTagName(name).item(0);
		assertEquals(val, x.getTextContent());
		
		//XMLFileHandler.writeXML(doc);
	}
}
