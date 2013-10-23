/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter.writer;

import org.apache.log4j.Logger;
import org.csstudio.opibuilder.converter.model.EdmLineStyle;
import org.csstudio.opibuilder.converter.model.Edm_activeChoiceButtonClass;

/**
 * XML conversion class for Edm_activeRectangleClass
 * @author Matevz
 */
public class Opi_activeChoiceButtonClass extends OpiWidget {

	private static Logger log = Logger.getLogger("org.csstudio.opibuilder.converter.writer.Opi_activeChoiceButtonClass");
	private static final String typeId = "choiceButton";
	private static final String name = "EDM choice  Button";
	private static final String version = "1.0";

	/**
	 * Converts the Edm_activeRectangleClass to OPI Rectangle widget XML.  
	 */
	public Opi_activeChoiceButtonClass(Context con, Edm_activeChoiceButtonClass r) {
		super(con, r);
		setTypeId(typeId);

		context.getElement().setAttribute("version", version);		
		new OpiString(context, "name", name);
		
		if(r.getAttribute("controlPv").isInitialized())
		{
			new OpiString(context, "pv_name", r.getControlPv());
			new OpiBoolean(context, "items_from_pv", true);
		}
		if(r.getAttribute("fgColor").isInitialized())
			new OpiColor(context, "foreground_color", r.getFgColor());
		if(r.getAttribute("bgColor").isInitialized())
			new OpiColor(context, "background_color", r.getBgColor());
		if(r.isFgAlarm())
			new OpiBoolean(context, "border_alarm_sensitive", r.isFgAlarm());
		
		

		log.debug("Edm_activeChoiceButtonClass written.");

	}

}

