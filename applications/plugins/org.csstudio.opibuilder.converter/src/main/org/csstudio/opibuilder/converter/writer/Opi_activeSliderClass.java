/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter.writer;

import org.apache.log4j.Logger;
import org.csstudio.opibuilder.converter.model.Edm_activeSliderClass;

/**
 * XML conversion class for Edm_activeSliderClasss
 * @author Matevz
 */
public class Opi_activeSliderClass extends OpiWidget {

	private static Logger log = Logger.getLogger("org.csstudio.opibuilder.converter.writer.Opi_activeSliderClass");
	private static final String typeId = "Ellipse";
	private static final String name = "EDM Ellipse";
	private static final String version = "1.0";

	/**
	 * Converts the Edm_activeSliderClasss to OPI Rectangle widget XML.  
	 */
	public Opi_activeSliderClass(Context con, Edm_activeSliderClass r) {
		super(con, r);
		setTypeId(typeId);
		setName(name);
		setVersion(version);
		
		if(r.getAttribute("controlPv").isInitialized())
			new OpiString(widgetContext, "pv_name", r.getControlPv());


		log.debug("Edm_activeSliderClass written.");

	}
	
	protected void setDefaultPropertyValue(){
		super.setDefaultPropertyValue();		
		new OpiBoolean(widgetContext, "transparent", true);
	}

}
