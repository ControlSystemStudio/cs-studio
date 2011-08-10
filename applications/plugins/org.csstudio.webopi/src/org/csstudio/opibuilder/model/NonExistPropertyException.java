/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.model;

/**
 * The exception shows that the property doesn't exist.
 * @author Xihui Chen
 *
 */
public class NonExistPropertyException extends RuntimeException {		
	private static final long serialVersionUID = 1L;
	private String propID;
	private String widgetName;
	public NonExistPropertyException(String widgetName, String propID) {
		this.propID = propID;
		this.widgetName = widgetName;
	}
	
	@Override
	public String getMessage() {
		return widgetName + " does not have property: " + propID;
	}
}
