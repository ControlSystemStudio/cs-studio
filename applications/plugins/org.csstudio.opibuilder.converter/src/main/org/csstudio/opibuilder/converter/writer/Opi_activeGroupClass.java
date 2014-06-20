/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter.writer;

import org.apache.log4j.Logger;
import org.csstudio.opibuilder.converter.model.Edm_activeGroupClass;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * XML conversion class for Edm_activeGroupClass
 * @author Matevz
 */
public class Opi_activeGroupClass extends OpiWidget {

	private static Logger log = Logger.getLogger("org.csstudio.opibuilder.converter.writer.Opi_activeGroupClass");
	private static final String typeId = "groupingContainer";
	private static final String version = "1.0";
	
	/**
	 * Converts the Edm_activeGroupClass to OPI groupingContainer widget XML.  
	 */
	public Opi_activeGroupClass(Context con, Edm_activeGroupClass g) {
		super(con, g);
		setTypeId(typeId);
		
		setVersion(version);
		NodeList childNodes = widgetContext.getElement().getParentNode().getChildNodes();
		int i=0;
		int j=-1;
		for(i=0; i<childNodes.getLength(); i++){
			if(childNodes.item(i) instanceof Element && ((Element)childNodes.item(i)).getAttribute("typeId").contains(typeId))
				j++;
			if(widgetContext==childNodes.item(i))
				break;		
		}
		setName(""+j);
		
		new OpiBoolean(widgetContext, "lock_children", true);
		new OpiInt(widgetContext, "border_style", 0);
		new OpiBoolean(widgetContext, "show_scrollbar", false);
		new OpiBoolean(widgetContext, "transparent", true);
		// Set absolute position in context.
		widgetContext.setX(g.getX());
		widgetContext.setY(g.getY());
		
		OpiWriter.writeWidgets(widgetContext, g.getWidgets());
		
		log.debug("Edm_activeGroupClass written.");
	}
}
