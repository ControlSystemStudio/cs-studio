package org.csstudio.opibuilder.converter.writer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.csstudio.opibuilder.converter.model.EdmAttribute;
import org.csstudio.opibuilder.converter.model.EdmException;
import org.csstudio.opibuilder.converter.model.EdmString;
import junit.framework.TestCase;

public class OpiStringTest extends TestCase {

	public void testOpiString() throws EdmException {
		// init document
		Document doc = XMLFileHandler.createDomDocument();
		
		String name = "stringElement";
		String val = "abcdèšæðžŽÐŠÆÈxyw~@{}2^¡";
		EdmString s = new EdmString(new EdmAttribute(val), true);
		
		Element parent = doc.createElement("root");
		doc.appendChild(parent);
		OpiString o = new OpiString(doc, parent, name, s);
		assertTrue(o instanceof OpiAttribute);
		
		// testing
		Element x = (Element)doc.getElementsByTagName(name).item(0);
		assertEquals(val, x.getTextContent());
		
		XMLFileHandler.writeXML(doc);
	}
}
