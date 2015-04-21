/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter.writer;

import org.apache.log4j.Logger;
import org.csstudio.opibuilder.converter.model.Edm_activeXTextDspClass;

/**
 * XML conversion class for Edm_activeXTextDspClass
 * @author Xihui Chen
 */
public class Opi_activeXTextDspClass extends Opi_activeXTextDspClass_noedit {

	private static Logger log = Logger.getLogger("org.csstudio.opibuilder.converter.writer.Opi_activeXTextDspClass");
	private static final String typeId = "TextInput";
	private static final String name = "EDM TextInput";

	/**
	 * Converts the Edm_activeRectangleClass to OPI Rectangle widget XML.  
	 */
	public Opi_activeXTextDspClass(Context con, Edm_activeXTextDspClass r) {
		super(con, r);
		setName(name);
		if(r.isEditable()){
			setTypeId(typeId);
			if(r.isDate())
				new OpiInt(widgetContext, "selector_type", 2);
			if(r.isFile()){
				new OpiInt(widgetContext, "selector_type", 1);		
				new OpiInt(widgetContext, "file_source", 1);
				int f=0;
				if(r.getFileComponent().equals("nameAndExt"))
					f=1;
				else if(r.getFileComponent().equals("name"))
					f=2;
				new OpiInt(widgetContext, "file_return_part", f);
			}
		}
		

		log.debug("Edm_activeXTextDspClass written.");

	}

}

