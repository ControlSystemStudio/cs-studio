/*******************************************************************************
* Copyright (c) 2010-2014 ITER Organization.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
******************************************************************************/
package org.csstudio.opibuilder.widgets.symbol.bool;

/**
 * Monitor model for a Boolean Symbol Image widget.
 * 
 * @author SOPRA Group
 * 
 */
public class MonitorBoolSymbolModel extends CommonBoolSymbolModel {

	/**
	 * Type ID for Boolean Symbol Image Monitor widget
	 */
	private static final String ID = "org.csstudio.opibuilder.widgets.symbol.bool.BoolMonitorWidget";

	/**
	 * Initialize the properties when the widget is first created.
	 */
	public MonitorBoolSymbolModel() {
	}

	@Override
	public String getTypeID() {
		return ID;
	}


}
