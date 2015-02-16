/*******************************************************************************
* Copyright (c) 2010-2014 ITER Organization.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
******************************************************************************/
package org.csstudio.alarm.beast.notifier.model;

import org.csstudio.alarm.beast.client.AADataStructure;

/**
 * Automated action command validator interface.
 * @author Fred Arnaud (Sopra Group)
 *
 */
public interface IActionValidator 
{
	/**
	 * Initialize the validator with automated action details
	 * @param details from {@link AADataStructure}
	 */
	public void init(String details);
	
	/** @return <code>true</code> if the command is valid.
	 * @throws Exception
	 */
	public boolean validate() throws Exception;
	
	/** Get the {@link IActionHandler} used to parse details */
	public IActionHandler getHandler();
}
