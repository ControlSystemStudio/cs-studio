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
import org.csstudio.opibuilder.converter.model.Edm_activeBarClass;

/**
 * XML conversion class for Edm_activeRectangleClass
 * @author Matevz
 */
public class Opi_activeBarClass extends OpiWidget {

	private static Logger log = Logger.getLogger("org.csstudio.opibuilder.converter.writer.Opi_activeBarClass");
	private static final String typeId = "progressbar";
	private static final String name = "EDM progressbar";
	private static final String version = "1.0";

	/**
	 * Converts the Edm_activeRectangleClass to OPI Rectangle widget XML.  
	 */
	public Opi_activeBarClass(Context con, Edm_activeBarClass r) {
		super(con, r);
		setTypeId(typeId);

		context.getElement().setAttribute("version", version);		
		new OpiString(context, "name", name);
		
		if(r.getAttribute("showScale").isInitialized())
			new OpiBoolean(context, "show_scale", false);
		else
			new OpiBoolean(context, "show_scale", true);
		
		if(r.getAttribute("border").isInitialized())
			new OpiInt(context, "border_width", 1);
		else
			new OpiInt(context, "border_width", 0);
		
		if(r.getAttribute("indicatorPv").isInitialized())
			new OpiString(context, "pv_name", r.getIndicatorPv());
		new OpiColor(context, "fill_color", r.getIndicatorColor());
		
		if(r.getOrientation().equals("vertical"))
			new OpiBoolean(context, "horizontal", true);
		else
			new OpiBoolean(context, "horizontal", false);

		log.debug("Edm_activeBarClass written.");

	}

}

