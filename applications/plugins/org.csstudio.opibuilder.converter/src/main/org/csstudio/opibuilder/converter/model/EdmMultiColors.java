/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter.model;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * Specific class representing multi-EdmColor property.
 *
 * @author Xihui Chen
 *
 */
public class EdmMultiColors extends EdmAttribute {

	private static Logger log = Logger.getLogger("org.csstudio.opibuilder.converter.parser.EdmColor");

	
	private Map<String, EdmColor> edmColorMap;
	
	/**
	 * Constructor which parses EdmColor from general EdmAttribute value.
	 *
	 * @param genericEntity EdmAttribute containing general EdmColor data.
	 * @param required false if this attribute is optional, else true
	 * @throws EdmException if EdmAttribute contains invalid data.
	 */
	public EdmMultiColors(EdmAttribute genericEntity, boolean required) throws EdmException {
		super(genericEntity);

		setRequired(required);

		if (genericEntity == null || getValueCount() == 0) {
			if (isRequired()) {
				throw new EdmException(EdmException.REQUIRED_ATTRIBUTE_MISSING,
				"Trying to initialize a required attribute from null object.", null);
			} else {
				log.warn("Missing optional property.");
				return;
			}
		}
		edmColorMap = new HashMap<String, EdmColor>();
		
		for(int i=0; i<getValueCount(); i++){
			String[] vals = getValue(i).split("\\s+", 2);
			edmColorMap.put(vals[0], new EdmColor(new EdmAttribute(vals[1]), false));			
		}		

		setInitialized(true);
	}
	
	public Map<String, EdmColor> getEdmColorMap() {
		return edmColorMap;
	}

	
}
