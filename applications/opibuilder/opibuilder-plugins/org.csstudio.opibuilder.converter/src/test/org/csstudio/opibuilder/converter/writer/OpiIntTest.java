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
import org.csstudio.opibuilder.converter.model.EdmInt;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class OpiIntTest extends TestCase {

	public void testOpiInt() throws EdmException {
		// init document
		Document doc = XMLFileHandler.createDomDocument();
		
		String name = "intElement";
		String val = "67";
		EdmInt i = new EdmInt(new EdmAttribute(val), true);
		
		Element parent = doc.createElement("root");
		doc.appendChild(parent);
		Context context = new Context(doc, parent, null, 0, 0);
		OpiInt o = new OpiInt(context, name, i);
		assertTrue(o instanceof OpiAttribute);
		
		// testing
		Element x = (Element)doc.getElementsByTagName(name).item(0);
		assertEquals(val, x.getTextContent());
		
		//XMLFileHandler.writeXML(doc);
	}
}
