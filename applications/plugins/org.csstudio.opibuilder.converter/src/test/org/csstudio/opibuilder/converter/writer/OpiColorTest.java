package org.csstudio.opibuilder.converter.writer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.csstudio.opibuilder.converter.model.EdmAttribute;
import org.csstudio.opibuilder.converter.model.EdmColor;
import org.csstudio.opibuilder.converter.model.EdmException;
import junit.framework.TestCase;

public class OpiColorTest extends TestCase {
	
    //<color red="255" green="255" blue="255" />
	
	public void testRgbOpiColor() throws EdmException {
		
		// init document
		Document doc = XMLFileHandler.createDomDocument();
		
		EdmColor c = new EdmColor(new EdmAttribute("rgb 65535 512 256"), true);
		
		Element parent = doc.createElement("root");
		doc.appendChild(parent);
		new OpiColor(doc, parent, "color", c);
		XMLFileHandler.writeXML(doc);		
		
		XMLFileHandler.isColorElementEqual(null, 255, 2, 1, 0, 0, 0, "color", parent);
	}

	public void testDefinitionOpiColor() throws EdmException {
		
		// init document
		Document doc = XMLFileHandler.createDomDocument();
		
		EdmAttribute a = new EdmAttribute("\"blinking purple\"");
		a.appendValue("65535 512 256");
		EdmColor c = new EdmColor(a, true);
		
		Element parent = doc.createElement("root");
		doc.appendChild(parent);
		new OpiColor(doc, parent, "color", c);
		XMLFileHandler.writeXML(doc);		
		
		XMLFileHandler.isColorElementEqual("blinking purple", 255, 2, 1, 0, 0, 0, "color", parent);
	}
}
