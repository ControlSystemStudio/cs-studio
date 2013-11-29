/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter.writer;

import org.apache.log4j.Logger;
import org.csstudio.opibuilder.converter.model.Edm_activeMeterClass;

/**
 * XML conversion class for Edm_activeRectangleClass
 * @author Lei Hu, Xihui Chen
 */
public class Opi_activeMeterClass extends OpiWidget {

	private static Logger log = Logger.getLogger("org.csstudio.opibuilder.converter.writer.Opi_activeMeterClass");
	private static final String typeId = "meter";
	private static final String name = "EDM meter Hulei";
	private static final String version = "1.0";

	/**
	 * Converts the Edm_activeRectangleClass to OPI Rectangle widget XML.  
	 */
	public Opi_activeMeterClass(Context con, Edm_activeMeterClass r) {
		super(con, r);
		setTypeId(typeId);

		widgetContext.getElement().setAttribute("version", version);
		
		new OpiString(widgetContext, "name", name);
		new OpiString(widgetContext, "pv_name", convertPVName(r.getReadPv()));
		new OpiColor(widgetContext, "needle_color", r.getFgColor(), r);
		new OpiColor(widgetContext, "foreground_color", r.getScaleColor(), r);
		new OpiBoolean(widgetContext, "forecolor_alarm_sensitive", r.isScaleAlarm());
		new OpiDouble(widgetContext, "minimum", r.getScaleMin());
		new OpiDouble(widgetContext, "maximum", r.getScaleMax());
		
		if(r.isShowScale())
			new OpiBoolean(widgetContext, "show_scale", r.isShowScale());
		log.debug("Edm_activeMeterClass written.");

	}

}
