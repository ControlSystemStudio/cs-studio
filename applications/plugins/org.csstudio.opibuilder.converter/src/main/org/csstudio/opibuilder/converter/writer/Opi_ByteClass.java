/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter.writer;

import org.apache.log4j.Logger;
import org.csstudio.opibuilder.converter.model.EdmLineStyle;
import org.csstudio.opibuilder.converter.model.Edm_ByteClass;

/**
 * XML conversion class for Edm_activeRectangleClass
 * @author Matevz
 */
public class Opi_ByteClass extends OpiWidget {

	private static Logger log = Logger.getLogger("org.csstudio.opibuilder.converter.writer.Opi_ByteClass");
	private static final String typeId = "meter";
	private static final String name = "EDM Byte";
	private static final String version = "1.0";

	/**
	 * Converts the Edm_activeRectangleClass to OPI Rectangle widget XML.  
	 */
	public Opi_ByteClass(Context con, Edm_ByteClass r) {
		super(con, r);
		setTypeId(typeId);

		context.getElement().setAttribute("version", version);
		
		if (r.getOnColor().isInitialized()) {
			new OpiColor(context, "on_color", r.getOnColor());
		}
		if (r.getOffColor().isInitialized()) {
			new OpiColor(context, "off_color", r.getOffColor());
		}
		if(r.getAttribute("controlPv").isInitialized())
			new OpiString(context, "pv_name", r.getControlPv());
		if(r.getAttribute("endian").isInitialized())
			new OpiBoolean(context, "horizontal", false);
		if(r.getAttribute("numBits").isInitialized())
			new OpiInt(context, "numBits", r.getNumBits());
		if(r.getAttribute("shift").isInitialized())
			new OpiInt(context, "startBit", r.getShift());
		log.debug("Edm_ByteClass written.");

	}

}
