/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.notifier.history;

import org.csstudio.alarm.beast.notifier.ActionID;
import org.csstudio.alarm.beast.notifier.EActionStatus;

/**
 * History entry of an automated action identified by its {@link ActionID}.
 * 
 * @author Fred Arnaud (Sopra Group) - ITER
 * 
 */
public class ActionHistoryEntry {

	private final ActionID actionId;
	private final EActionStatus status;

	public ActionHistoryEntry(ActionID actionId, EActionStatus status) {
		this.actionId = actionId;
		this.status = status;
	}

	public ActionID getActionId() {
		return actionId;
	}

	public EActionStatus getStatus() {
		return status;
	}

}
