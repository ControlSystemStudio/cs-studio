/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter.writer;

import org.csstudio.opibuilder.converter.model.Edm_activeDynSymbolClass;

/**
 * XML conversion class for activeDynSymbolClass
 * @author Xihui Chen
 */
public class Opi_activeDynSymbolClass extends OpiWidget {

	private static final String typeId = "linkingContainer";
	private static final String name = "EDM Dynamic Symbol";
	private static final String version = "1.0";

	/**
	 * Converts the Edm_activeRectangleClass to OPI Rectangle widget XML.  
	 */
	public Opi_activeDynSymbolClass(Context con, Edm_activeDynSymbolClass r) {
		super(con, r);
		setTypeId(typeId);
		setVersion(version);
		setName(name);
		

		if(r.getFile()!=null)
		{
			String originPath = r.getFile();
			if (originPath.endsWith(".edl")) {
				originPath = originPath.replace(".edl", ".opi");
			} else
				originPath = originPath + ".opi";
			new OpiString(widgetContext, "opi_file", originPath);				
		}
		new OpiInt(widgetContext, "border_style", 0);
		new OpiString(widgetContext, "group_name", "0");
		createPVOutputRule(r, "sim://ramp(0,"+(r.getNumStates()-1)+",1,"+r.getRate()+")",
				"group_name", "\"\"+pvInt0", "DynamicSymbolRule");
		
		

	}

}

