package org.csstudio.opibuilder.converter.writer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.csstudio.opibuilder.converter.model.EdmAttribute;
import org.csstudio.opibuilder.converter.model.EdmException;
import org.csstudio.opibuilder.converter.model.EdmInt;
import junit.framework.TestCase;

public class OpiIntTest extends TestCase {

	public void testOpiInt() throws EdmException {
		// init document
		Document doc = XMLFileHandler.createDomDocument();
		
		String name = "intElement";
		String val = "67";
		EdmInt i = new EdmInt(new EdmAttribute(val), true);
		
		Element parent = doc.createElement("root");
		doc.appendChild(parent);
		Context context = new Context(doc, parent, 0, 0);
		OpiInt o = new OpiInt(context, name, i);
		assertTrue(o instanceof OpiAttribute);
		
		// testing
		Element x = (Element)doc.getElementsByTagName(name).item(0);
		assertEquals(val, x.getTextContent());
		
		//XMLFileHandler.writeXML(doc);
	}
}
