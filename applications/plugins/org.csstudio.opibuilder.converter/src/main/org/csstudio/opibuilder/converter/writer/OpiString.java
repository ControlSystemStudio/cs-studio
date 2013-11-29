/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter.writer;

import org.csstudio.opibuilder.converter.model.EdmString;

/**
 * XML output class for EdmString type.
 * @author Matevz
 */
public class OpiString extends OpiAttribute {

	/**
	 * Creates an element <name>stringValue</name> with the given EdmString value.
	 */
	public OpiString(Context con, String name, EdmString s) {
		this(con, name, s.get());
	}
	
	/**
	 * Creates an element <name>stringValue</name> with the given String value.
	 */
	public OpiString(Context con, String name, String s) {
		super(con, name);
		propertyContext.getElement().appendChild(con.getDocument().createTextNode(String.valueOf(s)));
	}
}
