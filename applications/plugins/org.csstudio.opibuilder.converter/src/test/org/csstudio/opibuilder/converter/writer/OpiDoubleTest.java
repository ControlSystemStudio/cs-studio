package org.csstudio.opibuilder.converter.writer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.csstudio.opibuilder.converter.model.EdmAttribute;
import org.csstudio.opibuilder.converter.model.EdmDouble;
import org.csstudio.opibuilder.converter.model.EdmException;
import junit.framework.TestCase;

public class OpiDoubleTest extends TestCase {

	public void testOpiDouble() throws EdmException {
		// init document
		Document doc = XMLFileHandler.createDomDocument();
		Element parent = doc.createElement("root");
		doc.appendChild(parent);
		
		String name = "doubleElement";
		String val = "12.3";
		EdmDouble d = new EdmDouble(new EdmAttribute(val), true);
		
		OpiDouble o = new OpiDouble(doc, parent, name, d);
		assertTrue(o instanceof OpiAttribute);
		
		// testing
		Element x = (Element)doc.getElementsByTagName(name).item(0);
		assertEquals(val, x.getTextContent());
		
		XMLFileHandler.writeXML(doc);
	}
}
