/*******************************************************************************
* Copyright (c) 2010-2014 ITER Organization.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
******************************************************************************/
package org.csstudio.alarm.beast.notifier.model;

import java.util.List;

import org.csstudio.alarm.beast.notifier.AAData;
import org.csstudio.alarm.beast.notifier.EActionStatus;
import org.csstudio.alarm.beast.notifier.ItemInfo;
import org.csstudio.alarm.beast.notifier.PVSnapshot;

/**
 * Interface for automated action.
 * Define standard methods to implement.
 * @author Fred Arnaud (Sopra Group)
 *
 */
public interface IAutomatedAction {

	/**
	 * Initialize the action.
	 */
	public void init(ItemInfo item, AAData auto_action, IActionHandler handler) throws Exception;
	
	/**  
	 * Method to be implemented with action specific code.
	 * Called by action thread after the delay if {@link EActionStatus} is still OK.
	 */
	public void execute(List<PVSnapshot> pvs) throws Exception;
	
}
