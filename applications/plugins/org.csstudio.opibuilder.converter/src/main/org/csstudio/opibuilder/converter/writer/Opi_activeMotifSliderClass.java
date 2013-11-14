/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter.writer;

import org.apache.log4j.Logger;
import org.csstudio.opibuilder.converter.model.Edm_activeMotifSliderClass;

/**
 * XML conversion class for Edm_activeMotifSliderClasss
 * @author Xihui Chen
 */
public class Opi_activeMotifSliderClass extends Opi_activeSliderClass {

	private static Logger log = Logger.getLogger("org.csstudio.opibuilder.converter.writer.Opi_activeMotifSliderClass");
	private static final String name = "EDM Motif Slider";
	private static final String version = "1.0";

	/**
	 * Converts the Edm_activeMotifSliderClasss to OPI Rectangle widget XML.  
	 */
	public Opi_activeMotifSliderClass(Context con, Edm_activeMotifSliderClass r) {
		super(con, r);
		setVersion(version);
		setName(name);
		
		new OpiBoolean(widgetContext, "horizontal", 
				!(r.getOrientation() !=null && r.getOrientation().equals("vertical")));
		
		

		log.debug("Edm_activeMotifSliderClass written.");

	}

}
