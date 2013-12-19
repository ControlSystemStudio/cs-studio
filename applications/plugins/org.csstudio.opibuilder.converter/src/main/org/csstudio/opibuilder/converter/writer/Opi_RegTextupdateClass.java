/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter.writer;

import org.csstudio.opibuilder.converter.model.Edm_RegTextupdateClass;

/**
 * XML conversion class for Edm_TextupdateClass.
 * @author Matevz, Xihui Chen
 */
public class Opi_RegTextupdateClass extends Opi_TextupdateClass {

	private static final String name = "EDM Reg. Text Update";

	/**
	 * Converts the Edm_TextupdateClass to OPI TextUpdate widget XML.  
	 */
	public Opi_RegTextupdateClass(Context con, Edm_RegTextupdateClass t) {
		super(con,t);
		setName(name);
	}
	
}
