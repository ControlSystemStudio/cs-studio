/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter.writer;

import org.apache.log4j.Logger;
import org.csstudio.opibuilder.converter.model.Edm_TextupdateClass;

/**
 * XML conversion class for Edm_TextupdateClass.
 * @author Matevz
 */
public class Opi_TextupdateClass extends OpiWidget {

	private static Logger log = Logger.getLogger("org.csstudio.opibuilder.converter.writer.Opi_TextupdateClass");
	private static final String typeId = "TextUpdate";	
	private static final String name = "EDM Text Update";
	private static final String version = "1.0";

	/**
	 * Converts the Edm_TextupdateClass to OPI TextUpdate widget XML.  
	 */
	public Opi_TextupdateClass(Context con, Edm_TextupdateClass t) {
		super(con,t);
		setTypeId(typeId);
		setName(name);
		setVersion(version);

		new OpiString(widgetContext, "pv_name", t.getControlPv());

		
		new OpiBoolean(widgetContext, "transparent", !t.isFill());
		
		
		if (t.getAttribute("fontAlign").isExistInEDL()){
			int align = 0;
			if(t.getFontAlign().equals("right"))
				align = 2;
			else if(t.getFontAlign().equals("center"))
				align = 1;
			
			new OpiInt(widgetContext, "horizontal_alignment", align);
		}
			
		
		if (t.getAttribute("lineWidth").isExistInEDL()) { 
			new OpiInt(widgetContext, "border_width", t.getLineWidth());
			new OpiInt(widgetContext, "border_style", 1);
		}
		
		if(t.getAttribute("mode").isExistInEDL()){
			int mode = 0;
			if(t.getDisplayMode().equals("decimal"))
				mode =1;
			else if(t.getDisplayMode().equals("hex"))
				mode = 3;
			else if(t.getDisplayMode().equals("engineer") || t.getDisplayMode().equals("exp"))
				mode = 2;
			new OpiInt(widgetContext, "format_type", mode);
		}
		
		if(t.getAttribute("precision").isExistInEDL())
			new OpiInt(widgetContext, "precision", t.getPrecision());	
		
		boolean lineAlarm = t.getAttribute("lineAlarm").isExistInEDL() && t.isLineAlarm();
		new OpiBoolean(widgetContext, "border_alarm_sensitive", lineAlarm);
		


		log.debug("Edm_TextupdateClass written.");
	}
	
	@Override
	protected void setDefaultPropertyValue() {
		super.setDefaultPropertyValue();
		new OpiInt(widgetContext, "horizontal_alignment", 0);

	}
	
}
