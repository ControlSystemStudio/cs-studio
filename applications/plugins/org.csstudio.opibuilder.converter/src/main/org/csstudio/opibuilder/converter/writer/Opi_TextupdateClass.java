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

		context.getElement().setAttribute("version", version);

		new OpiString(context, "name", name);


		new OpiString(context, "pv_name", t.getControlPv());
		
		new OpiColor(context, "foreground_color", t.getFgColor());
		new OpiColor(context, "background_color", t.getBgColor());
		
		new OpiBoolean(context, "transparent", !t.isFill());
		
		new OpiFont(context, "font", t.getFont());
		if (t.getAttribute("fontAlign").isInitialized()){
			int align = 0;
			if(t.getFontAlign().equals("right"))
				align = 2;
			else if(t.getFontAlign().equals("center"))
				align = 1;
			
			new OpiInt(context, "horizontal_alignment", align);
		}
			
		
		if (t.getAttribute("lineWidth").isInitialized()) { 
			new OpiInt(context, "border_width", t.getLineWidth());
			new OpiInt(context, "border_style", 1);
		}
		
		if(t.getAttribute("mode").isInitialized()){
			int mode = 0;
			if(t.getMode().equals("decimal"))
				mode =1;
			else if(t.getMode().equals("hex"))
				mode = 3;
			else if(t.getMode().equals("engineer") || t.getMode().equals("exp"))
				mode = 2;
			new OpiInt(context, "format_type", mode);
		}
		
		if(t.getAttribute("precision").isInitialized())
			new OpiInt(context, "precision", t.getPrecision());	
		
		boolean lineAlarm = t.getAttribute("lineAlarm").isInitialized() && t.isLineAlarm();
		new OpiBoolean(context, "border_alarm_sensitive", lineAlarm);
		
		boolean fgAlarm = t.getAttribute("fgAlarm").isInitialized() && t.isFgAlarm();
		new OpiBoolean(context, "forecolor_alarm_sensitive", fgAlarm);

		log.debug("Edm_TextupdateClass written.");
	}
	
	@Override
	protected void setDefaultPropertyValue() {
		super.setDefaultPropertyValue();
		new OpiInt(context, "horizontal_alignment", 0);

	}
	
}
