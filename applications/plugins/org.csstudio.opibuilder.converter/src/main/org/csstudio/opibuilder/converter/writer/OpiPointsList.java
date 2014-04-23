/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter.writer;

import org.csstudio.opibuilder.converter.model.EdmPointsList;
import org.w3c.dom.Element;


/**
 * XML output class for OpiPointsList type.
 * @author Xihui Chen
 */
public class OpiPointsList extends OpiAttribute {

	/**
	 * Creates an element <name>intValue</name> with the given EdmInt value.
	 */
	public OpiPointsList(Context con, String name, EdmPointsList xPoints, EdmPointsList yPoints) {
		this(con, name, xPoints.get(), yPoints.get());
	}
	
	/**
	 * Creates an element <name>intValue</name> with the given int value.
	 */
	public OpiPointsList(Context con, String name, int[] x, int[] y) {
		super(con, name);
		
		for(int i=0; i<Math.min(x.length, y.length); i++){
			Element pointElement = propertyContext.getDocument().createElement("point");
			pointElement.setAttribute("x", ""+(x[i]-propertyContext.getX()));
			pointElement.setAttribute("y", ""+(y[i]-propertyContext.getY()));
			propertyContext.getElement().appendChild(pointElement);			
		}
	}
}
