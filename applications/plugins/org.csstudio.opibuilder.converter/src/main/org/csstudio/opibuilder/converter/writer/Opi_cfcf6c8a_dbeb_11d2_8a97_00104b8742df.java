/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter.writer;

import org.apache.log4j.Logger;
import org.csstudio.opibuilder.converter.model.Edm_cfcf6c8a_dbeb_11d2_8a97_00104b8742df;

/**
 * XML conversion class for EDM gif widget
 * @author Matevz
 */
public class Opi_cfcf6c8a_dbeb_11d2_8a97_00104b8742df extends OpiWidget {

	private static Logger log = Logger.getLogger("org.csstudio.opibuilder.converter.writer.Opi_activePngClass");
	private static final String typeId = "Image";
	private static final String name = "EDM Rectangle";
	private static final String version = "1.0";

	/**
	 * Converts the Edm_activePngClass to OPI Rectangle widget XML.  
	 */
	public Opi_cfcf6c8a_dbeb_11d2_8a97_00104b8742df(Context con, Edm_cfcf6c8a_dbeb_11d2_8a97_00104b8742df r) {
		super(con, r);
		setTypeId(typeId);
		setName(name);
		setVersion(version);
		
		
		if(r.getAttribute("file").isExistInEDL())
			new OpiString(widgetContext, "image_file", r.getFile());	

		log.debug("Edm_activePngClass written.");

	}
	

}
