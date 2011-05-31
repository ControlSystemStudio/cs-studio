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
		
		context.getElement().setAttribute("version", version);
		
//		new OpiInt(context, "x", g.getX() - context.getX());
//		new OpiInt(context, "y", g.getY() - context.getY());
//		new OpiInt(context, "width", g.getW());
//		new OpiInt(context, "height", g.getH());

		new OpiInt(context, "border_style", 0);
		new OpiBoolean(context, "show_scrollbar", false);
		// Set absolute position in context.
		context.setX(g.getX());
		context.setY(g.getY());
		
		OpiWriter.writeWidgets(context, g.getWidgets());
		
		log.debug("Edm_activeGroupClass written.");
	}
}
