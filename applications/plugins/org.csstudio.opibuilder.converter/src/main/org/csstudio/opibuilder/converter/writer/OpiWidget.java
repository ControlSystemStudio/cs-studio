/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter.writer;

import org.csstudio.opibuilder.converter.model.EdmWidget;
import org.w3c.dom.Element;

/**
 * General class for outputting widgets.
 * @author Matevz
 */
public class OpiWidget {

	protected Context context;

	/**
	 * Creates element:
	 * 		<widget typeId="org.csstudio.opibuilder.widgets.type">
	 * 		</widget>
	 */
	public OpiWidget(Context con, EdmWidget r) {

		Element element = con.getDocument().createElement("widget");
		con.getElement().appendChild(element);
		
		// Move context to this object. 
		this.context = new Context(con.getDocument(), element, con.getX(), con.getY());
		setDefaultPropertyValue();
		new OpiInt(context, "x", r.getX() - context.getX());
		new OpiInt(context, "y", r.getY() - context.getY());
		new OpiInt(context, "width", r.getW()+1);
		new OpiInt(context, "height", r.getH()+1);
		
	}

	/**
	 * Sets the attribute typeId of the OPI widget with 'org.csstudio.opibuilder.widgets.' prefix.  
	 * @param typeId
	 */
	protected void setTypeId(String typeId) {
		context.getElement().setAttribute("typeId", "org.csstudio.opibuilder.widgets." + typeId);
	}
	
	protected void setDefaultPropertyValue(){
		new OpiBoolean(context, "border_alarm_sensitive", false);
	}
}
