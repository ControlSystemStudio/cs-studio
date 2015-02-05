/*******************************************************************************
* Copyright (c) 2010-2014 ITER Organization.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
******************************************************************************/
package org.csstudio.alarm.beast.notifier.model;

/**
 * Automated action details handler interface. 
 * @author Fred Arnaud (Sopra Group)
 *
 */
public interface IActionHandler {

	/** Parse automated action details */
	public void parse() throws Exception;
}
