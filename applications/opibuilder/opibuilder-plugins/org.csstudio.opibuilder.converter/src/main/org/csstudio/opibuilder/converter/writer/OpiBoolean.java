/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter.writer;

import org.csstudio.opibuilder.converter.model.EdmBoolean;

/**
 * XML output class for EdmBoolean type.
 * @author Matevz
 */
public class OpiBoolean extends OpiAttribute {

	/**
	 * Creates an element <name>booleanValue</name> with the given EdmBoolean value.
	 */
	public OpiBoolean(Context con, String name, EdmBoolean b) {
		this(con, name, b.is());
	}
	
	/**
	 * Creates an element <name>booleanValue</name> with the given boolean value.
	 */
	public OpiBoolean(Context con, String name, boolean b) {
		super(con, name);
		propertyContext.getElement().appendChild(con.getDocument().createTextNode(String.valueOf(b)));
	}
}
