/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter.writer;

import org.csstudio.opibuilder.converter.model.Edm_activeIndicatorClass;

/**
 * XML conversion class for Edm_activeIndicatorClass
 * 
 * @author Xihui Chen
 */
public class Opi_activeIndicatorClass extends Opi_activeBarClass {

	private static final String name = "EDM Indicator";

	/**
	 * Converts the Edm_activeRectangleClass to OPI Rectangle widget XML.
	 */
	public Opi_activeIndicatorClass(Context con, Edm_activeIndicatorClass r) {
		super(con, r);
		setName(name);
		new OpiBoolean(widgetContext, "indicator_mode", true);		
	}

}
