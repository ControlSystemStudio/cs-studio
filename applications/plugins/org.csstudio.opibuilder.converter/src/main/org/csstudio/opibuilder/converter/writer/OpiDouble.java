/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter.writer;

import org.csstudio.opibuilder.converter.model.EdmDouble;

/**
 * XML output class for EdmDouble type.
 * @author Matevz
 */
public class OpiDouble extends OpiAttribute {

	/**
	 * Creates an element <name>doubleValue</name> with the given EdmDouble value.
	 */
	public OpiDouble(Context con, String name, EdmDouble d) {
		this(con, name, d.get());
	}

	/**
	 * Creates an element <name>doubleValue</name> with the given double value.
	 */
	public OpiDouble(Context con, String name, double d) {
		super(con, name);
		propertyContext.getElement().appendChild(con.getDocument().createTextNode(String.valueOf(d)));
	}

}
