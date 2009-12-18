package org.csstudio.opibuilder.converter.writer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.csstudio.opibuilder.converter.model.EdmException;
import junit.framework.TestCase;

public class OpiAttributeTest extends TestCase {

	public void testOpiAttribute() throws EdmException {
		
		// init document
		Document doc = XMLFileHandler.createDomDocument();
		
		// OpiAttribute data
		String name = "x";
		
		// instantiating OpiAttribute
		Element parent = doc.createElement("root");
		doc.appendChild(parent);
		Context context = new Context(doc, parent, 0, 0);
		new OpiAttribute(context, name);
		
		// testing
		assertEquals(1, doc.getElementsByTagName(name).getLength());
		
		//XMLFileHandler.writeXML(doc);
		
	}
}
