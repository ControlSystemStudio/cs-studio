/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter.writer;

import org.apache.log4j.Logger;
import org.csstudio.opibuilder.converter.model.Edm_activeMenuButtonClass;

/**
 * XML conversion class for Edm_activeRectangleClass
 * @author Matevz
 */
public class Opi_activeMenuButtonClass extends OpiWidget {

	private static Logger log = Logger.getLogger("org.csstudio.opibuilder.converter.writer.Opi_activeMenuButtonClass");
	private static final String typeId = "MenuButton";
	private static final String name = "EDM menu button";
	private static final String version = "1.0";

	/**
	 * Converts the Edm_activeRectangleClass to OPI Rectangle widget XML.  
	 */
	public Opi_activeMenuButtonClass(Context con, Edm_activeMenuButtonClass r) {
		super(con, r);
		setTypeId(typeId);
		setName(name);
		setVersion(version);
		
		if(r.getControlPv()!=null)
		{
			new OpiString(widgetContext, "pv_name", convertPVName(r.getControlPv()));
			new OpiBoolean(widgetContext, "actions_from_pv", true);
		}

		log.debug("Edm_activeMenuButtonClass written.");

	}

}

