/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter.writer;

import org.apache.log4j.Logger;
import org.csstudio.opibuilder.converter.model.Edm_activeUpdownButtonClass;

/**
 * XML conversion class for Opi_activeUpdownButtonClass.
 * 
 * @author Xihui Chen
 */
public class Opi_activeUpdownButtonClass extends OpiWidget {

	private static Logger log = Logger
			.getLogger("org.csstudio.opibuilder.converter.writer.Opi_activeUpdownButtonClass");
	private static final String typeId = "spinner";
	private static final String name = "EDM Up/Down Button";
	private static final String version = "1.0";

	/**
	 * Converts the Edm_activeRectangleClass to OPI Rectangle widget XML.
	 */
	public Opi_activeUpdownButtonClass(Context con, Edm_activeUpdownButtonClass r) {
		super(con, r);
		setTypeId(typeId);
		setName(name);
		setVersion(version);
		if(r.getControlPv()!=null)
			new OpiString(widgetContext, "pv_name", convertPVName(r.getControlPv()));
		if(r.getCoarseValue()!=0)
			new OpiDouble(widgetContext, "page_increment", r.getCoarseValue());
		if(r.getFineValue()!=0)
			new OpiDouble(widgetContext, "step_increment", r.getFineValue());
		if(r.getLabel()!=null)
			new OpiString(widgetContext, "tooltip", r.getLabel());
		new OpiBoolean(widgetContext, "limits_from_pv", r.isLimitsFromDb());
		new OpiDouble(widgetContext, "minimum", r.getScaleMin());
		new OpiDouble(widgetContext, "maximum", r.getScaleMax());		
		
		
		log.debug("Opi_activeUpdownButtonClass written.");

	}


}
