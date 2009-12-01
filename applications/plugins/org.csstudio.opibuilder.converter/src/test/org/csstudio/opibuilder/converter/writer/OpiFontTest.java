package org.csstudio.opibuilder.converter.writer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.csstudio.opibuilder.converter.model.EdmAttribute;
import org.csstudio.opibuilder.converter.model.EdmException;
import org.csstudio.opibuilder.converter.model.EdmFont;
import junit.framework.TestCase;

public class OpiFontTest extends TestCase {
	
    //<font fontName="Arial" height="14" style="0" />
	
	/*style conversion definition:
	0 - medium, reg
	1 - bold, reg
	2 - medui, italic
	3 - b, i
	*/
	
	public void testOpiFont() throws EdmException {
		
		// init document
		Document doc = XMLFileHandler.createDomDocument();
		
		String val = "helvetica-bold-r-14.0";
		EdmFont f = new EdmFont(new EdmAttribute(val), true);
		Element parent = doc.createElement("root");
		doc.appendChild(parent);
		new OpiFont(doc, parent, "font", f);
		
		XMLFileHandler.isFontElementEqual(val, "font", parent);
		
		XMLFileHandler.writeXML(doc);
		
	}
}
