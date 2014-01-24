/*******************************************************************************
* Copyright (c) 2010-2014 ITER Organization.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
******************************************************************************/
package org.csstudio.alarm.beast.notifier.actions;

import java.util.LinkedList;
import java.util.List;

import org.csstudio.alarm.beast.notifier.AAData;
import org.csstudio.alarm.beast.notifier.ItemInfo;
import org.csstudio.alarm.beast.notifier.PVSnapshot;
import org.csstudio.alarm.beast.notifier.model.IActionHandler;
import org.csstudio.alarm.beast.notifier.model.IAutomatedAction;

public class AutomatedActionSequence implements IAutomatedAction {

	private List<IAutomatedAction> actions = null;

	public AutomatedActionSequence() {
		actions = new LinkedList<IAutomatedAction>();
	}

	public void add(IAutomatedAction action) {
		actions.add(action);
	}
	
	public int size() {
		return actions.size();
	}

	@Override
	public void init(ItemInfo item, AAData data, IActionHandler handler)
			throws Exception { }

	@Override
	public void execute(List<PVSnapshot> pvs) throws Exception {
		for (IAutomatedAction action : actions) {
			action.execute(pvs);
		}
	}

}
