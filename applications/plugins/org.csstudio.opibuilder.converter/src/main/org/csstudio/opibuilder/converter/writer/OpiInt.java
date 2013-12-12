/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter.writer;

import org.csstudio.opibuilder.converter.model.EdmInt;

/**
 * XML output class for EdmInt type.
 * @author Matevz
 */
public class OpiInt extends OpiAttribute {

	/**
	 * Creates an element <name>intValue</name> with the given EdmInt value.
	 */
	public OpiInt(Context con, String name, EdmInt i) {
		this(con, name, i.get());
	}
	
	/**
	 * Creates an element <name>intValue</name> with the given int value.
	 */
	public OpiInt(Context con, String name, int i) {
		super(con, name);
		propertyContext.getElement().appendChild(con.getDocument().createTextNode(String.valueOf(i)));
	}
}
