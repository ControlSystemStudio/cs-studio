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
		EdmWidget edmWidget = new EdmWidget(new EdmEntity(widgetType));
		
		Element parent = doc.createElement("root");
		doc.appendChild(parent);
		Context context = new Context(doc, parent, 0, 0);
		new OpiWidget(context, edmWidget);
		
		// Input tests here when there will be some implementation.
		
		//XMLFileHandler.writeXML(doc);
	}
}
