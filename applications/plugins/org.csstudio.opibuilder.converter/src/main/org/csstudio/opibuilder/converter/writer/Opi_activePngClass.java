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
 * @author Matevz
 */
public class Opi_activePngClass extends OpiWidget {

	private static Logger log = Logger.getLogger("org.csstudio.opibuilder.converter.writer.Opi_activePngClass");
	private static final String typeId = "Image";
	private static final String name = "EDM Rectangle";
	private static final String version = "1.0";

	/**
	 * Converts the Edm_activePngClass to OPI Rectangle widget XML.  
	 */
	public Opi_activePngClass(Context con, Edm_activePngClass r) {
		super(con, r);
		setTypeId(typeId);

		context.getElement().setAttribute("version", version);
		
		new OpiString(context, "name", name);
		
		
		if(r.getAttribute("file").isInitialized())
			new OpiString(context, "image_file", r.getFile());	

		log.debug("Edm_activePngClass written.");

	}
	

}
