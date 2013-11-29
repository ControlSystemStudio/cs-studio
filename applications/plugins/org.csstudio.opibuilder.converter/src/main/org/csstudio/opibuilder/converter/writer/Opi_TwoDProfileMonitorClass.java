/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter.writer;

import org.apache.log4j.Logger;
import org.csstudio.opibuilder.converter.model.Edm_TwoDProfileMonitorClass;

/**
 * XML conversion class for Edm_TwoDProfileMonitorClass.
 * 
 * @author Xihui Chen
 */
public class Opi_TwoDProfileMonitorClass extends OpiWidget {

	private static Logger log = Logger
			.getLogger("org.csstudio.opibuilder.converter.writer.Edm_TwoDProfileMonitorClass");
	private static final String typeId = "intensityGraph";
	private static final String name = "EDM Edm_TwoDProfileMonitorClass";
	private static final String version = "1.0";

	/**
	 * Converts the Edm_TwoDProfileMonitorClass to OPI Rectangle widget XML.
	 */
	public Opi_TwoDProfileMonitorClass(Context con, Edm_TwoDProfileMonitorClass r) {
		super(con, r);
		setTypeId(typeId);
		setName(name);
		setVersion(version);
		
		new OpiInt(widgetContext, "maximum", 255);
		new OpiInt(widgetContext, "minimum", 0);
		new OpiBoolean(widgetContext, "rgb_mode", false);
		
		
		
		
		if(r.getDataPvStr()!=null)
			new OpiString(widgetContext, "pv_name", convertPVName(r.getDataPvStr()));
		
		if(r.isPvBasedDataSize() && r.getWidthPvStr() != null && r.getHeightPvStr() != null){			
			createPVOutputRule(r, convertPVName(r.getWidthPvStr()), "data_width", "pv0", "DataWidthRule");
			createPVOutputRule(r, convertPVName(r.getHeightPvStr()), "data_height", "pv0", "DataHeightRule");			
		}else{
			new OpiInt(widgetContext, "data_width", r.getDataWidth());
			new OpiInt(widgetContext, "data_height", Integer.parseInt(r.getHeightPvStr()));
		}

		log.debug("Edm_activeRectangleClass written.");

	}

}
