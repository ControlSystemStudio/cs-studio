/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter.writer;

import org.apache.log4j.Logger;
import org.csstudio.opibuilder.converter.model.Edm_activePngClass;

/**
 * XML conversion class for Edm_activePngClass
 * @author Lei Hu, Xihui Chen
 */
public class Opi_activePngClass extends OpiWidget {

	private static Logger log = Logger.getLogger("org.csstudio.opibuilder.converter.writer.Opi_activePngClass");
	private static final String typeId = "Image";
	private static final String name = "EDM Png";
	private static final String version = "1.0";

	/**
	 * Converts the Edm_activePngClass to OPI Rectangle widget XML.  
	 */
	public Opi_activePngClass(Context con, Edm_activePngClass r) {
		super(con, r);
		setTypeId(typeId);
		setName(name);
		setVersion(version);		
		
		if(r.getAttribute("file").isExistInEDL()){
			String path = r.getFile();
			int i = path.indexOf('.');
			if(i==-1||i!=path.length()-4)
				path = path+".png";
			new OpiString(widgetContext, "image_file", path);
		}

		log.debug("Edm_activePngClass written.");

	}
	

}
