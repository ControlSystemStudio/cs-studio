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
import org.csstudio.opibuilder.converter.model.EdmBoolean;
import org.csstudio.opibuilder.converter.model.EdmException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class OpiBooleanTest extends TestCase {

	public void testOpiBoolean() throws EdmException {
	
		// init document
		Document doc = XMLFileHandler.createDomDocument();
		
		// OpiBoolean data 
		String name = "someBooleanElement";
		EdmBoolean bT = new EdmBoolean(new EdmAttribute(), false);	//[TRUE]
		EdmBoolean bF = new EdmBoolean(null, false);				//[FALSE]
		
		// instantiating OpiBoolean
		Element parent = doc.createElement("root");
		doc.appendChild(parent);
		Context context = new Context(doc, parent, null, 0, 0);
		OpiBoolean o = new OpiBoolean(context, name, bT);
		assertTrue(o instanceof OpiAttribute);
		new OpiBoolean(context, name, bF);
		
		// testing
		Element x = (Element)doc.getElementsByTagName(name).item(0);
		assertEquals("true", x.getTextContent());
		x = (Element)doc.getElementsByTagName(name).item(1);
		assertEquals("false", x.getTextContent());
		
		//XMLFileHandler.writeXML(doc);
		
	}
	
}
