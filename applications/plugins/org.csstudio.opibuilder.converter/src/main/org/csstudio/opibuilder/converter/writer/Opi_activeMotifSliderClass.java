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
import org.csstudio.opibuilder.converter.model.Edm_activeMotifSliderClass;

/**
 * XML conversion class for Edm_activeMotifSliderClasss
 * @author Matevz
 */
public class Opi_activeMotifSliderClass extends OpiWidget {

	private static Logger log = Logger.getLogger("org.csstudio.opibuilder.converter.writer.Opi_activeMotifSliderClass");
	private static final String typeId = "Ellipse";
	private static final String name = "EDM Ellipse";
	private static final String version = "1.0";

	/**
	 * Converts the Edm_activeMotifSliderClasss to OPI Rectangle widget XML.  
	 */
	public Opi_activeMotifSliderClass(Context con, Edm_activeMotifSliderClass r) {
		super(con, r);
		setTypeId(typeId);

		context.getElement().setAttribute("version", version);
		
		new OpiString(context, "name", name);
		
		if(r.getAttribute("controlPv").isInitialized())
			new OpiString(context, "pv_name", r.getControlPv());
		
		new OpiColor(context, "foreground_color", r.getFgColor());
		new OpiColor(context, "background_color", r.getBgColor());

		log.debug("Edm_activeMotifSliderClass written.");

	}
	
	protected void setDefaultPropertyValue(){
		super.setDefaultPropertyValue();		
		new OpiBoolean(context, "transparent", true);
	}

}
