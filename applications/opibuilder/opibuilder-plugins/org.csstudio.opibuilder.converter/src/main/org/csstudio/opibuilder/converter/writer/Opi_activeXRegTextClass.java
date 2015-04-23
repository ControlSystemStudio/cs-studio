/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter.writer;

import org.csstudio.opibuilder.converter.model.Edm_activeXRegTextClass;

/**
 * XML conversion class for Edm_activeXRegTextClass.
 * @author Xihui Chen
 */
public class Opi_activeXRegTextClass extends Opi_activeXTextClass {
	

	private static final String name = "EDM RegText";
	
	/**
	 * Converts the Edm_activeXTextClass to OPI Label widget XML.  
	 */
	public Opi_activeXRegTextClass(Context con, Edm_activeXRegTextClass t) {
		super(con,t);
		
		setName(name);		
		
	}

}
