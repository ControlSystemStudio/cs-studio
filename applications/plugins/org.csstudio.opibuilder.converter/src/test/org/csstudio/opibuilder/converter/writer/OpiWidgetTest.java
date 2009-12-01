package org.csstudio.opibuilder.converter.writer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.csstudio.opibuilder.converter.model.EdmEntity;
import org.csstudio.opibuilder.converter.model.EdmException;
import org.csstudio.opibuilder.converter.model.EdmWidget;
import junit.framework.TestCase;

public class OpiWidgetTest extends TestCase {

	public void testOpiWidget() throws EdmException {
		
		// init document
		Document doc = XMLFileHandler.createDomDocument();
		
		String widgetType = "newWidget";
		new EdmWidget(new EdmEntity(widgetType));
		
		Element parent = doc.createElement("root");
		doc.appendChild(parent);
		new OpiWidget(doc, parent, widgetType);
		
		// testing
		Element x = (Element)doc.getElementsByTagName("widget").item(0);
		assertTrue(x.hasAttribute("typeId"));
		assertEquals("org.csstudio.opibuilder.widgets." + widgetType, x.getAttribute("typeId"));
		
		XMLFileHandler.writeXML(doc);
		
	}
}
