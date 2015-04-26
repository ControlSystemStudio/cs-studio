/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter.writer;

import org.apache.log4j.Logger;
import org.csstudio.opibuilder.converter.model.Edm_activeRadioButtonClass;

/**
 * XML conversion class for Edm_activeRectangleClass
 * @author Lei Hu, Xihui Chen
 */
public class Opi_activeRadioButtonClass extends OpiWidget {

	private static Logger log = Logger.getLogger("org.csstudio.opibuilder.converter.writer.Opi_activeRadioButtonClass");
	private static final String typeId = "radioBox";
	private static final String name = "EDM Radio button";
	private static final String version = "1.0";

	/**
	 * Converts the Edm_activeRectangleClass to OPI Rectangle widget XML.  
	 */
	public Opi_activeRadioButtonClass(Context con, Edm_activeRadioButtonClass r) {
		super(con, r);
		setTypeId(typeId);
		setName(name);
		setVersion(version);
		
		if(r.getAttribute("controlPv").isExistInEDL())
		{
			new OpiString(widgetContext, "pv_name", convertPVName(r.getControlPv()));
			new OpiBoolean(widgetContext, "items_from_pv", true);
		}		
		new OpiColor(widgetContext, "background_color", r.getButtonColor(), r);
		new OpiColor(widgetContext, "selected_color", r.getSelectColor(), r);
		
		log.debug("Edm_activeRadioButtonClass written.");

	}

}

