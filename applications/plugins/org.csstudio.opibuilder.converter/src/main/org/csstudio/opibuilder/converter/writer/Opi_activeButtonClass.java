/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter.writer;

import org.apache.log4j.Logger;
import org.csstudio.opibuilder.converter.model.Edm_activeButtonClass;

/**
 * XML conversion class for Edm_activeRectangleClass
 * @author Matevz
 */
public class Opi_activeButtonClass extends OpiWidget {

	private static Logger log = Logger.getLogger("org.csstudio.opibuilder.converter.writer.Opi_activeButtonClass");
	private static final String typeId = "ActionButton";
	private static final String name = "EDM Button Hulei";
	private static final String version = "1.0";

	/**
	 * Converts the Edm_activeRectangleClass to OPI Rectangle widget XML.  
	 */
	public Opi_activeButtonClass(Context con, Edm_activeButtonClass r) {
		super(con, r);
		setTypeId(typeId);
		setName(name);
		widgetContext.getElement().setAttribute("version", version);
				
	
		if(r.getAttribute("controlPv").isExistInEDL())
			new OpiString(widgetContext, "pv_name", r.getControlPv());
		

		log.debug("Edm_activeButtonClass written.");

	}

}
