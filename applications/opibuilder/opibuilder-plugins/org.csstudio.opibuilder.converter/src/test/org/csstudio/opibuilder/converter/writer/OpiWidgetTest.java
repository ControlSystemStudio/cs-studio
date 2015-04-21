/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter.writer;

import junit.framework.TestCase;

import org.csstudio.opibuilder.converter.model.EdmEntity;
import org.csstudio.opibuilder.converter.model.EdmException;
import org.csstudio.opibuilder.converter.model.EdmWidget;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class OpiWidgetTest extends TestCase {

	public void testOpiWidget() throws EdmException {
		
		// init document
		Document doc = XMLFileHandler.createDomDocument();
		
		String widgetType = "newWidget";
		EdmWidget edmWidget = new EdmWidget(new EdmEntity(widgetType));
		
		Element parent = doc.createElement("root");
		doc.appendChild(parent);
		Context context = new Context(doc, parent, null, 0, 0);
		new OpiWidget(context, edmWidget);
		
		// Input tests here when there will be some implementation.
		
		//XMLFileHandler.writeXML(doc);
	}
}
