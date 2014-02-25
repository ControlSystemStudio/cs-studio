/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter.writer;

import org.csstudio.opibuilder.converter.model.Edm_TextentryClass;

/**
 * XML conversion class for Edm_TextentryClass.
 * @author Xihui Chen
 */
public class Opi_TextentryClass extends Opi_TextupdateClass {

	private static final String typeId = "TextInput";	
	private static final String name = "EDM Text Entry";
	private static final String version = "1.0";

	/**
	 * Converts the Edm_TextupdateClass to OPI TextUpdate widget XML.  
	 */
	public Opi_TextentryClass(Context con, Edm_TextentryClass t) {
		super(con,t);
		setTypeId(typeId);
		setName(name);
		setVersion(version);		
		new OpiInt(widgetContext, "style", 0);
		new OpiInt(widgetContext, "border_style", 3);
		
	}
	
}
