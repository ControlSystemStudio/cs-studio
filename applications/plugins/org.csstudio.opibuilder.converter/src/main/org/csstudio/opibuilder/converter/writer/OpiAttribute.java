/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter.writer;

import org.w3c.dom.Element;

/**
 * General OPI output class for EdmAttribute.
 * @author Matevz
*/
public class OpiAttribute {

	protected Context propertyContext;
	
	/**
	 * Appends an element with the given name to current element and
	 * sets the local context to this element.  
	 */
	public OpiAttribute(Context widgetContext, String name) {

		Element element = widgetContext.getDocument().createElement(name);
	    widgetContext.getElement().appendChild(element);
		
	    // Move context to this object. 
		this.propertyContext = new Context(widgetContext.getDocument(), element, widgetContext.getRootDisplay(), widgetContext.getX(), widgetContext.getY());
	}
}
