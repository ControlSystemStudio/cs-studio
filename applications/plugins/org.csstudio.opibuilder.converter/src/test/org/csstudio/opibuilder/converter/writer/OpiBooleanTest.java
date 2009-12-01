package org.csstudio.opibuilder.converter.writer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.csstudio.opibuilder.converter.model.EdmAttribute;
import org.csstudio.opibuilder.converter.model.EdmBoolean;
import org.csstudio.opibuilder.converter.model.EdmException;
import junit.framework.TestCase;

public class OpiBooleanTest extends TestCase {

	public void testOpiBoolean() throws EdmException {
	
		// init document
		Document doc = XMLFileHandler.createDomDocument();
		
		// OpiBoolean data 
		String name = "someBooleanElement";
		EdmBoolean bT = new EdmBoolean(new EdmAttribute());	//[TRUE]
		EdmBoolean bF = new EdmBoolean(null);				//[FALSE]
		
		// instantiating OpiBoolean
		Element parent = doc.createElement("root");
		doc.appendChild(parent);
		OpiBoolean o = new OpiBoolean(doc, parent, name, bT);
		assertTrue(o instanceof OpiAttribute);
		new OpiBoolean(doc, parent, name, bF);
		
		// testing
		Element x = (Element)doc.getElementsByTagName(name).item(0);
		assertEquals("true", x.getTextContent());
		x = (Element)doc.getElementsByTagName(name).item(1);
		assertEquals("false", x.getTextContent());
		
		XMLFileHandler.writeXML(doc);
		
	}
	
}
