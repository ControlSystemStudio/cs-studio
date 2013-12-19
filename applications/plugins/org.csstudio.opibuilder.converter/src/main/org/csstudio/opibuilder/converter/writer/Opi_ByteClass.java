/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter.writer;

import org.apache.log4j.Logger;
import org.csstudio.opibuilder.converter.model.Edm_ByteClass;

/**
 * XML conversion class for Edm_activeRectangleClass
 * @author Lei Hu, Xihui Chen
 */
public class Opi_ByteClass extends OpiWidget {

	private static Logger log = Logger.getLogger("org.csstudio.opibuilder.converter.writer.Opi_ByteClass");
	private static final String typeId = "bytemonitor";
	private static final String name = "EDM Byte";
	private static final String version = "1.0";

	/**
	 * Converts the Edm_activeRectangleClass to OPI Rectangle widget XML.  
	 */
	public Opi_ByteClass(Context con, Edm_ByteClass r) {
		super(con, r);
		setTypeId(typeId);
		setName(name);
		setVersion(version);
		
		new OpiBoolean(widgetContext, "horizontal", true);
		new OpiBoolean(widgetContext, "effect_3d", false);
		new OpiBoolean(widgetContext, "square_led", true);
		
		
		new OpiColor(widgetContext, "on_color", r.getOnColor(), r);
	
		new OpiColor(widgetContext, "off_color", r.getOffColor(), r);
	
		if(r.getControlPv() !=null){
			new OpiString(widgetContext, "pv_name", convertPVName(r.getControlPv()));
			createColorAlarmRule(r, convertPVName(r.getControlPv()), "on_color", "onColorAlarm", false);
		}
		new OpiBoolean(widgetContext, "bitReverse", r.getEndian() !=null && 
				r.getEndian().equals("little"));

		new OpiInt(widgetContext, "numBits", r.getNumBits()==0?16:r.getNumBits());
		
		new OpiInt(widgetContext, "startBit", r.getShift());
		
		log.debug("Edm_ByteClass written.");

	}

}
